package party.threebody.herd.service;

import party.threebody.herd.job.ComposedLinarJob;
import party.threebody.herd.job.LinarJob;
import party.threebody.skean.misc.SkeanException;

import java.util.Arrays;

/**
 * exclusive job for batch-sync per database.
 * using Record Lock of MySQL InnoDB.
 */
public class MonoBatchSyncJob extends ComposedLinarJob {
    BatchSyncService batchSyncService;


    public static MonoBatchSyncJob of(BatchSyncService batchSyncService, LinarJob... children) {
        return new MonoBatchSyncJob(batchSyncService, children);
    }

    protected MonoBatchSyncJob(BatchSyncService batchSyncService, LinarJob... children) {
        super(Arrays.asList(children));
        this.batchSyncService = batchSyncService;
    }


    @Override
    public void start() {
        try {
            batchSyncService.takeRepoSyncLock();
            super.start();
            batchSyncService.releaseRepoSyncLock();
        } catch (SkeanException e) {
            halt(e.getMessage());
            throw e;
        } catch (RuntimeException e1) {
            halt(e1.getMessage());
            throw new SkeanException("do MonoBatchSyncJob failed.", e1);
        }

    }
}
