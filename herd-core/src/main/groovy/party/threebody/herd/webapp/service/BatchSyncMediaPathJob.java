package party.threebody.herd.webapp.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import party.threebody.herd.job.BasicLinarJob;
import party.threebody.herd.job.JobResult;
import party.threebody.herd.job.UnitaryJob;
import party.threebody.herd.webapp.dao.MediaPathDao;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.skean.web.SkeanNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BatchSyncMediaPathJob extends MonoBatchSyncJob {

    static final Logger logger = LoggerFactory.getLogger(BatchSyncMediaPathJob.class);
    Repo repo;
    private List<MediaPath> oldMediaPaths;
    private List<Path> newFilePaths;
    private List<MediaPath> deadMediaPaths;
    private MediaPathDao mediaPathDao;

    @Autowired
    public BatchSyncMediaPathJob(BatchSyncService batchSyncService, MediaPathDao mediaPathDao) {
        super(batchSyncService);
        this.mediaPathDao = mediaPathDao;
    }

    public void reset(Repo repo) {
        if (repo == null) {
            throw new SkeanNotFoundException("repo missing");
        }
        this.repo = repo;
        setChildren(
                new UnitaryJob() {
                    @Override
                    public String takeStep() throws Exception {
                        oldMediaPaths = mediaPathDao.listByRepoNames(Arrays.asList(repo.getName()));
                        Path rootPath = Paths.get(repo.getAbsPath());
                        newFilePaths = Files.walk(rootPath).filter(Files::isRegularFile).collect(Collectors.toList());
                        deadMediaPaths = oldMediaPaths.stream()
                                .filter(mp -> !Files.exists(Paths.get(mp.getPath())))
                                .collect(Collectors.toList());

                        return null;
                    }

                    @Override
                    protected String getStepText(Object consumer) {
                        return "preparing";
                    }
                },
                new PutNewMediaPathJob(),
                new DeleteDeadMediaPathJob()
        );
    }


    /**
     * file path newly scanned -> create or update MediaPath
     * put = create/update
     */
    public class PutNewMediaPathJob extends BasicLinarJob<Path> {

        @Override
        public Collection<Path> getStepConsumers() {
            return newFilePaths;
        }

        @Override
        protected String takeStep(Path path) throws Exception {
            Thread.sleep(10);
            Optional<MediaPath> opp = oldMediaPaths.stream()
                    .filter(mp -> mp.getPath().equals(path.toString()))
                    .findFirst();
            if (opp.isPresent()) {
                // no verify
                return JobResult.SKIPPED;
            }
            String hash = null;
            try {
                hash = DigestUtils.sha1Hex(Files.newInputStream(path));
            } catch (IOException e) {
                logger.warn("file hash failed", e);
                return JobResult.FAILED;
            }

            MediaPath mediaPath = new MediaPath(hash, path.toString(), null,
                    repo.getName(), getStatus().getCurrentStartTime());
            mediaPathDao.create(mediaPath);
            return JobResult.OK;
        }

        @Override
        protected String getStepText(Path consumer) {
            return "同步文件路径到媒体库：" + consumer.toString();
        }

    }

    public class DeleteDeadMediaPathJob extends BasicLinarJob<MediaPath> {

        @Override
        public Collection<MediaPath> getStepConsumers() {
            return deadMediaPaths;
        }

        @Override
        protected String takeStep(MediaPath mediaPath) throws Exception {
            int rna = mediaPathDao.deleteByExample(mediaPath);
            return (rna > 0) ? JobResult.OK : JobResult.SKIPPED;
        }

        @Override
        protected String getStepText(MediaPath mediaPath) {
            return "移除无效的文件路径：" + mediaPath.getPath();
        }
    }


}
