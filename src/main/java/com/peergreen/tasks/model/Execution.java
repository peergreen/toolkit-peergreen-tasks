package com.peergreen.tasks.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Execution {

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
        executeTasks();
    }

    private void executeTasks() {
        for (Pipeline pipeline : pipelines) {
            Collection<Task> runnable = filter(pipeline.getTasks());
            for (final Task task : runnable) {
                task.setState(State.SCHEDULED);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        task.setState(State.RUNNING);
                        task.getJob().execute(null);
                        task.setState(State.COMPLETED);
                        fireTaskCompleted(task);
                    }
                });
            }
        }
    }

    private void fireTaskCompleted(Task task) {

        // Notify Pipelines that a Task has been completed
        for (Pipeline pipeline : pipelines) {
            pipeline.taskCompleted(task);
        }

        // execute runnable Tasks
        executeTasks();
    }

    private Collection<Task> filter(Collection<Task> tasks) {
        Collection<Task> filtered = new HashSet<Task>();
        for (Task task : tasks) {

            // Only keep Tasks in WAITING mode
            if (!State.WAITING.equals(task.getState())) {
                continue;
            }

            // Only keep Tasks with empty dependencies
            if (!task.getDependencies().isEmpty()) {
                continue;
            }

            // add the task
            filtered.add(task);
        }
        return filtered;
    }
}
