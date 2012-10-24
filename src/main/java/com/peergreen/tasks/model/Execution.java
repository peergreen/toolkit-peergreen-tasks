package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Execution implements StateListener {

    private ExecutorService executorService;
    private Collection<Pipeline> pipelines;

    public Execution(ExecutorService executorService, Pipeline pipeline) {
        this(executorService, Collections.singleton(pipeline));
    }

    public Execution(ExecutorService executorService, Collection<Pipeline> pipelines) {
        this.executorService = executorService;
        this.pipelines = pipelines;
    }

    public void start() {
        // Prepare initial states of Pipelines
        for (Pipeline pipeline : pipelines) {
            pipeline.setState(State.RUNNING);
        }

        // Start execution flow
        executePipelines();
    }

    private void executePipelines() {

        // Iterates on all the Pipelines
        for (Pipeline pipeline : pipelines) {
            executePipeline(pipeline);
        }
    }

    private void executePipeline(Pipeline pipeline) {
        // Collect the runnable tasks of the pipeline
        Collection<Task> runnable = filter(pipeline.getTasks());

        for (Task task : runnable) {
            executeTask(task);
        }

        if (pipeline.isTerminated()) {
            System.out.printf("Closing %s%n", pipeline.getName());
            pipeline.setState(State.COMPLETED);
        }
    }

    private void executeTask(Task task) {
        if (task instanceof UnitOfWork) {
            executeUnitOfWork((UnitOfWork) task);
        } else if (task instanceof Pipeline) {
            executeSubPipeline((Pipeline) task);
        } // Unknown type, error ?
    }

    private void executeSubPipeline(Pipeline pipeline) {
        // Execute the inner pipeline
        System.out.printf("Executing %s%n", pipeline.getName());
        pipeline.addStateListener(this);
        Execution subExecution = new Execution(executorService, pipeline);
        subExecution.start();
    }

    private void executeUnitOfWork(final UnitOfWork unitOfWork) {
        // Execute/Schedule the unit of work
        unitOfWork.setState(State.SCHEDULED);
        unitOfWork.addStateListener(this);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.printf("Executing %s%n", unitOfWork.getName());
                unitOfWork.setState(State.RUNNING);
                unitOfWork.getJob().execute(null);
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
        if (current == State.COMPLETED) {
            executePipelines();
        }
    }
}
