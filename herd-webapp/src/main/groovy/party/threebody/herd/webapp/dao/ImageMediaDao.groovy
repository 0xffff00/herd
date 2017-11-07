package party.threebody.herd.webapp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.webapp.domain.ImageMedia
import party.threebody.skean.data.query.CriteriaAndSortingAndPaging
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.jdbc.rs.DualColsBean
import party.threebody.skean.jdbc.rs.TripleColsBean
import party.threebody.skean.web.mvc.dao.SinglePKJpaCrudDAO

import java.time.LocalDate

@Repository
class ImageMediaDao extends SinglePKJpaCrudDAO<ImageMedia, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }

    @Override
    List<ImageMedia> readList(CriteriaAndSortingAndPaging csp) {
        def sql = '''
SELECT *,(SELECT size FROM hd_media m WHERE m.hash=mi.hash) file_size
FROM hd_media_image mi
'''
        cjt.fromSql(sql).suite(csp).list(ImageMedia.class)
    }

    List<ImageMedia> listByHashs(Collection<String> hashs) {
        fromTable().by('hash').val([hashs]).list(ImageMedia.class)
    }

    List<DualColsBean<LocalDate, Integer>> countByDate() {
        def sql = '''
  SELECT DATE(exif_date_time),COUNT(*) 
  FROM hd_media_image 
  GROUP BY DATE(exif_date_time) 
  ORDER BY DATE(exif_date_time) DESC
'''
        cjt.sql(sql).listOfDualCols(LocalDate.class, Integer.class)
    }

    List<TripleColsBean<Integer, Integer, Integer>> countByMonth() {
        def sql = '''
  SELECT YEAR(exif_date_time),MONTH(exif_date_time),COUNT(*) 
  FROM hd_media_image 
  GROUP BY YEAR(exif_date_time),MONTH(exif_date_time) 
  ORDER BY YEAR(exif_date_time) DESC,MONTH(exif_date_time) DESC
'''
        cjt.sql(sql).listOfTripleCols(Integer.class, Integer.class, Integer.class)
    }

    List<DualColsBean<Integer, Integer>> countByYear() {
        def sql = '''  
  SELECT YEAR(exif_date_time),COUNT(*) 
  FROM hd_media_image 
  GROUP BY YEAR(exif_date_time)
  ORDER BY YEAR(exif_date_time) DESC
'''
        cjt.sql(sql).listOfDualCols(Integer.class, Integer.class)
    }
}
