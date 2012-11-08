package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.SleepExpectation;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.job.HolderJob;
import com.peergreen.tasks.model.state.State;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        HolderJob zero = new HolderJob();
        HolderJob one = new HolderJob();
        HolderJob two = new HolderJob();
        final Task task0 = new UnitOfWork(zero, "task-0");
        final Task task1 = new UnitOfWork(one, "task-1");
        final Task task2 = new UnitOfWork(two, "task-2");

        parallel.add(task0);
        parallel.add(task1);
        parallel.add(task2);

        ExpectationsJob zeroExpectations = new ExpectationsJob(
                new SleepExpectation(100),
                new StateExpectation(task1, State.RUNNING),
                new StateExpectation(task2, State.RUNNING),
                new SleepExpectation(400)
        );
        ExpectationsJob oneExpectations = new ExpectationsJob(
                new SleepExpectation(100),
                new StateExpectation(task0, State.RUNNING),
                new StateExpectation(task2, State.RUNNING),
                new SleepExpectation(400)
        );
        ExpectationsJob twoExpectations = new ExpectationsJob(
                new SleepExpectation(100),
                new StateExpectation(task0, State.RUNNING),
                new StateExpectation(task1, State.RUNNING),
                new SleepExpectation(400)
        );

        zero.job = zeroExpectations;
        one.job = oneExpectations;
        two.job = twoExpectations;

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ParallelExecution execution = new ParallelExecution(executorService, parallel);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(zeroExpectations.passed);
        assertTrue(oneExpectations.passed);
        assertTrue(twoExpectations.passed);

    }

}


