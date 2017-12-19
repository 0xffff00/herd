package party.threebody.herd.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.domain.MediaRepo
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.web.mvc.dao.SinglePKJpaCrudDAO

@Repository
class MediaRepoDao extends SinglePKJpaCrudDAO<MediaRepo, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }


    List<MediaRepo> listByState(String state) {
        fromTable().by('state').val(state).list(MediaRepo.class)
    }
}
