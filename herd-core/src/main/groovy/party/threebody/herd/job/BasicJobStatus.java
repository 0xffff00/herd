package party.threebody.herd.job;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BasicJobStatus implements JobStatus {

    private LocalDateTime startTime;    //job's start time

    private AtomicInteger currentStep;      //current step index
    private String currentMessage;     //current message
    private LocalDateTime currentStartTime;
    private int totalSteps;

    private boolean broken;

    private Map<String, AtomicInteger> results;   //result tag -> count

    public BasicJobStatus(int totalSteps) {
        results = new HashMap<>(8);
        this.totalSteps = totalSteps;
        this.currentStep = new AtomicInteger();
    }

    @Override
    public int getCurrent() {
        return currentStep.get();
    }

    @Override
    public int getTotalSteps() {
        return totalSteps;
    }

    @Override
    public String getCurrentMessage() {
        return currentMessage;
    }

    public int next() {
        return next(null);
    }

    /**
     * @param message
     * @return currentStep
     */
    public int next(String message) {
        this.currentMessage = message;
        this.currentStartTime = LocalDateTime.now();
        return currentStep.incrementAndGet();
    }

    /**
     * check of current step as a resultTag
     */
    public void as(String resultTag) {
        AtomicInteger old = results.get(resultTag);
        if (old == null) {
            results.put(resultTag, new AtomicInteger(1));
        } else {
            old.incrementAndGet();
        }
    }


    public void asDone() {
        as(JobResult.DONE);
    }

    public void asOk() {
        as(JobResult.OK);
    }

    public void asFailed() {
        as(JobResult.FAILED);
    }

    public void asSkipped() {
        as(JobResult.SKIPPED);
    }

    public void asHalted(String fatalMessage) {
        as(JobResult.HALTED);
        currentMessage = fatalMessage;
        broken = true;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalDateTime getCurrentStartTime() {
        return currentStartTime;
    }

    public void setCurrentStartTime(LocalDateTime currentStartTime) {
        this.currentStartTime = currentStartTime;
    }


    /**
     * @return resultTag -> count
     */
    @Override
    public Map<String, Integer> getResults() {
        return results.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }

    public void setResults(Map<String, AtomicInteger> results) {
        this.results = results;
    }

    @Override
    public Category getCategory() {
        if (broken) {
            return Category.HALTED;
        }
        if (getCurrent() == 0) {
            return Category.INITIAL;
        }
        if (getCurrent() > getTotalSteps()) {
            return Category.COMPLETED;
        }
        return Category.RUNNING;
    }

}
