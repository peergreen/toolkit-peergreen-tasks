package com.peergreen.tasks.execution;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.DefaultExecutionContext;
import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Task;

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
    private DefaultExecutionContext executionContext = new DefaultExecutionContext();
    private TaskContextFactory taskContextFactory = new DefaultTaskContextFactory();

    public RootExecution(Task task) {
        this.task = task;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void setTaskContextFactory(TaskContextFactory taskContextFactory) {
        this.taskContextFactory = taskContextFactory;
    }

    public void addExecutionBuilder(final ExecutionBuilder builder) {
        // Always add
        builders.add(0, builder);
    }

    @Override
    public Execution newExecution(ExecutionContext executionContext, Breadcrumb breadcrumb, Task task) {
        TaskContext context = taskContextFactory.createTaskContext(executionContext, breadcrumb, task);
        for (ExecutionBuilder builder : builders) {
            Execution execution = builder.newExecution(context);
            if (execution != null) {
                return execution;
            }
        }

        throw new IllegalStateException("Cannot find any ExecutionBuilder supporting " + task.getClass());
    }

    public void execute() {
        newExecution(executionContext, null, task).execute();
    }


}
