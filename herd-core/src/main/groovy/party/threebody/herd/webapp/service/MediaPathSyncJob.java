package party.threebody.herd.webapp.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import party.threebody.herd.job.Job;
import party.threebody.herd.job.JobStatus;
import party.threebody.herd.job.JobStepResult;
import party.threebody.herd.webapp.dao.*;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.skean.misc.SkeanException;
import party.threebody.skean.misc.SkeanInvalidArgumentException;
import party.threebody.skean.web.SkeanNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class MediaPathSyncJob extends MonoBatchSyncJob {
    static final Logger logger = LoggerFactory.getLogger(MediaPathSyncJob.class);
    @Autowired MediaPathDao mediaPathDao;
    @Autowired BatchSyncService batchSyncService;
    @Autowired RepoDao repoDao;
    Repo repo;
    List<MediaPath> oldMediaPaths;

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    private String syncMediaPathOfRepo(Path path) {
        Optional<MediaPath> opp = oldMediaPaths.stream()
                .filter(mp -> mp.getPath().equals(path.toString()))
                .findFirst();
        if (opp.isPresent()) {
            // no verify
            return JobStepResult.SKIPPED;
        }
        String hash = null;

        try {
            hash = DigestUtils.sha1Hex(Files.newInputStream(path));
        } catch (IOException e) {
            logger.warn("file hash failed", e);
            return JobStepResult.FAILED;
        }

        MediaPath mediaPath = new MediaPath(hash, path.toString(), null,
                repo.getName(), getJobStatus().getCurrentStartTime());
        mediaPathDao.create(mediaPath);
        return JobStepResult.OK;
    }




    @Override
    void execute0() throws Exception {
        if (repo == null) {
            throw new SkeanInvalidArgumentException("repo");
        }
        Path rootPath = Paths.get(repo.getAbsPath());
        this.oldMediaPaths = mediaPathDao.listByRepoNames(Arrays.asList(repo.getName()));
        List<Path> allFilePaths =Files.walk(rootPath).filter(Files::isRegularFile).collect(toList());
        final int n = (int) Files.walk(rootPath).filter(Files::isRegularFile).count();
        setJobStatus(new JobStatus(allFilePaths.size()));
        Files.walk(rootPath).filter(Files::isRegularFile)
                .forEach(p -> takeStep("同步文件路径：" + p, this::syncMediaPathOfRepo, p));
    }
}
