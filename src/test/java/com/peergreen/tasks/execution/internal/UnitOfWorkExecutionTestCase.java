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

import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.SequenceTracker;
import com.peergreen.tasks.model.job.SleepJob;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecutionTestCase {

    public static final int N_THREADS = 2;

    @Test
    public void testSimpleExecution() throws Exception {
        UnitOfWork unitOfWork = new UnitOfWork(new SleepJob(100));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        SequenceTracker tracker = new SequenceTracker();
        manager.registerTracker(tracker);
        tracker.addStep(unitOfWork, State.SCHEDULED);
        tracker.addStep(unitOfWork, State.RUNNING);
        tracker.addStep(unitOfWork, State.COMPLETED);

        Future<?> future = execution.execute(unitOfWork);
        future.get();

        assertTrue(tracker.verify());
    }

}


