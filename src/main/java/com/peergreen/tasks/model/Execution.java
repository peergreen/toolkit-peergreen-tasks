package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.peergreen.tasks.model.util.Pipelines.parallelize;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Execution implements StateListener {

    private ExecutorService executorService;
    private Pipeline pipeline;

    private Map<Pipeline, Execution> executions = new HashMap<Pipeline, Execution>();

    public Execution(ExecutorService executorService, Pipeline... pipelines) {
        this(executorService, parallelize(pipelines));
    }

    public Execution(ExecutorService executorService, Pipeline pipeline) {
        this.executorService = executorService;
        this.pipeline = pipeline;
    }

    public void start() {
        pipeline.addStateListener(new LogStateListener());
        pipeline.setState(State.RUNNING);

        // Start execution flow
        executePipeline();
    }

    private void executePipeline() {
        // Collect the runnable tasks of the pipeline
        Collection<Task> runnable = filter(pipeline.getTasks());

        for (Task task : runnable) {
            executeTask(task);
        }

        for (Execution execution : executions.values()) {
            execution.executePipeline();
        }

    }

    private void executeTask(Task task) {
        if (task instanceof UnitOfWork) {
            executeUnitOfWork((UnitOfWork) task);
        } else if (task instanceof Pipeline) {
            executeSubPipeline((Pipeline) task);
        } // Unknown type, error ?
    }

    private void executeSubPipeline(Pipeline sub) {

        Execution inner = executions.get(sub);
        if (inner == null) {
            sub.addStateListener(this);
            inner = new Execution(executorService, sub);
            executions.put(sub, inner);

            inner.start();
        } else {
            inner.executePipeline();
        }
    }

    private void executeUnitOfWork(final UnitOfWork unitOfWork) {
        // Execute/Schedule the unit of work
        unitOfWork.addStateListener(new LogStateListener());
        unitOfWork.addStateListener(this);
        unitOfWork.setState(State.SCHEDULED);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                unitOfWork.setState(State.RUNNING);
                try {
                    unitOfWork.getJob().execute(null);
                } catch (Throwable t) {
                    unitOfWork.setState(State.FAILED);
                    return;
                }
                unitOfWork.setState(State.COMPLETED);
            }
        });
    }

    private Collection<Task> filter(Collection<Task> tasks) {
        Collection<Task> filtered = new HashSet<Task>();
        for (Task task : tasks) {
            if (task.isReady()) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    @Override
    public void stateChanged(Task source, State previous, State current) {

        // Try to find some new tasks to execute
        executePipeline();

        // If the task has failed, change the global state of the executed Pipeline
        if ((current == State.FAILED) && (pipeline.getState() != State.FAILED)) {
            pipeline.setState(State.FAILED);
        }

        // If the task has completed without problems AND that there are no more task to execute
        if ((current == State.COMPLETED)
                && (pipeline.getState() != State.COMPLETED)
                && (pipeline.isTerminated())) {
            pipeline.setState(State.COMPLETED);
        }


    }

    private class LogStateListener implements StateListener {

        @Override
        public void stateChanged(Task source, State previous, State current) {
            System.out.printf(
                    "%15s - %9S - %s%n",
                    Thread.currentThread().getName(),
                    current.name(),
                    source.getName()
            );
        }
    }
}
