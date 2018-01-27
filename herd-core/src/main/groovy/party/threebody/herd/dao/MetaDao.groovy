package party.threebody.herd.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.skean.jdbc.ChainedJdbcTemplate

@Repository
class MetaDao {

     def table ='hd_meta'
     def mediaSyncStatus="media.sync.lock"
    @Autowired
    ChainedJdbcTemplate cjt

    int releaseMediaSyncLock() {
        cjt.sql("update $table set val='INACTIVE' where name='$mediaSyncStatus'").execute()
    }

    boolean obtainMediaSyncLock() {
        def status = cjt.sql("select val from $table where name='$mediaSyncStatus'").firstCell()
        if (status) {
            if (status == 'PENDING') {
                return false
            } else {
                cjt.sql("update $table set val='PENDING' where name='$mediaSyncStatus'").execute()
                cjt.sql("select * from $table where name='$mediaSyncStatus' for update").first()
            }

        } else {
            cjt.sql("insert into $table(name,val) values('$mediaSyncStatus','PENDING')").execute()
            cjt.sql("select * from $table where name='$mediaSyncStatus' for update").first()

        }
        return true
    }
}
