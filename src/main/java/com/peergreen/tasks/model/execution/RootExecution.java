package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.context.DefaultExecutionContext;
import com.peergreen.tasks.model.context.DefaultTaskContext;
import com.peergreen.tasks.model.context.ExecutionContext;
import com.peergreen.tasks.model.context.TaskContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class RootExecution implements Execution, ExecutionBuilderManager, TaskContextFactory {

    private List<ExecutionBuilder> builders = new ArrayList<ExecutionBuilder>();
    private Task task;
    private DefaultExecutionContext executionContext = new DefaultExecutionContext();
    private TaskContextFactory taskContextFactory = this;

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
        TaskContext context = createTaskContext(executionContext, breadcrumb, task);
        for (ExecutionBuilder builder : builders) {
            Execution execution = builder.newExecution(context);
            if (execution != null) {
                return execution;
            }
        }

        throw new IllegalStateException("Cannot find any ExecutionBuilder supporting " + task.getClass());
    }

    @Override
    public TaskContext createTaskContext(ExecutionContext parent, Breadcrumb breadcrumb, Task task) {
        return new DefaultTaskContext(parent, new Breadcrumb(breadcrumb, task));
    }

    public void execute() {
        newExecution(executionContext, null, task).execute();
    }


}
