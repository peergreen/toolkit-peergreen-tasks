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
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.WakeUp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 09:46
 * To change this template use File | Settings | File Templates.
 */
public class WakeUpExecution extends AbstractExecution implements PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private WakeUp wakeUp;
    private Execution execution;

    public WakeUpExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, WakeUp task) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.wakeUp = task;

        // React to the Task's wake up event
        task.addPropertyChangeListener("wakeUp", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                reallyExecute();
            }
        });
    }

    @Override
    public void execute() {
        execution = executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), wakeUp.getDelegate());
        setState(State.SCHEDULED);
    }

    private void reallyExecute() {
        wakeUp.setReadOnly();
        setState(State.RUNNING);
        execution.addPropertyChangeListener("state", this);
        execution.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
                setState(State.FAILED);
                break;
            case COMPLETED:
                setState(State.COMPLETED);
                break;
        }
    }

    @Override
    public Task getModel() {
        return wakeUp;
    }

    @Override
    public TaskContext getContext() {
        return taskContext;
    }
}
