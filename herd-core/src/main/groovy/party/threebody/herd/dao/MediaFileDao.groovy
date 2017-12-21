package party.threebody.herd.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.domain.MediaFile
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.web.mvc.dao.legacy.LegacySinglePKJpaCrudDAO

@Repository
class MediaFileDao extends LegacySinglePKJpaCrudDAO<MediaFile, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }

    MediaFile getByHash(String hash) {
        fromTable().by('hash').val(hash).first(MediaFile.class)
    }

//    List<String> listPathsByHash(String hash) {
//        cjt.sql("SELECT path FROM $table WHERE hash=?").arg(hash).listOfSingleColumn(String.class)
//    }


    List<MediaFile> listByPathPrefix(String pathPrefix) {
        def sql = "SELECT * FROM $table WHERE path LIKE ?"
        cjt.sql(sql).arg(pathPrefix + '%').list(MediaFile.class)
    }

//    List<MediaFile> listBySyncTime(LocalDateTime syncTime) {
//        fromTable().by('sync_time').val(syncTime).list(MediaFile.class)
//    }

    int deleteSomeByPathPrefix(String pathPrefix) {
        def sql = "DELETE FROM $table WHERE path LIKE ?"
        cjt.sql(sql).arg(pathPrefix + '%').execute()
    }

}

