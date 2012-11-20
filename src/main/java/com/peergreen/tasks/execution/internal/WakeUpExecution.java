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
public class WakeUpExecution implements Execution, PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private WakeUp wakeUp;

    public WakeUpExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, WakeUp task) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.wakeUp = task;
        task.addPropertyChangeListener(this);
    }

    @Override
    public void execute() {

        wakeUp.setState(State.SCHEDULED);
    }

    private void reallyExecute() {
        wakeUp.setState(State.RUNNING);

        wakeUp.getDelegate().addPropertyChangeListener("state", this);
        executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), wakeUp.getDelegate()).execute();


    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("wakeUp".equals(event.getPropertyName())) {
            reallyExecute();
        } else if ("state".equals(event.getPropertyName())) {
            State newValue = (State) event.getNewValue();

            switch (newValue) {
                case FAILED:
                    wakeUp.setState(State.FAILED);
                    break;
                case COMPLETED:
                    wakeUp.setState(State.COMPLETED);
                    break;
            }

        }
    }
}
