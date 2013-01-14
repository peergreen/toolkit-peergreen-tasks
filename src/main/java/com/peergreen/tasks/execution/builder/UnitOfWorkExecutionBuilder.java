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

package com.peergreen.tasks.execution.builder;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.ErrorHandler;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.internal.UnitOfWorkExecution;
import com.peergreen.tasks.model.UnitOfWork;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecutionBuilder implements ExecutionBuilder<UnitOfWork> {

    private ExecutorService executorService;
    private ErrorHandler errorhandler;

    public UnitOfWorkExecutionBuilder(ExecutorService executorService) {
        this(executorService, new DefaultErrorHandler());
    }

    public UnitOfWorkExecutionBuilder(ExecutorService executorService, ErrorHandler errorhandler) {
        this.executorService = executorService;
        this.errorhandler = errorhandler;
    }

    @Override
    public Execution newExecution(TaskContext taskContext, UnitOfWork task) {
        return new UnitOfWorkExecution(executorService, errorhandler, taskContext, task);
    }

    public static class DefaultErrorHandler implements ErrorHandler {

        @Override
        public void onError(LiveTask liveTask, Throwable throwable) {
            System.out.printf("Task '%s' failed with %s.%n",
                              liveTask.getModel().getName(),
                              throwable.getMessage());
        }
    }
}
