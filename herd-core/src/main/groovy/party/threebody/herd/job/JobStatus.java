package party.threebody.herd.job;

import java.time.LocalDateTime;

public interface JobStatus {


    int getCurrent();

    int getTotalSteps();

    String getCurrentMessage();

    default boolean isFinished() {
        return getCurrent() > getTotalSteps();
    }

    default boolean isRunning() {
        return getCurrent() > 0 && getCurrent() <= getTotalSteps();
    }

    LocalDateTime getStartTime();

    LocalDateTime getCurrentStartTime();


}
