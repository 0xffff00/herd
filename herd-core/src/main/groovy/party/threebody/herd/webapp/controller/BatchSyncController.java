package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import party.threebody.herd.job.JobStatus;
import party.threebody.herd.webapp.dao.RepoDao;
import party.threebody.herd.webapp.domain.Media;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.herd.webapp.service.BatchSyncService;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.herd.webapp.service.MediaPathSyncJob;
import party.threebody.herd.webapp.util.ImageConverter;
import party.threebody.skean.data.query.CriteriaAndSortingAndPaging;
import party.threebody.skean.data.result.Counts;
import party.threebody.skean.web.SkeanNotFoundException;
import party.threebody.skean.web.data.CriteriaBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("batch-sync")
public class BatchSyncController {
    @Autowired HerdService herdService;
    @Autowired RepoDao repoDao;
    @Autowired CriteriaBuilder criteriaBuilder;
    @Autowired BatchSyncService batchSyncService;
    @Autowired MediaPathSyncJob mediaPathSyncJob;


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("media-paths")
    public Object syncMediaPathsByRepo(@RequestParam String repoName) {
        Repo repo = repoDao.readOne(repoName);

        mediaPathSyncJob.setRepo(repo);
        mediaPathSyncJob.excute();
        return null;
    }

    @GetMapping("media-paths/status")
    public JobStatus getMediaPathSyncJobStatus() {
        return mediaPathSyncJob.getJobStatus();
    }
}
