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
import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.ExpectationTracker;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.EmptyJob;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/11/12
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class DelegateExecutionTestCase {

    @Test
    public void testNormalDelegateExecution() throws Exception {
        Delegate<UnitOfWork> delegate = new Delegate<UnitOfWork>();
        delegate.setDelegate(new UnitOfWork(new EmptyJob()));

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        ExpectationTracker tracker = new ExpectationTracker();
        manager.registerTracker(tracker);
        tracker.addExpectation(new StateExpectation(delegate, State.COMPLETED));
        tracker.addExpectation(new StateExpectation(delegate.getDelegate(), State.COMPLETED));

        execution.execute(delegate).get();

        assertTrue(tracker.verify());
    }

    @Test
    public void testEmptyDelegateExecution() throws Exception {
        Delegate<?> delegate = new Delegate<Task>();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        ExpectationTracker tracker = new ExpectationTracker();
        manager.registerTracker(tracker);
        tracker.addExpectation(new StateExpectation(delegate, State.COMPLETED));

        execution.execute(delegate).get();

        assertTrue(tracker.verify());
    }
}
