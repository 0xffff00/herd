package party.threebody.herd.webapp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.webapp.domain.RepoLogItem
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.web.mvc.dao.legacy.LegacyDualPKsJpaCrudDAO

import java.time.LocalDateTime

@Repository
class RepoLogItemDao extends LegacyDualPKsJpaCrudDAO<RepoLogItem, LocalDateTime, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }

}
