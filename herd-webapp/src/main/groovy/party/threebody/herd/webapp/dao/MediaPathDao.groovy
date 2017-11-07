package party.threebody.herd.webapp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.webapp.domain.MediaPath
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.jdbc.util.CriteriaUtils
import party.threebody.skean.web.mvc.dao.DualPKsJpaCrudDAO
import party.threebody.skean.web.mvc.dao.legacy.LegacyDualPKsJpaCrudDAO

import java.time.LocalDateTime

@Repository
class MediaPathDao extends LegacyDualPKsJpaCrudDAO<MediaPath, String, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }


    List<String> listPathsByHash(String hash) {
        cjt.sql("SELECT path FROM hd_media_path WHERE hash=?").arg(hash).listOfSingleColumn(String.class)
    }

    List<MediaPath> listByRepoNames(List<String> repoNames) {
        fromTable().by('repo_name').val([repoNames]).list(MediaPath.class)
    }


    List<MediaPath> listBySyncTime(LocalDateTime syncTime) {
        fromTable().by('sync_time').val(syncTime).list(MediaPath.class)
    }

    int deleteByRepoNames(List<String> repoNames) {
        def repoName_IN = CriteriaUtils.buildClauseOfInStrs('repo_name', repoNames)
        def sql = "DELETE FROM hd_media_path WHERE $repoName_IN"
        cjt.sql(sql).execute()
    }


}

