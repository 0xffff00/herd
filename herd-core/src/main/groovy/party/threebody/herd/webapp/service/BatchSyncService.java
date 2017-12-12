package party.threebody.herd.webapp.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.skean.data.result.Count;
import party.threebody.skean.data.result.Counts;
import party.threebody.skean.lang.DateTimeFormatters;
import party.threebody.skean.misc.SkeanException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

@Service
public class BatchSyncService {


    /**
     * scan and hash all files in all repos, and record hash and path to MediaPath data
     * >>>data flow: Repo -> MediaPath
     *
     * @return MediaPath's Count
     */
    @Transactional
    public List<Count> synchonizeMediaPaths(List<Repo> repos, LocalDateTime syncTime) {
        logger.info("synchonizing MediaPaths @ {} ...", DateTimeFormatters.DEFAULT.format(syncTime));
        final long t0 = currentTimeMillis();
        final AtomicInteger totalFiles = new AtomicInteger();
        final AtomicInteger totalFilesToHash = new AtomicInteger();
        final AtomicInteger totalFilesToHashFailed = new AtomicInteger();
        final AtomicLong totalSizeInBytes = new AtomicLong();
        final List<MediaPath> mediaPathsToHashes = new ArrayList<>();
        final Set<String> oldPathStrs = listMediaPaths().stream().map(mp -> mp.getPath()).collect(Collectors.toSet());
        final Predicate<Path> fileFilter = p -> Files.isRegularFile(p) && !oldPathStrs.contains(p.toString());
        ;
        repos.forEach(repo -> {
            logger.info("scanning repo[{}] ...", repo.getName());
            Path pRoot = Paths.get(repo.getAbsPath());
            try {
                final int n = (int) Files.walk(pRoot).filter(fileFilter).count();
                totalFiles.addAndGet(n);
                AtomicInteger i = new AtomicInteger();
                AtomicInteger j = new AtomicInteger();
                Files.walk(pRoot).filter(fileFilter).forEach(path -> {
                    long t1 = currentTimeMillis();
                    try {
                        String hash = DigestUtils.sha1Hex(Files.newInputStream(path));
                        totalSizeInBytes.addAndGet(Files.size(path));
                        mediaPathsToHashes.add(new MediaPath(hash, path.toString(), null, repo.getName(), syncTime));
                        logger.info("[{}/{}] hashed, {}ms used : {} << {}",
                                i.incrementAndGet(), n, System.currentTimeMillis() - t1, hash, path);
                    } catch (IOException e) {
                        j.incrementAndGet();
                        createRepoLogWhenFail(syncTime, "syncMediaPaths.hash", "absPath", path.toString(), e);
                        logger.warn("[{}/{}] hash failed! {}ms used : {} ",
                                i.incrementAndGet(), n, System.currentTimeMillis() - t1, e.getMessage());
                    }
                });
                totalFiles.addAndGet(n);
                totalFilesToHash.addAndGet(i.get());
                totalFilesToHashFailed.addAndGet(j.get());
            } catch (IOException e) {
                throw new SkeanException("fail to scan repo: " + repo.getName(), e);
            }
        });
        logger.info("scanning finished: {} files found; {} bytes in all files.",
                totalFiles.get(), totalSizeInBytes.get());
        Count hashCnt = Counts.of("FileToHash")
                .completed(totalFilesToHash.get())
                .failed(totalFilesToHashFailed.get())
                .skipped(totalFiles.get() - totalFilesToHash.get()).since(t0);
        final long t1 = currentTimeMillis();
        int totalMediaPaths = mediaPathsToHashes.size();
        int totalMediaPathsCreated = mediaPathsToHashes.stream().map(mp -> {
            try {
                return mediaPathDao.create(mp);
            } catch (Exception e) {
                createRepoLogWhenFail(syncTime, "syncMediaPaths.createMP", "absPath", mp.getPath(), e);
                logger.warn("create MediaPath failed : " + mp.getPath(), e);
                return 0;
            }
        }).reduce((s, a) -> s + a).orElse(0);

        Count mpCnt = Counts.of("MediaPath")
                .created(totalMediaPathsCreated)
                .failed(totalMediaPaths - totalMediaPathsCreated).since(t1);
        createRepoLogByRepos(syncTime, "syncMediaPaths", repos, mpCnt);
        logger.info(mpCnt.toString());
        logger.info(mpCnt.toString());
        return Counts.merge(hashCnt, mpCnt);
    }


}
