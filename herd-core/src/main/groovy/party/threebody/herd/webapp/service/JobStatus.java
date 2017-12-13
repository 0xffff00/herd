package party.threebody.herd.webapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JobStatus {
    private AtomicInteger current;
    private int total;
    private String message;     //current message

    private Map<String, AtomicInteger> cntMap;   //cntTag -> cnt

    public JobStatus(int total) {
        cntMap = new HashMap<>(8);
        current = new AtomicInteger();
        this.total = total;
    }

    public int getCurrent() {
        return current.get();
    }

    public int getTotal() {
        return total;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFinished() {
        return getCurrent() > getTotal();
    }

    public int next() {
        return next(null);
    }

    public int next(String message) {
        this.message = message;
        return current.incrementAndGet();
    }

    public void checkOff(String cntTag) {
        AtomicInteger old = cntMap.get(cntTag);
        if (old == null) {
            cntMap.put(cntTag, new AtomicInteger(1));
        } else {
            old.incrementAndGet();
        }
    }

    public int checkOffAndNext(String cntTag, String message) {
        checkOff(cntTag);
        return next(message);
    }

    public int asDoneAndNext(String message) {
        return checkOffAndNext("done", message);
    }

    public int asOkayAndNext(String message) {
        return checkOffAndNext("okay", message);
    }

    public int asFailAndNext(String message) {
        return checkOffAndNext("fail", message);
    }

}
