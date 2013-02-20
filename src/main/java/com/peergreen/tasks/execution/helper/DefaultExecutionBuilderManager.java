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

package com.peergreen.tasks.execution.helper;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Task;

import java.util.HashMap;
import java.util.Map;

public class DefaultExecutionBuilderManager implements ExecutionBuilderManager {
    private Map<Class<? extends Task>, InternalExecutionBuilder> builders = new HashMap<Class<? extends Task>, InternalExecutionBuilder>();
    private TaskContextFactory taskContextFactory;
    private TrackerManager trackerManager;


    public DefaultExecutionBuilderManager() {
        this(new DefaultTaskContextFactory());
    }

    public DefaultExecutionBuilderManager(TaskContextFactory taskContextFactory) {
        this.taskContextFactory = taskContextFactory;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    public void setTrackerManager(TrackerManager trackerManager) {
        this.trackerManager = trackerManager;
    }

    public <T extends Task> void addExecutionBuilder(final Class<? extends T> taskType,
                                                     final ExecutionBuilder<T> builder) {
        // TODO Generate a warning if an ExecutionBuilder is already registered for the given type
        builders.put(taskType, new InternalExecutionBuilder<T>(taskType, builder));
    }

    @Override
    public Execution newExecution(ExecutionContext executionContext, Breadcrumb breadcrumb, Task task) {

        // Create a dedicated TaskContext for the new Task to be executed
        TaskContext context = taskContextFactory.createTaskContext(executionContext, breadcrumb, task);

        // Then find a compatible ExecutionBuilder
        Class<? extends Task> type = task.getClass();
        while (type != null) {

            // Find a compatible Builder
            InternalExecutionBuilder builder = builders.get(type);
            if (builder != null) {
                // Try to create an Execution
                Execution execution = builder.newExecution(context, task);
                if (execution != null) {
                    // Register the manager into the Task
                    if (trackerManager != null) {
                        execution.addPropertyChangeListener("state", trackerManager);
                    }
                    return execution;
                }
            }

            Class<?> superType = type.getSuperclass();
            if (Task.class.isAssignableFrom(superType)) {
                type = superType.asSubclass(Task.class);
            } else {
                type = null;
            }
        }

        throw new IllegalStateException("Cannot find any ExecutionBuilder supporting " + task.getClass());

    }

    private static class InternalExecutionBuilder<T extends Task> {
        private Class<? extends T> type;
        private ExecutionBuilder<T> builder;

        private InternalExecutionBuilder(Class<? extends T> type, ExecutionBuilder<T> builder) {
            this.type = type;
            this.builder = builder;
        }

        public Execution newExecution(TaskContext context, Task task) {
            return builder.newExecution(context, type.cast(task));
        }
    }
}