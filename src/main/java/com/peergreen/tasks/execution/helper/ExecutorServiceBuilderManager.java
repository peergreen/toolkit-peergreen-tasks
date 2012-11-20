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

import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.execution.builder.DelegateExecutionBuilder;
import com.peergreen.tasks.execution.builder.ParallelExecutionBuilder;
import com.peergreen.tasks.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.execution.builder.UnitOfWorkExecutionBuilder;
import com.peergreen.tasks.execution.builder.WakeUpExecutionBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/11/12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public class ExecutorServiceBuilderManager extends DefaultExecutionBuilderManager {

    public static final int DEFAULT_POOL_SIZE = 10;

    public ExecutorServiceBuilderManager() {
        this(DEFAULT_POOL_SIZE);
    }

    public ExecutorServiceBuilderManager(int size) {
        this(Executors.newFixedThreadPool(size));
    }

    public ExecutorServiceBuilderManager(ExecutorService executorService) {
        this(new DefaultTaskContextFactory(), executorService);
    }

    public ExecutorServiceBuilderManager(TaskContextFactory taskContextFactory, ExecutorService executorService) {
        super(taskContextFactory);
        addExecutionBuilder(new UnitOfWorkExecutionBuilder(executorService));
        addExecutionBuilder(new PipelineExecutionBuilder(this));
        addExecutionBuilder(new ParallelExecutionBuilder(this));
        addExecutionBuilder(new WakeUpExecutionBuilder(this));
        addExecutionBuilder(new DelegateExecutionBuilder(this));
    }
}
