/**
 * Copyright 2012-2013 Peergreen S.A.S. All rights reserved.
 * Proprietary and confidential.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.execution.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.annotations.Test;

import com.peergreen.tasks.execution.ErrorHandler;
import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.SequenceTracker;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.FailingJob;
import com.peergreen.tasks.model.job.SleepJob;

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


    @Test
    public void testFailingUnitOfWork() throws Exception {
        UnitOfWork pass = new UnitOfWork(new EmptyJob(), "pass");
        UnitOfWork fail = new UnitOfWork(new FailingJob(), "fail");
        Pipeline pipeline = new Pipeline();
        pipeline.add(pass, fail);

        ErrorHandlerVerifier handler = new ErrorHandlerVerifier();

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(executorService, handler);
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        execution.execute(pipeline).get();

        // Called only once
        assertEquals(handler.getTimes(), 1);
        assertEquals(handler.getTask().getName(), "fail");
    }

    private static class ErrorHandlerVerifier implements ErrorHandler {

        private Task task;
        private int times = 0;

        @Override
        public void onError(LiveTask liveTask, Throwable throwable) {
            this.task = liveTask.getModel();
            this.times++;
        }

        public Task getTask() {
            return task;
        }

        public int getTimes() {
            return times;
        }
    }
}


