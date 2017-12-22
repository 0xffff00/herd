package party.threebody.herd.job;

import java.time.LocalDateTime;
import java.util.Map;

public interface JobStatus {

    int getCurrent();

    int getTotalSteps();

    String getCurrentMessage();

    LocalDateTime getStartTime();

    LocalDateTime getCurrentStartTime();

    Category getCategory();

    Map<String, Integer> getResults();


    enum Category {
        INITIAL, RUNNING, COMPLETED, HALTED
    }
}
