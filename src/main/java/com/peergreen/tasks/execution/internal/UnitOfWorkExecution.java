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
import com.peergreen.tasks.execution.ErrorHandler;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecution extends AbstractExecution {

    private ExecutorService executorService;
    private ErrorHandler errorHandler;
    private TaskContext taskContext;
    private UnitOfWork unitOfWork;

    public UnitOfWorkExecution(ExecutorService executorService, ErrorHandler errorHandler, TaskContext taskContext, UnitOfWork unitOfWork) {
        this.executorService = executorService;
        this.errorHandler = errorHandler;
        this.taskContext = taskContext;
        this.unitOfWork = unitOfWork;
    }

    public void execute() {
        // Execute/Schedule the unit of work
        setState(State.SCHEDULED);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                unitOfWork.setReadOnly();
                setState(State.RUNNING);
                try {
                    unitOfWork.getJob().execute(taskContext);
                } catch (Throwable t) {
                    setState(State.FAILED);
                    errorHandler.onError(UnitOfWorkExecution.this, t);
                    return;
                }
                setState(State.COMPLETED);
            }
        });
    }

    @Override
    public Task getModel() {
        return unitOfWork;
    }

    @Override
    public TaskContext getContext() {
        return taskContext;
    }
}
