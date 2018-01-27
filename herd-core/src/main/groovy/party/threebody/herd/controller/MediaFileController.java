package party.threebody.herd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.domain.MediaFile;
import party.threebody.skean.web.mvc.controller.*;

@RestController
@RequestMapping("media-files")
public class MediaFileController extends QVarCrudRestController<MediaFile> {

    @Autowired MediaFileDao mediaFileDao;

    @Override
    public void buildCrudFunctions(CrudFunctionsBuilder<MediaFile> builder) {
        builder.fillFromCrudDAO(mediaFileDao);
    }

}
