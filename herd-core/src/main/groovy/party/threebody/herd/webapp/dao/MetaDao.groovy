package party.threebody.herd.webapp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.skean.jdbc.ChainedJdbcTemplate

@Repository
class MetaDao {

    @Autowired
    ChainedJdbcTemplate cjt

    void releaseRepoSyncLock() {
        cjt.sql("update hd_meta set val='INACTIVE' where name='repo.sync.status'").execute()
    }

    boolean getRepoSyncLock() {
        def status = cjt.sql("select val from hd_meta where name='repo.sync.status'").firstCell()
        if (status) {
            if (status == 'PENDING') {
                return false
            } else {
                cjt.sql("update hd_meta set val='PENDING' where name='repo.sync.status'").execute()
                cjt.sql("select * from hd_meta where name='repo.sync.status' for update").first()
            }

        } else {
            cjt.sql("insert into hd_meta(name,val) values('repo.sync.status','PENDING')").execute()
            cjt.sql("select * from hd_meta where name='repo.sync.status' for update").first()

        }
        return true
    }
}
