/*
 * Copyright 2012-2013 Peergreen S.A.S.
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.peergreen.tasks.context.DefaultTaskContextFactory;
import com.peergreen.tasks.execution.ErrorHandler;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.execution.builder.DelegateExecutionBuilder;
import com.peergreen.tasks.execution.builder.ParallelExecutionBuilder;
import com.peergreen.tasks.execution.builder.PipelineExecutionBuilder;
import com.peergreen.tasks.execution.builder.UnitOfWorkExecutionBuilder;
import com.peergreen.tasks.execution.builder.WakeUpExecutionBuilder;
import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.WakeUp;

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
        this(new DefaultTaskContextFactory(), executorService, new UnitOfWorkExecutionBuilder.DefaultErrorHandler());
    }

    public ExecutorServiceBuilderManager(TaskContextFactory taskContextFactory, ExecutorService executorService) {
        this(taskContextFactory, executorService, new UnitOfWorkExecutionBuilder.DefaultErrorHandler());
    }

    public ExecutorServiceBuilderManager(ExecutorService executorService, ErrorHandler errorHandler) {
        this(new DefaultTaskContextFactory(), executorService, errorHandler);
    }

    public ExecutorServiceBuilderManager(TaskContextFactory taskContextFactory, ExecutorService executorService, ErrorHandler errorHandler) {
        super(taskContextFactory);
        addExecutionBuilder(UnitOfWork.class, new UnitOfWorkExecutionBuilder(executorService, errorHandler));
        addExecutionBuilder(Pipeline.class, new PipelineExecutionBuilder(this));
        addExecutionBuilder(Parallel.class, new ParallelExecutionBuilder(this));
        addExecutionBuilder(WakeUp.class, new WakeUpExecutionBuilder(this));
        addExecutionBuilder(Delegate.class, new DelegateExecutionBuilder(this));
    }
}
