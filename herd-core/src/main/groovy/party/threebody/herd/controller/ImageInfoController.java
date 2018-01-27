package party.threebody.herd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.dao.ImageInfoDao;
import party.threebody.herd.domain.ImageInfo;
import party.threebody.herd.service.HerdService;
import party.threebody.skean.web.mvc.controller.SinglePKCrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKUriVarCrudRestController;

@RestController
@RequestMapping("image-infos")
public class ImageInfoController extends SinglePKUriVarCrudRestController<ImageInfo, String> {
    @Autowired
    HerdService herdService;

    @Autowired ImageInfoDao imageInfoDao;

    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<ImageInfo, String> builder) {
        builder.fromSinglePKCrudDAO(imageInfoDao);
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
