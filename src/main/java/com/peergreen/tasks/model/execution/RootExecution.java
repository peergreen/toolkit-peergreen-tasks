package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.context.ExecutionContext;
import com.peergreen.tasks.model.context.TaskContext;
import com.peergreen.tasks.model.execution.Execution;
import com.peergreen.tasks.model.execution.ExecutionBuilder;
import com.peergreen.tasks.model.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.tracker.TrackerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class RootExecution implements Execution, ExecutionBuilderManager {

    private List<ExecutionBuilder> builders = new ArrayList<ExecutionBuilder>();
    private Task task;
    private ExecutionContext executionContext = new ExecutionContext();

    public RootExecution(Task task) {
        this.task = task;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void addExecutionBuilder(final ExecutionBuilder builder) {
        // Always add
        builders.add(0, builder);
    }

    @Override
    public Execution newExecution(Breadcrumb breadcrumb, Task task) {
        TaskContext taskContext = executionContext.newTaskContext(breadcrumb, task);
        for (ExecutionBuilder builder : builders) {
            Execution execution = builder.newExecution(taskContext, task);
            if (execution != null) {
                return execution;
            }
        }

        throw new IllegalStateException("Cannot find any ExecutionBuilder supporting " + task.getClass());
    }

    public void execute() {
        newExecution(null, task).execute();
    }


}
