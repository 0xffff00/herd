package party.threebody.herd.job;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JobStatus {

    private LocalDateTime startTime;    //job's start time

    private AtomicInteger currentStep;      //current step index
    private String currentMessage;     //current message
    private LocalDateTime currentStartTime;
    private int totalSteps;


    private Map<String, AtomicInteger> cntMap;   //cntTag -> cnt

    public JobStatus(int totalSteps) {
        cntMap = new HashMap<>(8);
        this.totalSteps = totalSteps;
        this.currentStep = new AtomicInteger();
    }

    public int getCurrent() {
        return currentStep.get();
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public boolean isFinished() {
        return getCurrent() > getTotalSteps();
    }

    public int next() {
        return next(null);
    }

    /**
     * @param message
     * @return currentStep
     */
    public int next(String message) {
        this.currentMessage = message;
        this.currentStartTime = LocalDateTime.now();
        return currentStep.incrementAndGet();
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

    public void asDone() {
        checkOff(JobStepResult.DONE);
    }

    public void asOk() {
        checkOff(JobStepResult.OK);
    }

    public void asFailed() {
        checkOff(JobStepResult.FAILED);
    }

    public void asSkipped() {
        checkOff(JobStepResult.SKIPPED);
    }

    public int asDoneAndNext(String message) {
        asDone();
        return next(message);
    }

    public int asOkayAndNext(String message) {
        asOk();
        return next(message);
    }

    public int asFailAndNext(String message) {
        asFailed();
        return next(message);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCurrentStartTime() {
        return currentStartTime;
    }

    public void setCurrentStartTime(LocalDateTime currentStartTime) {
        this.currentStartTime = currentStartTime;
    }

    public Map<String, AtomicInteger> getCntMap() {
        return cntMap;
    }

    public void setCntMap(Map<String, AtomicInteger> cntMap) {
        this.cntMap = cntMap;
    }
}
