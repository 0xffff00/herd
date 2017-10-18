package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import party.threebody.herd.webapp.service.HerdService;

import java.io.IOException;

@RestController
@RequestMapping("/herd")
public class HerdController {

    @Autowired
    HerdService herdService;


    @ResponseBody
    @GetMapping(value = "/pic2/{hash}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> testphoto(@PathVariable String hash,
                                            @RequestParam(name = "cache", required = false) String cacheCategory) {
        try {
            byte[] res = herdService.getMediaFileContent(hash, cacheCategory);
            if (res != null) {
                return ResponseEntity.ok().body(res);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
