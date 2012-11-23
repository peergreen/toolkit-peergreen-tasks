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
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecution implements Execution, PropertyChangeListener {

    private ExecutionBuilderManager executionBuilderManager;
    private AtomicInteger completed = new AtomicInteger(0);
    private State out = State.COMPLETED;
    private TaskContext taskContext;
    private Parallel parallel;

    public ParallelExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, Parallel parallel) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.parallel = parallel;
    }

    public void execute() {
        parallel.setState(State.RUNNING);

        if (!parallel.getTasks().isEmpty()) {
            // Start execution flow
            executeAll();
        } else {
            parallel.setState(State.COMPLETED);
        }
    }

    private void executeAll() {
        for (Task task : parallel.getTasks()) {
            task.addPropertyChangeListener("state", this);
            executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), task).execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        State newValue = (State) event.getNewValue();

        switch (newValue) {
            case FAILED:
                out = State.FAILED;
            case COMPLETED:
                // The inner Task has been completed
                if (completed.incrementAndGet() == parallel.getTasks().size()) {
                    // All tasks have been executed
                    // The parallel is now either FAILED or COMPLETED
                    parallel.setState(out);
                }
        }
    }
}
