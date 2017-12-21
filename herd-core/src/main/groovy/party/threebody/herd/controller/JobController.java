package party.threebody.herd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.dao.MediaRepoDao;
import party.threebody.herd.domain.MediaRepo;
import party.threebody.herd.job.ComposedLinarJob;
import party.threebody.herd.job.JobStatus;
import party.threebody.herd.service.BatchSyncJob;
import party.threebody.herd.service.BatchSyncService;
import party.threebody.herd.service.HerdService;
import party.threebody.herd.service.MakeImageThumbnailsJob;
import party.threebody.skean.web.SkeanNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("jobs")
public class JobController {
    @Autowired HerdService herdService;
    @Autowired BatchSyncService batchSyncService;
    @Autowired MediaFileDao mediaFileDao;
    @Autowired MediaRepoDao mediaRepoDao;

    @Autowired BatchSyncJob batchSyncJob;
    @Autowired MakeImageThumbnailsJob makeImageThumbnailsJob;

    @Async
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("batch-sync")
    public void startBatchSyncJob(@RequestParam String repoName) {
        Path rootDirPath = getRepoRootDirPath(repoName);
        batchSyncJob.prepare(rootDirPath);
        batchSyncJob.start();
    }

    @GetMapping("batch-sync/status/all")
    public ComposedLinarJob.JobStatusVO getBatchSyncJobAllStatus() {
        return batchSyncJob.getAllStatus();
    }

    @GetMapping("batch-sync/status")
    public JobStatus getBatchSyncJobStatus() {
        return batchSyncJob.getStatus();
    }

    @GetMapping("image/thumbnails/status")
    public JobStatus getImageThumbnailsStatus1() {
        return makeImageThumbnailsJob.getStatus();
    }

    private Path getRepoRootDirPath(String repoName) {
        MediaRepo mediaRepo = mediaRepoDao.readOne(repoName);
        if (mediaRepo == null) {
            throw new SkeanNotFoundException("repoName:" + repoName);
        }
        return Paths.get(mediaRepo.getPath());
    }

    @DeleteMapping("media-repos")
    public Object truncateMediaRepo(@RequestParam String repoName) {
        MediaRepo mediaRepo = mediaRepoDao.readOne(repoName);
        return herdService.truncateMediaFiles(mediaRepo.getPath());
    }

}
