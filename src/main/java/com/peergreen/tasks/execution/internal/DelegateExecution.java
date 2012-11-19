package com.peergreen.tasks.execution.internal;

import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilderManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class DelegateExecution implements Execution, PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private Delegate<?> delegate;

    public DelegateExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, Delegate<?> delegate) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.delegate = delegate;
    }
    public void execute() {
        delegate.setState(State.RUNNING);
        Task delegated = delegate.getDelegate();
        if (delegated != null) {
            delegated.addPropertyChangeListener("state", this);
            executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), delegated).execute();
        } else {
            // No delegated Task, simply complete
            delegate.setState(State.COMPLETED);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
            case COMPLETED:
                delegate.setState(newValue);
                break;
            default:
        }

    }
}
