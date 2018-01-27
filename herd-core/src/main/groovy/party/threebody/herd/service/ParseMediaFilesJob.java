package party.threebody.herd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import party.threebody.herd.dao.ImageInfoDao;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.domain.ImageInfo;
import party.threebody.herd.domain.MediaFile;
import party.threebody.herd.job.BasicLinarJob;
import party.threebody.herd.util.HerdFiles;
import party.threebody.herd.util.ImageMetaUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static party.threebody.herd.job.JobResult.*;

@Component
public class ParseMediaFilesJob extends BasicLinarJob<Path> {
    @Autowired ImageInfoDao imageInfoDao;
    @Autowired HerdService herdService;
    Path rootDirPath;

    public ParseMediaFilesJob() {
    }

    public void prepare(Path rootDirPath) {
        this.rootDirPath = rootDirPath;
    }

    @Override
    public Collection<Path> getStepConsumers() {
        return HerdFiles.listAllFilesDeeply(rootDirPath);
    }

    @Override
    protected String takeStep(Path path) throws Exception {
        MediaFile mf = herdService.getMediaFileByFullPath(path);
        if (mf == null) {
            return FAILED;
        }
        String hash = mf.getHash();
        ImageInfo imageInfo = imageInfoDao.readOne(hash);
        if (imageInfo != null) {
            return SKIPPED;
        }

        if (mf.getMimeType()!=null && mf.getMimeType().startsWith("image")) {
            InputStream inputStream = Files.newInputStream(Paths.get(mf.getFullPath()));
            imageInfo = ImageMetaUtils.parseExifInfo(inputStream);
            imageInfo.setHash(hash);
            imageInfoDao.create(imageInfo);
            return OK;
        } else {
            // TODO support other mime type parsing
            return SKIPPED;
        }
    }

    @Override
    public String getName() {
        return "分析文件多媒体信息";
    }

    @Override
    protected String getStepText(Path path) {
        return "分析文件多媒体信息:" + path.toString();
    }
}
