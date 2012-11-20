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

package com.peergreen.tasks.execution.helper;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.model.Task;

import java.util.ArrayList;
import java.util.List;

public class DefaultExecutionBuilderManager implements ExecutionBuilderManager {
    private List<ExecutionBuilder> builders = new ArrayList<ExecutionBuilder>();
    private TaskContextFactory taskContextFactory;

    public DefaultExecutionBuilderManager() {
        this(new DefaultTaskContextFactory());
    }

    public DefaultExecutionBuilderManager(TaskContextFactory taskContextFactory) {
        this.taskContextFactory = taskContextFactory;
    }

    public void addExecutionBuilder(final ExecutionBuilder builder) {
        // Always add in first place to simulate interception
        builders.add(0, builder);
    }

    @Override
    public Execution newExecution(ExecutionContext executionContext, Breadcrumb breadcrumb, Task task) {

        // Create a dedicated TaskContext for the new Task to be executed
        TaskContext context = taskContextFactory.createTaskContext(executionContext, breadcrumb, task);

        // Then find a compatible ExecutionBuilder
        for (ExecutionBuilder builder : builders) {
            Execution execution = builder.newExecution(context);
            if (execution != null) {
                return execution;
            }
        }

        throw new IllegalStateException("Cannot find any ExecutionBuilder supporting " + task.getClass());
    }
}