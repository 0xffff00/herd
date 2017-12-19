package party.threebody.herd.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.threebody.skean.misc.SkeanException;

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
     * tell how to take a step. any Exception will check of this step as FAILED
     *
     * @param consumer
     * @return JobResult string, such as FAILED, OK; regards null as OK
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
        if (status != null && status.isRunning()) {
            throw new SkeanException("fail to start since BasicLinarJob[" + getName() + "] has been running.");
        }
        Collection<C> consumers = getStepConsumers();
        if (consumers == null) {
            throw new SkeanException("fail to start since Job[" + getName() + "]'s consumers not initialized.");
        }
        status = new BasicJobStatus(consumers.size());
        status.setStartTime(LocalDateTime.now());
        logger.info("Job[{}] Started. {} steps included.", getName(), consumers.size());
        for (C item : consumers) {
            String result = startNextStep(item);
            if (FATAL.equals(result)) {
                break;
            }
        }
        logger.info("Job[{}] Finished. {}", getName(), status.getResults());
    }

    protected String startNextStep(C consumer) {
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
        } catch (Exception e) {
            status.as(FAILED);
            String s2 = String.format(" %7s ", FAILED);
            logger.warn(s1 + s2 + stepText, e);
            return FAILED;
        }

    }

    @Override
    public BasicJobStatus getStatus() {
        if (status == null) {
            Collection<C> consumers = getStepConsumers();
            if (consumers != null) {
                status = new BasicJobStatus(consumers.size());
            }
        }
        return status;
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
