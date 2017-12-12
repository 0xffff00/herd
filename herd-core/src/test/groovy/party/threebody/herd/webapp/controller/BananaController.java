package party.threebody.herd.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import party.threebody.herd.webapp.service.BananaSyncTaskService;
import party.threebody.skean.collections.Maps;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * http://carlmartensen.com/completablefuture-deferredresult-async
 * http://www.baeldung.com/spring-async
 * https://stackoverflow.com/questions/10899635/spring-mvc-how-to-get-progress-of-running-async-task
 */
@RestController
@RequestMapping("/banana")
public class BananaController {
    @RequestMapping
    public Future<String> get() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("not actually in the background");
        return future;
    }

    @GetMapping("1")
    public Future<String> get1(@RequestParam String input) {
        CompletableFuture<String> future = new CompletableFuture<>();
        return CompletableFuture.supplyAsync(() -> input + "...in the background");
    }

    @GetMapping("0")
    public DeferredResult<String> get(@RequestParam String input) {
        DeferredResult<String> deferredResult = new DeferredResult<>();

        new Thread(() -> {
            String apiResponse = callApi(input);
            deferredResult.setResult(apiResponse);
        }).start();

        return deferredResult;
    }

    String callApi(String str) {
        // restTemplate.invoke(...)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return str.toUpperCase();
    }

    @GetMapping("5")
    @Async
    public Future<String> asyncMethodWithReturnType() {
        System.out.println("Execute method asynchronously - "
                + Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
            return new AsyncResult<String>("hello world !!!!");
        } catch (InterruptedException e) {
            //
        }

        return null;
    }

    @Autowired BananaSyncTaskService catSyncTaskService;

    @GetMapping("cat/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String start(@RequestParam int max) {
        catSyncTaskService.startSync(max);
        return "aaaa";
    }

    @GetMapping("cat/status")
    public Map status() {
        return Maps.of(
                "cnt", catSyncTaskService.getCnt(),
                "msg", catSyncTaskService.getMsg()
        );
    }

}
