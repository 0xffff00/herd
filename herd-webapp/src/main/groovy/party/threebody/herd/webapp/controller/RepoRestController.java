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

    @Autowired HerdService herdService;
    @Autowired RepoDao repoDao;

    @Autowired CriteriaBuilder criteriaBuilder;

    @Override
    public void buildCrudFunctions(SinglePKCrudFunctionsBuilder<Repo, String> builder) {
        builder.fromSinglePKCrudDAO(repoDao);
    }

    /**
     * [4:3,3:2,16:9]
     * Quarter 1k  [960,1080,1280]x720 0.7~0.9MP
     * Half    2k  [1920,2160,2560]x1440 2.4~3.7MP
     * Full    4k  [3840,4320,5120]x2880 11~15MP
     *
     * @param reqestParamMap
     * @param action
     * @return
     */
    @PostMapping("/advanced/repos")
    public ResponseEntity<Object> actOnRepos(@RequestParam MultiValueMap<String, String> reqestParamMap,
                                             @RequestParam("action") String action) {
        CriteriaAndSortingAndPaging csp = criteriaBuilder
                .toCriteriaAndSortingAndPaging(MultiValueMaps.toMap(reqestParamMap));
        List<Repo> repos = herdService.listRepos(csp);
        List<String> repoNames = repos.stream().map(Repo::getName).collect(Collectors.toList());
        List<MediaPath> mediaPaths = herdService.listMediaPathByRepoNames(repoNames);
        List<Media> medias = herdService.listMediasByRepoNames(repoNames);
        Object res = Counts.empty();
        switch (action) {
            case "sync":
                res = herdService.synchonizeAndAnalyzeAll(repos);
                break;
            case "sync.path":
                res = herdService.synchonizeMediaPaths(repos, LocalDateTime.now());
                break;
            case "sync.info.brief":
                res = herdService.synchonizeMedias(mediaPaths, LocalDateTime.now());
                break;
            case "sync.info.senior":
                res = herdService.analyzeMedias(medias, LocalDateTime.now());
                break;

            case "clear":
                res = herdService.clearAll();
                break;
            case "clear.path":
                res = herdService.clearMediaPaths(repos);
                break;
            case "clear.info.brief":
                res = herdService.clearMedias(repos);
                break;
            case "clear.info.senior":
                res = herdService.clearImageMedias(repos);
                break;

            case "convert2jpg.1Kq5":
                ImageConverter JPGC_1Kq5 = ImageConverter.toJPG().name("1Kq5")
                        .edgeNoLessThan(720).edgeNoMoreThan(720 * 4)
                        .compressQuality(0.5).noCompressIfBppBelow(0.12);
                res = herdService.convertToJpgByMedias(medias, LocalDateTime.now(), JPGC_1Kq5);
                break;
            case "convert2jpg.2Kq7":
                ImageConverter JPGC_2Kq7 = ImageConverter.toJPG().name("2Kq7")
                        .edgeNoLessThan(1440).edgeNoMoreThan(1440 * 4)
                        .compressQuality(0.7).noCompressIfBppBelow(0.12);
                res = herdService.convertToJpgByMedias(medias, LocalDateTime.now(), JPGC_2Kq7);
                break;
            default:
        }
        return ResponseEntity.badRequest().body(res);
    }
}
