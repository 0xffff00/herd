package party.threebody.herd.util;

import org.junit.Test;
import party.threebody.herd.domain.ImageInfo;

import java.nio.file.Paths;

public class ImageMetaUtilsTest {

    @Test
    public void parseExifInfo() throws Exception{
        ImageInfo imageInfo = ImageMetaUtils.parseExifInfo(
                Paths.get("C:\\Users\\hzk\\Pictures\\623\\623toall\\mmexport1498244563795.jpg"));

        System.out.println(imageInfo);

    }
}