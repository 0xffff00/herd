package party.threebody.herd.job;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import party.threebody.herd.webapp.dao.*;
import party.threebody.herd.webapp.domain.MediaPath;
import party.threebody.herd.webapp.domain.Repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class  Job {

    static final Logger logger = LoggerFactory.getLogger(Job.class);

    JobStatus jobStatus;

    public <T> void takeStep(String stepText, Function<T, String> stepProcedure, T t) {
        int curr = jobStatus.next(stepText);
        LocalDateTime t1 = jobStatus.getCurrentStartTime();
        int total = jobStatus.getTotalSteps();
        String s1 = String.format("[%3d/%3d]", curr, total);
        try {
            String result = stepProcedure.apply(t);
            if (result == null) {
                result = JobStepResult.OK;
            }
            jobStatus.checkOff(result);
            String s2 = String.format(" %7s ", result);
            LocalDateTime t2 = LocalDateTime.now();
            long timeUsed = ChronoUnit.MILLIS.between(t1, t2);
            String s3 = String.format(". %d ms used.", timeUsed);
            logger.info(s1 + s2 + stepText + s3);
        } catch (Exception e) {
            String s2 = String.format(" %7s ", JobStepResult.FAILED);
            logger.warn(s1 + s2 + stepText, e);
        }
    }


    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}
