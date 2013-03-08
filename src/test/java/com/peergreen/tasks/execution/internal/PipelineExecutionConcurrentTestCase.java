/*
 * Copyright 2013 Peergreen S.A.S.
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

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.StateTracker;
import com.peergreen.tasks.model.job.AddEmptyJob;
import com.peergreen.tasks.tree.TaskRenderingVisitor;

/**
 * Adds unitOfWork from a parallel task into a pipeline task
 * @author Florent Benoit
 */
public class PipelineExecutionConcurrentTestCase  {

    // Need at least 10 executors so that all jobs are executed concurrently
    public static final int N_THREADS = 10;

    private ExecutorService executorService;

    @BeforeClass
    void setup() {
        this.executorService = Executors.newFixedThreadPool(N_THREADS);
    }

    @AfterClass
    void shutdown() {
        this.executorService.shutdown();
    }


    /**
     * Adds unitOfWork from a parallel task into a subparallel task
     * @throws Exception
     */
    //@Test(threadPoolSize = 1, invocationCount = 20)
    public void testConcurrentExecution() throws Exception {

        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        Pipeline pipeline = new Pipeline();
        Parallel prepareParallel = new Parallel("Prepare");
        Pipeline doParallel = new Pipeline("Do");
        Parallel postParallel = new Parallel("After");
        pipeline.add(prepareParallel);
        pipeline.add(doParallel);
        pipeline.add(postParallel);

        List<Task> tasksCreated = new ArrayList<Task>();

        for (int i = 0; i < 200; i++) {
            AddEmptyJob job = new AddEmptyJob(doParallel);
            tasksCreated.add(job.getCreatedTask());

            final Task unitOfWork = new UnitOfWork(job);
            prepareParallel.add(unitOfWork);
            tasksCreated.add(unitOfWork);
        }


        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        StateTracker tracker = new StateTracker();
        manager.registerTracker(tracker);

        TaskRenderingVisitor taskRenderingVisitor = new TaskRenderingVisitor();
        manager.registerTracker(taskRenderingVisitor);

        //new Thread(this).start();

        Future<State> future = execution.execute(pipeline);

        // Wait completion until 10 seconds (it should have finished soon. Not finished if there is a bug)
        future.get(10, TimeUnit.SECONDS);
        for (Task task : tasksCreated) {
            assertEquals(tracker.getState(task), State.COMPLETED);
        }
        assertEquals(tracker.getState(pipeline), State.COMPLETED);
        assertEquals(tracker.getState(prepareParallel), State.COMPLETED);
        assertEquals(tracker.getState(doParallel), State.COMPLETED);
        assertEquals(tracker.getState(postParallel), State.COMPLETED);
    }




}