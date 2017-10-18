package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.webapp.domain.ImageMedia;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.skean.web.mvc.controller.CrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKCrudRestController;
@RestController
@RequestMapping("/imageMedias")
public class ImageMediaRestController extends SinglePKCrudRestController<ImageMedia, String> {
    @Autowired
    HerdService herdService;

    @Override
    public void buildCrudFunctions(CrudFunctionsBuilder<ImageMedia, String> builder) {
        builder.listReader(herdService::listImageMedias)
                .countReader(herdService::countImageMedias);
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
