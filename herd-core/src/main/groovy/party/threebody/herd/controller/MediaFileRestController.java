package party.threebody.herd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.dao.MediaFileDao;
import party.threebody.herd.domain.MediaFile;
import party.threebody.skean.web.mvc.controller.SinglePKCrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKUriVarCrudRestController;

@RestController
@RequestMapping("media-files")
public class MediaFileRestController extends SinglePKUriVarCrudRestController<MediaFile, String> {

    @Autowired MediaFileDao mediaFileDao;

    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<MediaFile, String> builder) {
        builder.fromSinglePKCrudDAO(mediaFileDao);
    }
}
