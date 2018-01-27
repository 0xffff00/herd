package party.threebody.herd.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.threebody.herd.domain.MediaFile;
import party.threebody.skean.misc.SkeanException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HerdFiles {

    static Logger logger = LoggerFactory.getLogger(HerdFiles.class);

    private HerdFiles() {
    }

    public static void makeSureDirectoryExists(Path dirPath) {
        if (!Files.isDirectory(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new SkeanException("cannot create directory", e);
            }
        }
    }

    public static List<Path> listAllFilesDeeply(Path rootDirPath) {
        try {
            return Files.walk(rootDirPath)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new SkeanException("fail to visit: " + rootDirPath,e);
        }
    }


    public static String toString(Path path) {
        return path.toString().replaceAll("\\\\", "/");
    }

    /**
     * judge mediaFile's file path is expected
     * @param mediaFile
     * @param expectedPath
     * @return
     */
    public static boolean pathEquals(MediaFile mediaFile,Path expectedPath){
        return Paths.get(mediaFile.getDirPath(),mediaFile.getFileName()).equals(expectedPath);
    }


}
