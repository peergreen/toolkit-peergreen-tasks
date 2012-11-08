package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecution extends AbstractExecution {

    private Parallel parallel;
    private AtomicInteger completed = new AtomicInteger(0);

    public ParallelExecution(ExecutorService executorService, Parallel parallel) {
        super(executorService);
        this.parallel = parallel;
    }

    protected ParallelExecution(AbstractExecution parent, Parallel parallel) {
        super(parent);
        this.parallel = parallel;
    }

    public void execute() {
        parallel.addStateListener(getTrackerManager());
        parallel.setState(State.RUNNING);

        // Start execution flow
        executeAll();
    }

    private void executeAll() {
        for (Task task : parallel.getTasks()) {
            executeTask(task);
        }
    }


    @Override
    public void stateChanged(Task source, State previous, State current) {
        if (State.COMPLETED == current) {
            // The inner Task has been completed
            int number = completed.incrementAndGet();
            if (number == parallel.getTasks().size()) {
                // All tasks have been executed
                parallel.setState(State.COMPLETED);
            }
        }
    }

}
