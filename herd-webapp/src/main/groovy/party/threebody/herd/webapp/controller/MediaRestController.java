package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.webapp.dao.MediaDao;
import party.threebody.herd.webapp.domain.Media;
import party.threebody.skean.web.mvc.controller.SinglePKCrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKUriVarCrudRestController;

@RestController
@RequestMapping("/medias")
public class MediaRestController extends SinglePKUriVarCrudRestController<Media, String> {
    @Autowired MediaDao mediaDao;

    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<Media, String> builder) {
        builder.fromSinglePKCrudDAO(mediaDao);
    }

}
