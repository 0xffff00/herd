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
import party.threebody.herd.webapp.dao.MediaPathDao;
import party.threebody.herd.webapp.dao.RepoDao;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.skean.misc.SkeanException;
import party.threebody.skean.web.SkeanNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public abstract class MonoBatchSyncJob extends Job {
    static final Logger logger = LoggerFactory.getLogger(MonoBatchSyncJob.class);

    @Autowired BatchSyncService batchSyncService;

    abstract void execute0() throws Exception;

    @Transactional
    public void excute() {
        try {
            batchSyncService.takeRepoSyncLock();
            execute0();
        } catch (Exception e) {
            throw new SkeanException("excute job failed.", e);
        } finally {
            batchSyncService.releaseRepoSyncLock();
        }
    }
}
