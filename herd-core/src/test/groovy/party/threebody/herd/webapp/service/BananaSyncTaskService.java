package party.threebody.herd.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BananaSyncTaskService {

    static final Logger logger = LoggerFactory.getLogger(BananaSyncTaskService.class);
    private AtomicInteger cnt = new AtomicInteger();
    private String msg;

    public String getMsg() {
        return msg;
    }

    //@Async
    public void startSync(int size) {
        for (int i = 0; i < size; i++) {
            int delay = (int) Math.random() * 1000 + 100;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int x = cnt.incrementAndGet();
            msg = LocalDateTime.now().toString();
            logger.info("running... cnt={}, msg={}", x, msg);
        }
    }

    public int getCnt() {
        return cnt.get();
    }
}
