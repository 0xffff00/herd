package party.threebody.herd.webapp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.webapp.domain.Repo
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.web.mvc.dao.SinglePKJpaCrudDAO

@Repository
class RepoDao extends SinglePKJpaCrudDAO<Repo, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }


    List<Repo> listByState(String state) {
        fromTable().by('state').val(state).list(Repo.class)
    }
}
