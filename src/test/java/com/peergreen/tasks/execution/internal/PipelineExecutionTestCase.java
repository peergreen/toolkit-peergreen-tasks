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

import static com.peergreen.tasks.context.helper.References.pipeline;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.Test;

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
import com.peergreen.tasks.model.expect.NotExecutedTracker;
import com.peergreen.tasks.model.expect.SequenceTracker;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.FailingJob;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecutionTestCase {

    public static final int N_THREADS = 2;

    @Test
    public void testSequentialExecution() throws Exception {

        /**
         * +---------------------------+
         * |   +---+   +---+   +---+   |
         * |   | a | < | b | < | c |   |
         * |   +---+   +---+   +---+   |
         * +---------------------------+
         */

        Pipeline pipeline = new Pipeline();

        Task task0 = new UnitOfWork(new EmptyJob(), "task-0");
        Task task1 = new UnitOfWork(new EmptyJob(), "task-1");
        Task task2 = new UnitOfWork(new EmptyJob(), "task-2");

        pipeline.add(task0);
        pipeline.add(task1);
        pipeline.add(task2);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(task0, State.COMPLETED);
        tracker.addStep(task1, State.COMPLETED);
        tracker.addStep(task2, State.COMPLETED);

        execution.execute(pipeline).get();

        assertTrue(tracker.verify());
    }

    @Test
    public void testSequentialExecutionWithFailure() throws Exception {

        /**
         * +---------------------------+
         * |   +---+   +---+   +---+   |
         * |   | a | < | b | < | c |   |
         * |   +---+   +---+   +---+   |
         * +---------------------------+
         *               X
         */

        Pipeline pipeline = new Pipeline();

        Task a = new UnitOfWork(new EmptyJob(), "task-a");

        Task b = new UnitOfWork(new FailingJob(), "task-b");

        Task c = new UnitOfWork(new EmptyJob(), "task-c");

        pipeline.add(a, b, c);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(a, State.COMPLETED);
        tracker.addStep(b, State.FAILED);
        tracker.addStep(pipeline, State.FAILED);

        NotExecutedTracker tracker2 = new NotExecutedTracker(c);
        manager.registerTracker(tracker2);

        execution.execute(pipeline).get();

        assertTrue(tracker.verify());
        assertTrue(tracker2.verify());

    }

    @Test
    public void testPipelinesExecutedInSequence() throws Exception {

        /**
         * +-------------------------------------------------+
         * |  +-------------------+   +-------------------+  |
         * |  |   +---+   +---+   |   |   +---+   +---+   |  |
         * |  |   | a | < | b |   | < |   | c | < | d |   |  |
         * |  |   +---+   +---+   |   |   +---+   +---+   |  |
         * |  +-------------------+   +-------------------+  |
         * +-------------------------------------------------+
         */
        Pipeline master = new Pipeline("master");
        Pipeline one = new Pipeline("pipeline-one");
        Pipeline two = new Pipeline("pipeline-two");
        master.add(one, two);

        Task taskA = new UnitOfWork(new EmptyJob(), "task-a");
        Task taskB = new UnitOfWork(new EmptyJob(), "task-b");

        one.add(taskA);
        one.add(taskB);

        Task taskC = new UnitOfWork(new EmptyJob(), "task-c");
        Task taskD = new UnitOfWork(new EmptyJob(), "task-d");

        two.add(taskC);
        two.add(taskD);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(taskA, State.COMPLETED);
        tracker.addStep(taskB, State.COMPLETED);
        tracker.addStep(one, State.COMPLETED);
        tracker.addStep(taskC, State.COMPLETED);
        tracker.addStep(taskD, State.COMPLETED);
        tracker.addStep(two, State.COMPLETED);
        tracker.addStep(master, State.COMPLETED);

        execution.execute(master).get();

        assertTrue(tracker.verify());

    }

    @Test
    public void testStagedExecution() throws Exception {

        /**
         * +---------------------------------------------------+
         * |  +--------------------+   +--------------------+  |
         * |  |   +---+    +---+   |   |   +---+    +---+   |  |
         * |  |   | a | // | b |   | < |   | c | // | d |   |  |
         * |  |   +---+    +---+   |   |   +---+    +---+   |  |
         * |  +--------------------+   +--------------------+  |
         * +---------------------------------------------------+
         */

        Pipeline pipeline = new Pipeline();
        Parallel stage1 = new Parallel("stage-one");
        Parallel stage2 = new Parallel("stage-two");

        Task a = new UnitOfWork(new EmptyJob(), "task-a");
        Task b = new UnitOfWork(new EmptyJob(), "task-b");

        Task c = new UnitOfWork(new EmptyJob(), "task-c");
        Task d = new UnitOfWork(new EmptyJob(), "task-d");

        stage1.add(a);
        stage1.add(b);

        stage2.add(c);
        stage2.add(d);

        pipeline.add(stage1, stage2);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(stage1, State.COMPLETED);
        tracker.addStep(stage2, State.COMPLETED);
        tracker.addStep(pipeline, State.COMPLETED);

        execution.execute(pipeline).get();

        assertTrue(tracker.verify());

    }

    @Test
    public void testPipelineInPipeline() throws Exception {
        /**
         * +-----------------------------------------+
         * |          +-------------------+          |
         * |  +---+   |   +---+   +---+   |   +---+  |
         * |  | a | < |   | b | < | c |   | < | d |  |
         * |  +---+   |   +---+   +---+   |   +---+  |
         * |          +-------------------+          |
         * +-----------------------------------------+
         */

        // Prepare objects
        Pipeline master = new Pipeline("master");
        Pipeline inner = new Pipeline("inner");

        UnitOfWork a = new UnitOfWork(new EmptyJob(), "a");
        UnitOfWork b = new UnitOfWork(new EmptyJob(), "b");
        UnitOfWork c = new UnitOfWork(new EmptyJob(), "c");
        UnitOfWork d = new UnitOfWork(new EmptyJob(), "d");

        // Build inner pipeline first
        inner.add(b, c);

        // Then build master Pipeline
        master.add(a, inner, d);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(a, State.COMPLETED);
        tracker.addStep(b, State.COMPLETED);
        tracker.addStep(c, State.COMPLETED);
        tracker.addStep(inner, State.COMPLETED);
        tracker.addStep(d, State.COMPLETED);
        tracker.addStep(master, State.COMPLETED);

        execution.execute(master).get();

        assertTrue(tracker.verify());

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
         * +---------------------------------+
         * |          +-------------------+  |
         * |  +---+   |   +---+   +---+   |  |
         * |  | a | < |   | b | < | c |   |  |
         * |  +---+   |   +---+   +---+   |  |
         * |          +-------------------+  |
         * +---------------------------------+
         */

        // Prepare objects
        Pipeline global = new Pipeline();
        final Pipeline master = new Pipeline("master");

        final UnitOfWork c = new UnitOfWork(new EmptyJob(), "c");

        UnitOfWork a = new UnitOfWork(new Job() {
            @Override
            public void execute(TaskContext context) throws Exception {
                context.find(pipeline("../master")).add(c);
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

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(c, State.COMPLETED);

        execution.execute(global).get();

        assertTrue(tracker.verify());

    }


}
