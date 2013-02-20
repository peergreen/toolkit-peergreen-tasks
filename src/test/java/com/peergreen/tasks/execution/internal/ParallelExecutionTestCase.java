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

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Job;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.StateTracker;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.FailingJob;
import com.peergreen.tasks.model.job.SleepJob;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.peergreen.tasks.context.helper.References.parallel;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class ParallelExecutionTestCase {

    // Need at least 3 executors so that all jobs are executed concurrently
    public static final int N_THREADS = 3;

    @Test
    public void testConcurrentExecution() throws Exception {
        Parallel parallel = new Parallel();

        final Task task0 = new UnitOfWork(new SleepJob(20), "task-0");
        final Task task1 = new UnitOfWork(new SleepJob(20), "task-1");
        final Task task2 = new UnitOfWork(new SleepJob(20), "task-2");

        parallel.add(task0);
        parallel.add(task1);
        parallel.add(task2);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        StateTracker tracker = new StateTracker();
        manager.registerTracker(tracker);

        Future<State> future = execution.execute(parallel);

        // Tasks should be running/scheduled at this point
        assertTrue((State.RUNNING.equals(tracker.getState(task0))) || (State.SCHEDULED.equals(tracker.getState(task0))));
        assertTrue((State.RUNNING.equals(tracker.getState(task1))) || (State.SCHEDULED.equals(tracker.getState(task1))));
        assertTrue((State.RUNNING.equals(tracker.getState(task2))) || (State.SCHEDULED.equals(tracker.getState(task2))));

        // Wait completion
        future.get();

        assertEquals(tracker.getState(task0), State.COMPLETED);
        assertEquals(tracker.getState(task1), State.COMPLETED);
        assertEquals(tracker.getState(task2), State.COMPLETED);

    }


    @Test
    public void testConcurrentExecutionWithFailure() throws Exception {
        Parallel parallel = new Parallel();

        Task a = new UnitOfWork(new EmptyJob(), "task-0");
        Task b = new UnitOfWork(new FailingJob(), "task-1");
        Task c = new UnitOfWork(new EmptyJob(), "task-2");

        parallel.add(a);
        parallel.add(b);
        parallel.add(c);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        StateTracker tracker = new StateTracker();
        manager.registerTracker(tracker);

        execution.execute(parallel).get();

        assertEquals(tracker.getState(a), State.COMPLETED);
        assertEquals(tracker.getState(b), State.FAILED);
        assertEquals(tracker.getState(c), State.COMPLETED);
        assertEquals(tracker.getState(parallel), State.FAILED);

    }


    @Test
    public void testTaskAdditionLive() throws Exception {

        /**
         * Step 0 (initial):
         * +-------------------------+
         * |          +-----------+  |
         * |  +---+   |   +---+   |  |
         * |  | a | < |   | b |   |  |
         * |  +---+   |   +---+   |  |
         * |          +-----------+  |
         * +-------------------------+
         * Step 1 (a add c):
         * +----------------------------------+
         * |          +--------------------+  |
         * |  +---+   |   +---+    +---+   |  |
         * |  | a | < |   | b | // | c |   |  |
         * |  +---+   |   +---+    +---+   |  |
         * |          +--------------------+  |
         * +----------------------------------+
         */

        // Prepare objects
        Pipeline global = new Pipeline();
        final Parallel master = new Parallel("master");

        final UnitOfWork c = new UnitOfWork(new EmptyJob(), "c");

        UnitOfWork a = new UnitOfWork(new Job() {
            @Override
            public void execute(TaskContext context) throws Exception {
                context.find(parallel("../master")).add(c);
            }
        }, "a");

        UnitOfWork b = new UnitOfWork(new EmptyJob(), "b");

        // Then build Pipelines
        global.add(a, master);
        master.add(b);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        StateTracker tracker = new StateTracker();
        manager.registerTracker(tracker);

        execution.execute(global).get();

        assertEquals(tracker.getState(c), State.COMPLETED);

    }

    @Test
    public void testEmptyParallelExecution() throws Exception {

        // Prepare objects
        Parallel master = new Parallel();

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        StateTracker tracker = new StateTracker();
        manager.registerTracker(tracker);

        execution.execute(master).get();

        assertEquals(tracker.getState(master), State.COMPLETED);


    }


}


