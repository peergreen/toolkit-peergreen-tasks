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

import com.peergreen.tasks.context.DefaultExecutionContext;
import com.peergreen.tasks.context.ExecutionContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilderManager;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class TaskExecutorService {

    private ExecutionBuilderManager executionBuilderManager;
    private ExecutionContext executionContext = new DefaultExecutionContext();

    public TaskExecutorService(ExecutionBuilderManager executionBuilderManager) {
        this.executionBuilderManager = executionBuilderManager;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public Future<State> execute(Task task) {
        return execute(executionContext, task);
    }

    public Future<State> execute(ExecutionContext executionContext, Task task) {
        Execution execution = executionBuilderManager.newExecution(executionContext, null, task);
        Future<State> future = new ExecutionFuture(execution);
        execution.execute();
        return future;
    }

    private class ExecutionFuture implements Future<State>, PropertyChangeListener {

        private Execution execution;
        private boolean done = false;
        private Lock lock = new Lock();

        public ExecutionFuture(Execution execution) {
            this.execution = execution;
            this.execution.addPropertyChangeListener("state", this);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public State get() throws InterruptedException, ExecutionException {
            if (!isDone()) {
                lock.lock();
            }
            return execution.getState();
        }

        @Override
        public State get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (!isDone()) {
                lock.lock(unit.toMillis(timeout));
            }
            return execution.getState();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            State state = (State) evt.getNewValue();
            switch (state) {
                case COMPLETED:
                case FAILED:
                    done = true;
                    lock.unlock();
                default:
            }
        }
    }

    /**
     * Simple lock implementation.
     */
    private class Lock {
        public synchronized void lock() throws InterruptedException {
            wait();
        }
        public synchronized void lock(long millis) throws InterruptedException {
            wait(millis);
        }
        public synchronized void unlock() {
            notifyAll();
        }
    }
}
