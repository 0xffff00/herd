package party.threebody.herd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import party.threebody.herd.dao.MetaDao;
import party.threebody.skean.web.SkeanConflictException;

/**
 *
 */
@Service
public class BatchSyncService {
    @Autowired MetaDao metaDao;

    @Value("${herd.localThumbnailRepoPath}")
    String localThumbnailRepoPath;

    public String getLocalThumbnailRepoPath() {
        return localThumbnailRepoPath;
    }

    /**
     * using Record Lock of MySQL InnoDB.
     */
    @Transactional
    public void takeRepoSyncLock() {
        boolean got = metaDao.obtainMediaSyncLock();
        if (!got) {
            throw new SkeanConflictException("Can not get Repo Sync Lock");
        }
    }

    @Transactional
    public int releaseRepoSyncLock() {
        return metaDao.releaseMediaSyncLock();
    }


}
