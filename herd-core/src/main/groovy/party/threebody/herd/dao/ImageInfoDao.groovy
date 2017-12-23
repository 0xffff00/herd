package party.threebody.herd.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import party.threebody.herd.domain.ImageInfo
import party.threebody.skean.data.query.CriteriaAndSortingAndPaging
import party.threebody.skean.jdbc.ChainedJdbcTemplate
import party.threebody.skean.jdbc.rs.DualColsBean
import party.threebody.skean.jdbc.rs.TripleColsBean
import party.threebody.skean.web.mvc.dao.legacy.LegacySinglePKJpaCrudDAO

import java.time.LocalDate

@Repository
class ImageInfoDao extends LegacySinglePKJpaCrudDAO<ImageInfo, String> {

    @Autowired
    ChainedJdbcTemplate cjt

    @Override
    ChainedJdbcTemplate getChainedJdbcTemplate() {
        return cjt
    }


    @Override
    List<ImageInfo> readList(CriteriaAndSortingAndPaging csp) {
        def sql = """
SELECT (SELECT size FROM hd_me_file  WHERE fi.hash=HASH LIMIT 1) file_size,
fi.*
FROM hd_me_file_image fi
"""
        cjt.fromSql(sql).suite(csp).list(ImageInfo.class)
    }

    List<ImageInfo> listByHashs(Collection<String> hashs) {
        fromTable().by('hash').val([hashs]).list(ImageInfo.class)
    }

    List<DualColsBean<LocalDate, Integer>> countByDate() {
        def sql = """
  SELECT DATE(exif_date_time),COUNT(*) 
  FROM $table 
  GROUP BY DATE(exif_date_time) 
  ORDER BY DATE(exif_date_time) DESC
"""
        cjt.sql(sql).listOfDualCols(LocalDate.class, Integer.class)
    }

    List<TripleColsBean<Integer, Integer, Integer>> countByMonth() {
        def sql = """
  SELECT YEAR(exif_date_time),MONTH(exif_date_time),COUNT(*) 
  FROM $table 
  GROUP BY YEAR(exif_date_time),MONTH(exif_date_time) 
  ORDER BY YEAR(exif_date_time) DESC,MONTH(exif_date_time) DESC
"""
        cjt.sql(sql).listOfTripleCols(Integer.class, Integer.class, Integer.class)
    }

    List<DualColsBean<Integer, Integer>> countByYear() {
        def sql = """
  SELECT YEAR(exif_date_time),COUNT(*) 
  FROM $table 
  GROUP BY YEAR(exif_date_time)
  ORDER BY YEAR(exif_date_time) DESC
"""
        cjt.sql(sql).listOfDualCols(Integer.class, Integer.class)
    }
}

