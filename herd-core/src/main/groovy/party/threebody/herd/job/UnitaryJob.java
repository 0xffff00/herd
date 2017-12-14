package party.threebody.herd.job;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * a Job containing only one step
 */
public abstract class UnitaryJob extends BasicLinarJob<Object> {
    private static final Collection<Object> allConsumers = Collections.singleton("NO-USE");

    @Override
    public Collection<Object> getStepConsumers() {
        return allConsumers;
    }


    @Override
    protected String takeStep(Object consumer) throws Exception {
        return takeStep();
    }

    public abstract String takeStep() throws Exception;

    public static UnitaryJob of(String name, Supplier<String> stepTaking) {
        return new UnitaryJob() {
            @Override
            public String takeStep() throws Exception {
                return stepTaking.get();
            }

            @Override
            protected String getStepText(Object consumer) {
                return name;
            }
        };
    }
}
