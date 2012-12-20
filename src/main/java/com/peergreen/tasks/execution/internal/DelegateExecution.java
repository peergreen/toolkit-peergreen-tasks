/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.execution.internal;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class DelegateExecution extends AbstractExecution implements PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private Delegate<?> delegate;

    public DelegateExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, Delegate<?> delegate) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.delegate = delegate;
    }
    public void execute() {
        delegate.setReadOnly();
        setState(State.RUNNING);
        Task delegated = delegate.getDelegate();
        if (delegated != null) {
            Execution execution = executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), delegated);
            execution.addPropertyChangeListener("state", this);
            execution.execute();
        } else {
            // No delegated Task, simply complete
            setState(State.COMPLETED);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
            case COMPLETED:
                setState(newValue);
                break;
            default:
        }

    }

    @Override
    public Task getModel() {
        return delegate;
    }

    @Override
    public TaskContext getContext() {
        return taskContext;
    }
}
