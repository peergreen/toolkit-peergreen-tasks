/*
 * Copyright 2012 Peergreen S.A.S.
 *
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicInteger;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecution extends AbstractExecution implements PropertyChangeListener {

    private final ExecutionBuilderManager executionBuilderManager;
    private final AtomicInteger completed = new AtomicInteger(0);
    private State out = State.COMPLETED;
    private final TaskContext taskContext;
    private final Parallel parallel;

    public ParallelExecution(ExecutionBuilderManager executionBuilderManager, TaskContext taskContext, Parallel parallel) {
        this.executionBuilderManager = executionBuilderManager;
        this.taskContext = taskContext;
        this.parallel = parallel;
    }

    @Override
    public void execute() {
        parallel.setReadOnly();
        setState(State.RUNNING);

        if (!parallel.getTasks().isEmpty()) {
            // Start execution flow
            executeAll();
        } else {
            setState(State.COMPLETED);
        }
    }

    private void executeAll() {
        for (Task task : parallel.getTasks()) {
            Execution execution = executionBuilderManager.newExecution(taskContext, taskContext.getBreadcrumb(), task);
            execution.addPropertyChangeListener("state", this);
            execution.execute();
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
                    setState(out);
                }
        }
    }

    @Override
    public Task getModel() {
        return parallel;
    }

    @Override
    public TaskContext getContext() {
        return taskContext;
    }
}
