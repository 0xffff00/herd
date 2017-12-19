package party.threebody.herd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.domain.MediaFile;
import party.threebody.herd.job.BasicLinarJob;
import party.threebody.herd.util.HerdFileUtils;
import party.threebody.herd.util.ImageConverter;
import party.threebody.herd.util.MediaType;
import party.threebody.herd.util.MediaTypeUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static party.threebody.herd.job.JobResult.OK;
import static party.threebody.herd.job.JobResult.SKIPPED;

@Component
public class MakeImageThumbnailsJob extends BasicLinarJob<Path> {

    static final ImageConverter JPGC_1Kq5 = ImageConverter.toJPG().name("1Kq5")
            .edgeNoLessThan(720).edgeNoMoreThan(720 * 4)
            .compressQuality(0.5).noCompressIfBppBelow(0.12);
    static final ImageConverter JPGC_2Kq7 = ImageConverter.toJPG().name("2Kq7")
            .edgeNoLessThan(1440).edgeNoMoreThan(1440 * 4)
            .compressQuality(0.7).noCompressIfBppBelow(0.12);

    @Autowired BatchSyncService batchSyncService;
    @Autowired private MediaFileDao mediaFileDao;

    private Path destDirPath;
    private Path srcDirPath;
    private ImageConverter converter;

    public MakeImageThumbnailsJob() {
    }

    public void prepare(Path srcDirPath) {
        prepare(srcDirPath, JPGC_1Kq5);
    }

    public void prepare(Path srcDirPath, ImageConverter converter) {
        this.converter = converter;
        this.srcDirPath = srcDirPath;
        String localThumbnailRepoPath = batchSyncService.getLocalThumbnailRepoPath();
        destDirPath = Paths.get(localThumbnailRepoPath, converter.getName());
        HerdFileUtils.makeSureDirectoryExists(destDirPath);
    }

    @Override
    public String getName() {
        return "批量生成图像缩略图";
    }

    @Override
    public Collection<Path> getStepConsumers() {
        return HerdFileUtils.listAllFilesDeeply(srcDirPath);
    }

    @Override
    protected String takeStep(Path path) throws Exception {
        MediaFile mf = mediaFileDao.readOne(path.toString());
        if (!MediaTypeUtils.isImageFileByPath(path.toString())){
            return SKIPPED;
        }
        File srcFile = path.toFile();
        String fileName = mf.getHash() + "." + MediaType.JPEG.getSuffix();
        Path destPath = destDirPath.resolve(fileName);
        if (Files.exists(destPath)){
            return SKIPPED;
        }
        converter.convertToJPG(srcFile, destPath.toFile());
        return OK;
    }


    @Override
    protected String getStepText(Path consumer) {
        return "生成缩略图：" + consumer;
    }
}
