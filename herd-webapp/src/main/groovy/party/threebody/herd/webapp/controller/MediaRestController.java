package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.webapp.domain.Media;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.skean.web.mvc.controller.CrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKCrudRestController;

@RestController
@RequestMapping("/medias")
public class MediaRestController extends SinglePKCrudRestController<Media,String> {
    @Autowired
    HerdService herdService;

    @Override
    public void buildCrudFunctions(CrudFunctionsBuilder<Media, String> builder) {
        builder.listReader(herdService::listMedias)
                .countReader(herdService::countMedias);
    }
}
