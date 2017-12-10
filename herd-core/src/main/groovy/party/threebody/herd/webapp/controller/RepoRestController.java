package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import party.threebody.herd.webapp.dao.RepoDao;
import party.threebody.herd.webapp.domain.Media;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.herd.webapp.util.ImageConverter;
import party.threebody.skean.data.query.CriteriaAndSortingAndPaging;
import party.threebody.skean.data.result.Counts;
import party.threebody.skean.web.data.CriteriaBuilder;
import party.threebody.skean.web.mvc.MultiValueMaps;
import party.threebody.skean.web.mvc.controller.SinglePKCrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKUriVarCrudRestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repos")
public class RepoRestController extends SinglePKUriVarCrudRestController<Repo, String> {

    @Autowired RepoDao repoDao;

    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<Repo, String> builder) {
        builder.fromSinglePKCrudDAO(repoDao);
    }


}
