package com.peergreen.tasks.execution.helper;

import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.ExpectationTracker;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.SleepJob;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/12/12
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class TaskExecutorServiceTestCase {
    @Test
    public void testFutureWaitTermination() throws Exception {


        /**
         * +---------------------------+
         * |   +---+   +---+   +---+   |
         * |   | a | < | b | < | c |   |
         * |   +---+   +---+   +---+   |
         * +---------------------------+
         */

        Pipeline pipeline = new Pipeline();

        Task task0 = new UnitOfWork(new SleepJob(50), "task-0");
        Task task1 = new UnitOfWork(new SleepJob(50), "task-1");
        Task task2 = new UnitOfWork(new SleepJob(50), "task-2");

        pipeline.add(task0, task1, task2);

        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager();
        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        // Just after execution start, the pipeline's state should be RUNNING
        ExpectationTracker tracker1 = new ExpectationTracker();
        tracker1.addExpectation(new StateExpectation(pipeline, State.RUNNING));

        // And all task should be completed at the end
        ExpectationTracker tracker2 = new ExpectationTracker();
        tracker2.addExpectation(new StateExpectation(pipeline, State.COMPLETED));
        tracker2.addExpectation(new StateExpectation(task0, State.COMPLETED));
        tracker2.addExpectation(new StateExpectation(task1, State.COMPLETED));
        tracker2.addExpectation(new StateExpectation(task2, State.COMPLETED));

        manager.registerTracker(tracker1);
        manager.registerTracker(tracker2);

        TaskExecutorService service = new TaskExecutorService(builderManager);
        Future<State> future = service.execute(pipeline);

        assertTrue(tracker1.verify());

        // Get should wait for termination
        future.get();

        assertTrue(tracker2.verify());

    }
}
