package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.webapp.dao.ImageMediaDao;
import party.threebody.herd.webapp.domain.ImageMedia;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.skean.web.mvc.controller.SinglePKCrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKUriVarCrudRestController;

@RestController
@RequestMapping("/imageMedias")
public class ImageMediaRestController extends SinglePKUriVarCrudRestController<ImageMedia, String> {
    @Autowired
    HerdService herdService;

    @Autowired ImageMediaDao imageMediaDao;
    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<ImageMedia, String> builder) {
        builder.fromSinglePKCrudDAO(imageMediaDao);
    }


    @GetMapping("/countByDate")
    @ResponseBody
    public Object countImageMediasByDate() {
        return herdService.countImageMediasByDate();
    }

    @GetMapping("/countByMonth")
    @ResponseBody
    public Object countImageMediasByMonth() {
        return herdService.countImageMediasByMonth();
    }

    @GetMapping("/countByYear")
    @ResponseBody
    public Object countImageMediasByYear() {
        return herdService.countImageMediasByYear();
    }

}
