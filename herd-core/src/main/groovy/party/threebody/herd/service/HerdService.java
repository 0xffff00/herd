package party.threebody.herd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import party.threebody.herd.dao.ImageInfoDao;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.domain.MediaFile;
import party.threebody.herd.util.HerdFiles;
import party.threebody.herd.util.MediaType;
import party.threebody.skean.data.query.BasicCriterion;
import party.threebody.skean.data.query.Criteria;
import party.threebody.skean.jdbc.rs.DualColsBean;
import party.threebody.skean.jdbc.rs.TripleColsBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * MediaFile
 * Media
 *
 * @author hzk
 */
@Service
public class HerdService {

    private static final Logger logger = LoggerFactory.getLogger(HerdService.class);

    @Value("herd.localThumbnailRepoPath")
    String localThumbnailRepoPath;

    @Autowired ImageInfoDao imageInfoDao;
    @Autowired MediaFileDao mediaFileDao;

    //------- queries --------


    public List<DualColsBean<LocalDate, Integer>> countImageMediasByDate() {
        return imageInfoDao.countByDate();
    }

    public List<TripleColsBean<Integer, Integer, Integer>> countImageMediasByMonth() {
        return imageInfoDao.countByMonth();
    }

    public List<DualColsBean<Integer, Integer>> countImageMediasByYear() {
        return imageInfoDao.countByYear();
    }


    public MediaFile getMediaFileByFullPath(Path fullPath){
        return mediaFileDao
                .readOne(HerdFiles.toString(fullPath.getParent()),HerdFiles.toString(fullPath.getFileName()));
    }
    public byte[] getMediaFileContent(String hash, String cacheCategory) throws IOException {
        if (cacheCategory != null) {
            Path p = Paths.get(localThumbnailRepoPath,
                    cacheCategory, hash + "." + MediaType.JPEG.getSuffix());
            if (Files.isRegularFile(p)) {
                logger.debug("read file from CACHE: {}", p.toString());
                return Files.readAllBytes(p);
            }
        }
        MediaFile mediaFile = mediaFileDao.getByHash(hash);
        Path path = Paths.get(mediaFile.getFullPath());
        if (Files.exists(path)) {
            logger.debug("read file from ORIGIN: {}", path.toString());
            return Files.readAllBytes(path);
        }
        return null;
    }

    @Transactional
    public int truncateMediaFiles(String pathPrefix){
        int rna=mediaFileDao.deleteSomeByPathPrefix(pathPrefix);
        return rna;
    }

}
