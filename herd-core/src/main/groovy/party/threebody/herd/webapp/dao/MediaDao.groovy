package party.threebody.herd.webapp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.webapp.domain.Media
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.jdbc.util.CriteriaUtils
import party.threebody.skean.web.mvc.dao.SinglePKJpaCrudDAO

import java.time.LocalDateTime

@Repository
class MediaDao extends SinglePKJpaCrudDAO<Media, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }

    List<Media> listAll() {
        def sql = '''
SELECT m.*,
       (SELECT p.path FROM hd_media_path p WHERE p.hash=m.hash LIMIT 1) path0Path
FROM hd_media m
'''
        cjt.sql(sql).list(Media.class)
    }


    List<Media> listBySyncTime(LocalDateTime syncTime) {
        def sql = '''
SELECT m.*,
       (SELECT p.path FROM hd_media_path p WHERE p.hash=m.hash LIMIT 1) path0Path
FROM hd_media m
WHERE sync_time=?
'''
        cjt.sql(sql).arg(syncTime).list(Media.class)
    }

    List<Media> listByHashs(Collection<String> hashs) {
        def hash_IN = CriteriaUtils.buildClauseOfInStrs('hash', hashs)
        def sql = """
SELECT m.*,
       (SELECT p.path FROM hd_media_path p WHERE p.hash=m.hash LIMIT 1) path0Path
FROM hd_media m
WHERE $hash_IN
"""
        cjt.sql(sql).list(Media.class)

    }


}
