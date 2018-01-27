package party.threebody.herd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.dao.MediaRepoDao;
import party.threebody.herd.domain.MediaRepo;
import party.threebody.skean.web.mvc.controller.SinglePKCrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKUriVarCrudRestController;

@RestController
@RequestMapping("media-repos")
public class MediaRepoController extends SinglePKUriVarCrudRestController<MediaRepo, String> {

    @Autowired MediaRepoDao mediaRepoDao;

    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<MediaRepo, String> builder) {
        builder.fromSinglePKCrudDAO(mediaRepoDao);
    }


}
