package party.threebody.herd.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.domain.MediaRepo
import party.threebody.skean.data.query.CriteriaAndSortingAndPaging
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

    @Override
    List<MediaRepo> readList(CriteriaAndSortingAndPaging csp) {
        def sql="""
SELECT r.*, 
(SELECT COUNT(*) FROM hd_me_file f WHERE f.dir_path LIKE CONCAT(r.path,'%')) file_cnt,
(SELECT COUNT(*) FROM hd_me_file f JOIN hd_me_file_image fi ON f.hash=fi.hash WHERE f.dir_path LIKE CONCAT(r.path,'%')) img_cnt
FROM hd_me_repo r
"""
        ensureCriteriaLegal(csp)
        //TODO https://github.com/0xffff00/skean/issues/10
        cjt.fromSql(sql).suite(csp).list(getEntityClass())
    }
}
