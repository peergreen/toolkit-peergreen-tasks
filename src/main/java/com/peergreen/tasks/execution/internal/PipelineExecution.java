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
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecution implements Execution, PropertyChangeListener {

    private Iterator<Task> cursor;
    private ExecutionBuilderManager executionBuilderManager;
    private TaskContext taskContext;
    private Pipeline pipeline;

    public PipelineExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, Pipeline pipeline) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.pipeline = pipeline;
        this.cursor = pipeline.getTasks().listIterator();
    }

    public void execute() {
        pipeline.setState(State.RUNNING);

        // Start execution flow
        executeNext();
    }

    private void executeNext() {
        if (cursor.hasNext()) {
            // Schedule the next one on the list
            Task next = cursor.next();
            next.addPropertyChangeListener("state", this);
            executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), next).execute();
        } else {
            // Change Pipeline's state
            pipeline.setState(State.COMPLETED);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
                pipeline.setState(State.FAILED);
                break;
            case COMPLETED:
                // The inner Task has been completed
                executeNext();
        }
    }
}
