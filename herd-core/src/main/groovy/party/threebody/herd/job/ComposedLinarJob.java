package party.threebody.herd.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import party.threebody.skean.misc.SkeanException;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * a LinarJob composed by child LinarJobs
 */
public class ComposedLinarJob implements LinarJob {

    private BasicJobStatus rootStatus;
    private FlattenJobStatus flattenStatus;
    private List<? extends LinarJob> children;


    public static ComposedLinarJob of(LinarJob... children) {
        return new ComposedLinarJob(Arrays.asList(children));
    }

    protected ComposedLinarJob(List<? extends LinarJob> children) {
        setChildren(children);
    }

    private LinarJob getCurrentChild() {
        if (rootStatus == null) {
            return null;
        }
        int x = rootStatus.getCurrent() - 1;
        if (x < 0 || x >= children.size()) {
            return null;
        }
        return children.get(x);
    }

    public void setChildren(@NotNull List<? extends LinarJob> children) {
        this.children = children;
    }

    public void setChildren(LinarJob... children) {
        this.children = Arrays.asList(children);
    }

    public List<? extends LinarJob> getChildren() {
        return children;
    }

    @Override
    public void start() {
        if (rootStatus != null && rootStatus.isRunning()) {
            throw new SkeanException("fail to start since ComposedLinarJob has been running.");
        }
        rootStatus = new BasicJobStatus(getChildren().size());
        flattenStatus = null;
        rootStatus.setStartTime(LocalDateTime.now());
        for (LinarJob child : children) {
            rootStatus.next(child.getName());
            child.start();
            rootStatus.asDone();
        }
        rootStatus.next(null);
    }

    @Override
    public JobStatus getStatus() {
        return getFlattenStatus();
    }

    public JobStatus getFlattenStatus() {
        if (flattenStatus == null) {
            if (getRootStatus() != null && getChildren() != null) {
                flattenStatus = new FlattenJobStatus(rootStatus, getChildren());
            }
        }
        return flattenStatus;
    }

    public JobStatus getRootStatus() {
        return rootStatus;
    }

    public JobStatus getChildStatus() {
        LinarJob child = getCurrentChild();
        return (child == null) ? null : child.getStatus();
    }

    public JobStatusVO getAllStatus() {
        return new JobStatusVO(getRootStatus(), getFlattenStatus(), getChildStatus());
    }

    public static class JobStatusVO {
        private JobStatus root;
        private JobStatus flatten;
        private JobStatus child;
        private Map<String, Integer> flattenResults;

        public JobStatusVO(JobStatus root, JobStatus flatten, JobStatus child) {
            this.root = root;
            this.flatten = flatten;
            this.child = child;
        }

        public JobStatus getRoot() {
            return root;
        }

        public JobStatus getFlatten() {
            return flatten;
        }

        public JobStatus getChild() {
            return child;
        }
    }

    public static class FlattenJobStatus implements JobStatus {
        private int totalSteps;
        private JobStatus root;
        private List<JobStatus> children;

        public FlattenJobStatus(JobStatus root, List<? extends LinarJob> jobs) {
            this.root = root;
            this.children = jobs.stream()
                    .map(LinarJob::getStatus)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            this.totalSteps = this.children.stream()
                    .mapToInt(JobStatus::getTotalSteps)
                    .sum();
        }

        public Map<String, Integer> getResults() {
            int x = root.getCurrent();
            if (x <= 0) {
                return null;
            }
            if (x >= children.size()) {
                x = children.size() - 1;
            }
            return mergeResultMaps(children.subList(0, x).stream()
                    .map(JobStatus::getResults));
        }

        private static Map<String, Integer> mergeResultMaps(Stream<Map<String, Integer>> maps) {
            Map<String, Integer> res = new HashMap<>();
            maps.forEach(map -> {
                for (String k : map.keySet()) {
                    Integer v0 = res.get(k);
                    Integer v = map.get(k);
                    if (v0 == null) {
                        res.put(k, v);
                    } else {
                        res.put(k, v0 + v);
                    }
                }
            });
            return res;
        }

        @Override
        public int getCurrent() {
            int x = root.getCurrent();
            if (x <= 0) {
                return 0;
            }
            if (x > children.size()) {
                return children.stream()
                        .mapToInt(JobStatus::getTotalSteps)
                        .sum() + 1;
            }
            int y1 = children.subList(0, x - 1).stream()
                    .mapToInt(JobStatus::getTotalSteps)
                    .sum();
            int y2 = children.get(x - 1).getCurrent();
            return y1 + y2;
        }

        private JobStatus getCurrentChild() {
            int x = root.getCurrent() - 1;
            if (x < 0 || x >= children.size()) {
                return null;
            }
            return children.get(x);
        }

        @Override
        public int getTotalSteps() {
            return totalSteps;
        }

        @Override
        public String getCurrentMessage() {
            if (getCurrentChild() == null) {
                return null;
            }
            return getCurrentChild().getCurrentMessage();
        }

        @JsonIgnore
        @Override
        public LocalDateTime getStartTime() {
            if (root == null) {
                return null;
            }
            return root.getCurrentStartTime();
        }

        @JsonIgnore
        @Override
        public LocalDateTime getCurrentStartTime() {
            if (getCurrentChild() == null) {
                return null;
            }
            return getCurrentChild().getCurrentStartTime();
        }

    }
}
