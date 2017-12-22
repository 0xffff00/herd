package party.threebody.herd.job;

public interface LinarJob {

    default String getName() {
        return getClass().getName();
    }

    /**
     * start the job
     */
    void start();

    /**
     * halt the job means it cannot go on anymore and will never be recovered.
     * @param message
     */
    void halt(String message);

    void resetStatus();

    JobStatus getStatus();

}
