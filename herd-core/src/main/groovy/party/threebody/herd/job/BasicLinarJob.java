package party.threebody.herd.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.threebody.skean.misc.SkeanException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import static party.threebody.herd.job.JobResult.FAILED;
import static party.threebody.herd.job.JobResult.OK;

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
     * @return JobResult string, such as FAILED, OK
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
        status = new BasicJobStatus(consumers.size());
        status.setStartTime(LocalDateTime.now());
        logger.info(" Job[{}] Started. {} steps included.", getName(), consumers.size());
        for (C item : consumers) {
            startNextStep(item);
        }
        logger.info("Job[{}] Finished. {}", getName(), status.getResults());
    }

    protected void startNextStep(C consumer) {
        String stepText = getStepText(consumer);
        int curr = status.next(stepText);
        LocalDateTime t1 = status.getCurrentStartTime();
        int total = status.getTotalSteps();
        String s1 = String.format("[%3d/%3d]", curr, total);
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
        } catch (Exception e) {
            status.as(FAILED);
            String s2 = String.format(" %7s ", FAILED);
            logger.warn(s1 + s2 + stepText, e);
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
}
