package party.threebody.herd.job;

public interface LinarJob {

    default String getName() {
        return getClass().getName();
    }

    void start();

    JobStatus getStatus();

}
