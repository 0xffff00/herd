package party.threebody.herd.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import static party.threebody.herd.job.JobResult.*;

/**
 * this class offers friendly log outputs and status changing logic.
 *
 * @param <C>
 */
public abstract class BasicLinarJob<C> implements LinarJob {

    private static final Logger logger = LoggerFactory.getLogger(BasicLinarJob.class);

    private BasicJobStatus status;


    public BasicLinarJob() {
    }

    /**
     * each consumser maps a step.
     */
    public abstract Collection<C> getStepConsumers();

    /**
     * <h1>tell how to take a step.</h1>
     * <br>
     * 2 ways to make the job HALTED:
     * <li> throw a JobHaltException</li>
     * <li> return HALTED</li
     * <br>
     * 2 ways to make current step FAILED:
     * <li> throw any other Exception</li>
     * <li> return FAILED</li>
     * <br>
     * Returning null will be regard as OK
     *
     * @param consumer
     * @return JobResult string, such as FAILED, SKIPPED, OK; regards null as OK
     * @throws Exception
     */
    protected abstract String takeStep(C consumer) throws Exception;

    /**
     * tell how to show text of a given step
     *
     * @param consumer input arg of the given step
     */
    protected abstract String getStepText(C consumer);


    @Override
    public void start() {
        if (status != null && status.getCategory().equals(JobStatus.Category.RUNNING)) {
            halt("fail to start an already running job.");
            return;
        }
        Collection<C> consumers;
        try {
            consumers = getStepConsumers();
            status = new BasicJobStatus(consumers.size());
        } catch (Exception e) {
            halt("fail to get steps. stepConsumers not initialized.");
            return;
        }
        status.setStartTime(LocalDateTime.now());
        logger.info("Job[{}] Started. {} steps included.", getName(), consumers.size());
        for (C item : consumers) {
            String result = startNextStep(item);
            if (HALTED.equals(result)) {
                break;
            }
        }
        status.next(getName() + " has been finished.");
        logger.info("Job[{}] Finished. {}", getName(), status.getResults());
    }

    private String startNextStep(C consumer) {
        String stepText = getStepText(consumer);
        int curr = status.next(stepText);
        LocalDateTime t1 = status.getCurrentStartTime();
        int total = status.getTotalSteps();
        String s1 = String.format(getS1Format(total), curr, total);
        try {
            String result = takeStep(consumer);
            if (result == null) {
                result = OK;
            }
            status.as(result);
            String s2 = String.format(" %7s ", result);
            LocalDateTime t2 = LocalDateTime.now();
            long timeUsed = ChronoUnit.MILLIS.between(t1, t2);
            String s3 = String.format(". %d ms used.", timeUsed);
            logger.info(s1 + s2 + stepText + s3);
            return result;
        } catch (JobHaltException jhe) {
            halt(jhe.getMessage());
            return HALTED;
        } catch (Exception e) {
            status.as(FAILED);
            String s2 = String.format(" %7s ", FAILED);
            logger.warn(s1 + s2 + stepText, e);
            return FAILED;
        }

    }

    @Override
    public void halt(String message) {
        status = new BasicJobStatus(0);
        logger.error("Job[{}] HALTED: ", getName(), message);
        status.asHalted(message);
    }

    @Override
    public BasicJobStatus getStatus() {
//        if (status == null) {
//            Collection<C> consumers = getStepConsumers();
//            if (consumers != null) {
//                status = new BasicJobStatus(consumers.size());
//            }
//        }
        return status;
    }


    @Override
    public void resetStatus() {
        status = null;
    }

    public void setStatus(BasicJobStatus jobStatus) {
        this.status = jobStatus;
    }

    private static String getS1Format(int total) {
        int nDigit = countDigits(total);
        String s = nDigit > 0 ? String.valueOf(nDigit) : "";
        return "[%" + s + "d/%" + s + "d]";
    }

    private static int countDigits(int num) {
        return (int) Math.floor(Math.log10(Math.abs(num))) + 1;
    }
}
