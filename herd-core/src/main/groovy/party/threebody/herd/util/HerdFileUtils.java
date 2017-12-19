package party.threebody.herd.util;

import party.threebody.skean.misc.SkeanException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class HerdFileUtils {
    private HerdFileUtils() {
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
        } catch (IOException e) {
            throw new SkeanException("fail to visit: " + rootDirPath, e);
        }
    }


}
