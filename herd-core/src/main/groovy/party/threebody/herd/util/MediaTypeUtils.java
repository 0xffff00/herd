package party.threebody.herd.util;

import org.apache.commons.io.FilenameUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MediaTypeUtils {
    private MediaTypeUtils() {
    }

    private static final Map<String, String> IMAGE_EXTS = new LinkedHashMap<String, String>() {{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("gif", "image/gif");
        put("png", "image/png");
        put("tiff", "image/tiff");
    }};


    public static String guessMimeTypeByPath(String path) {
        String ext = FilenameUtils.getExtension(path);
        String res= IMAGE_EXTS.get(ext.toLowerCase());
        if (res==null){
            return "unkwown";
        }
        return res;
    }

    public static boolean isImageFileByPath(String path) {
        return guessMimeTypeByPath(path).startsWith("image");
    }

}
