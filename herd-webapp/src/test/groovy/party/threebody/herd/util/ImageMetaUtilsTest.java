package party.threebody.herd.util;

import org.junit.Test;
import party.threebody.herd.webapp.domain.ImageMedia;
import party.threebody.herd.webapp.util.ImageMetaUtils;

import java.nio.file.Paths;

public class ImageMetaUtilsTest {

    @Test
    public void parseExifInfo() throws Exception{
        ImageMedia imageMedia= ImageMetaUtils.parseExifInfo(
                Paths.get("C:\\Users\\hzk\\Pictures\\623\\623toall\\mmexport1498244563795.jpg"));

        System.out.println(imageMedia);

    }
}