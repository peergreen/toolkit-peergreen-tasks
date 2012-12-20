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

package com.peergreen.tasks.execution.tracker.time;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.SleepJob;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class ElapsedTimeTaskTrackerTestCase {

    @Test
    public void testElapsedTime() throws Exception {

        Duration sequential = execute(newParallel(), 1);

        // Re-run the same Pipeline, with 2 Threads
        // Pipeline execution time should be less than the first execution

        Duration parallel = execute(newParallel(), 2);

        assertTrue(parallel.value < sequential.value);


    }

    private Duration execute(Parallel parallel, int executors) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(executors);
        ExecutorServiceBuilderManager manager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(manager);

        TrackerManager trackerManager = new TrackerManager();
        manager.setTrackerManager(trackerManager);

        final Duration d = new Duration();

        TimesVisitor visitor = new TimesVisitor() {
            @Override
            public void visitDuration(LiveTask task, long duration) {
                if ("pipeline".equals(task.getModel().getName())) {
                    d.value = duration;
                    //System.out.printf("Terminated in %d%n", d.value);
                }
            }
        };

        trackerManager.registerTracker(new ElapsedTimeTaskTracker(visitor));

        execution.execute(parallel).get();

        return d;
    }

    private Parallel newParallel() {
        Parallel parallel = new Parallel("pipeline");

        UnitOfWork task0 = new UnitOfWork(new SleepJob(10), "task-0");
        UnitOfWork task1 = new UnitOfWork(new SleepJob(20), "task-1");
        UnitOfWork task2 = new UnitOfWork(new SleepJob(30), "task-2");

        parallel.add(task0);
        parallel.add(task1);
        parallel.add(task2);
        return parallel;
    }

    private static final class Duration {
        public long value;
    }

}
