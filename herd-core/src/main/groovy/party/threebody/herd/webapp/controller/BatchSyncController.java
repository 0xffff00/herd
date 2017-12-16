package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import party.threebody.herd.job.ComposedLinarJob;
import party.threebody.herd.job.JobStatus;
import party.threebody.herd.webapp.dao.MediaPathDao;
import party.threebody.herd.webapp.dao.RepoDao;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.herd.webapp.service.BatchSyncMediaPathJob;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.skean.data.result.Count;
import party.threebody.skean.data.result.Counts;
import party.threebody.skean.web.data.CriteriaBuilder;

import java.util.Arrays;

@RestController
@RequestMapping("batch-sync")
public class BatchSyncController {
    @Autowired HerdService herdService;
    @Autowired RepoDao repoDao;
    @Autowired MediaPathDao mediaPathDao;
    @Autowired CriteriaBuilder criteriaBuilder;

    @Autowired BatchSyncMediaPathJob batchSyncMediaPathJob;

    @Async
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("media-paths")
    public void startBatchSyncMediaPathJob(@RequestParam String repoName) {
        Repo repo = repoDao.readOne(repoName);
        batchSyncMediaPathJob.reset(repo);
        batchSyncMediaPathJob.start();
    }

    @GetMapping("media-paths/status1")
    public ComposedLinarJob.JobStatusVO getBatchSyncMediaPathJobAllStatus() {
        return batchSyncMediaPathJob.getAllStatus();
    }

    @GetMapping("media-paths/status/flatten")
    public JobStatus getBatchSyncMediaPathJobFlattenStatus() {
        return batchSyncMediaPathJob.getFlattenStatus();
    }

    @DeleteMapping("media-paths")
    public Count batchDeleteMediaPaths(@RequestParam String repoName) {
        int rna = mediaPathDao.deleteByRepoNames(Arrays.asList(repoName));
        return Counts.deleted(rna);
    }


}
