package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import party.threebody.herd.webapp.domain.Media;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;
import party.threebody.herd.webapp.service.HerdService;
import party.threebody.herd.webapp.util.ImageConverter;
import party.threebody.skean.data.query.QueryParamsBuildUtils;
import party.threebody.skean.data.query.QueryParamsSuite;
import party.threebody.skean.data.result.Counts;
import party.threebody.skean.web.mvc.controller.ControllerUtils;
import party.threebody.skean.web.mvc.controller.CrudFunctionsBuilder;
import party.threebody.skean.web.mvc.controller.SinglePKCrudRestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repo")
public class RepoRestController extends SinglePKCrudRestController<Repo, String> {

    @Autowired
    HerdService herdService;

    @Override
    public void buildCrudFunctions(CrudFunctionsBuilder<Repo, String> builder) {
        builder.listReader(herdService::listRepos)
                .countReader(herdService::countRepos)
                .oneReader(herdService::getRepo)
                .creator(herdService::createRepo)
                .entireUpdater(herdService::updateRepo)
                .partialUpdater(herdService::partialUpdateRepo)
                .pkGetter(Repo::getName);

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
    public ResponseEntity<Object> actOnRepos(@RequestParam Map<String, String> reqestParamMap, @RequestParam("action") String action) {
        QueryParamsSuite qps = QueryParamsBuildUtils.buildQueryParamsSuiteByPLOx(
                reqestParamMap,
                Arrays.asList("name")
        );
        List<Repo> repos = herdService.listRepos(qps);
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
