package party.threebody.herd.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import party.threebody.herd.dao.ImageInfoDao;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.domain.MediaFile;
import party.threebody.herd.job.BasicLinarJob;
import party.threebody.herd.job.UnitaryJob;
import party.threebody.herd.util.HerdFiles;
import party.threebody.herd.util.MediaTypeUtils;
import party.threebody.skean.collections.Maps;
import party.threebody.skean.web.SkeanNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static party.threebody.herd.job.JobResult.*;

/**
 * rootDirPath -> newPaths -> mediaPaths
 */
@Component
public class BatchSyncJob extends MonoBatchSyncJob {

    private static final Logger logger = LoggerFactory.getLogger(BatchSyncJob.class);


    private List<MediaFile> oldMediaFiles;
    private List<Path> newPaths;
    private List<MediaFile> deadMediaFiles;
    @Autowired MediaFileDao mediaFileDao;
    @Autowired ImageInfoDao imageInfoDao;

    @Autowired ParseMediaFilesJob parseMediaFilesJob;
    @Autowired MakeImageThumbnailsJob makeImageThumbnailsJob;

    @Autowired
    public BatchSyncJob(BatchSyncService batchSyncService) {
        super(batchSyncService);
    }

    public void prepare(Path rootDirPath) {
        if (rootDirPath == null) {
            throw new SkeanNotFoundException("rootDirPath missing");
        }
        setChildren(
                new UnitaryJob() {
                    @Override
                    public String takeStep() throws Exception {
                        oldMediaFiles = mediaFileDao.listByPathPrefix(rootDirPath.toString());
                        newPaths = HerdFiles.listAllFilesDeeply(rootDirPath);
                        deadMediaFiles = oldMediaFiles.stream()
                                .filter(mp -> !Files.exists(Paths.get(mp.getPath())))
                                .collect(Collectors.toList());
                        parseMediaFilesJob.prepare(rootDirPath);
                        makeImageThumbnailsJob.prepare(rootDirPath);
                        return null;
                    }

                    @Override
                    protected String getStepText(Object consumer) {
                        return "preparing";
                    }
                },
                new PutNewMediaFilesJob(),
                new DeleteDeadMediaFilesJob(),
                parseMediaFilesJob
                ,makeImageThumbnailsJob
        );
    }


    /**
     * file path newly scanned -> create or update MediaFile
     * put = create/update
     */
    public class PutNewMediaFilesJob extends BasicLinarJob<Path> {

        @Override
        public Collection<Path> getStepConsumers() {
            return newPaths;
        }

        @Override
        protected String takeStep(Path path) throws Exception {
            Thread.sleep(10);
            Optional<MediaFile> opmf = oldMediaFiles.stream()
                    .filter(mp -> mp.getPath().equals(path.toString()))
                    .findFirst();
            int fileSize = (int) Files.size(path);
            if (opmf.isPresent()) {
                // only verify file size
                boolean isSameSize = opmf.get().getSize() == fileSize;
                if (isSameSize) {
                    return SKIPPED;
                }
            }

            // calc hash
            String hash = null;
            try {
                hash = DigestUtils.sha1Hex(Files.newInputStream(path));
            } catch (IOException e) {
                logger.warn("file hash failed", e);
                return FAILED;
            }
            if (opmf.isPresent()) {  // update after re-hash
                mediaFileDao.partialUpdate(Maps.of("hash", hash), opmf.get().getPath());
            } else {  // create after hash
                MediaFile mf = new MediaFile();
                mf.setHash(hash);
                mf.setPath(HerdFiles.toString(path));
                mf.setSize(fileSize);
                mf.setSyncTime(LocalDateTime.now());
                mf.setMimeType(MediaTypeUtils.guessMimeTypeByPath(mf.getPath()));
                mediaFileDao.create(mf);
            }
            return OK;
        }

        @Override
        protected String getStepText(Path path) {
            return "更新文件记录：" + path.toString();
        }

    }

    public class DeleteDeadMediaFilesJob extends BasicLinarJob<MediaFile> {

        @Override
        public Collection<MediaFile> getStepConsumers() {
            return deadMediaFiles;
        }

        @Override
        protected String takeStep(MediaFile mediaPath) throws Exception {
            int rna = mediaFileDao.deleteByExample(mediaPath);
            return (rna > 0) ? OK : SKIPPED;
        }

        @Override
        protected String getStepText(MediaFile mediaPath) {
            return "移除已失效的文件记录：" + mediaPath.getPath();
        }
    }

}
