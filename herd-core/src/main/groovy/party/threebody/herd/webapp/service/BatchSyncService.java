package party.threebody.herd.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import party.threebody.herd.webapp.dao.*;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.skean.web.SkeanConflictException;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Service
public class BatchSyncService {
    @Autowired RepoDao repoDao;
    @Autowired MediaDao mediaDao;
    @Autowired MediaPathDao mediaPathDao;
    @Autowired RepoLogItemDao repoLogItemDao;
    @Autowired ImageMediaDao imageMediaDao;
    @Autowired MetaDao metaDao;


    /**
     * using Record Lock of MySQL InnoDB.
     */
    @Transactional
    public void takeRepoSyncLock() {
        boolean got = metaDao.getRepoSyncLock();
        if (!got) {
            throw new SkeanConflictException("Can not get Repo Sync Lock");
        }
    }

    @Transactional
    public void releaseRepoSyncLock() {
        metaDao.releaseRepoSyncLock();
    }


}
