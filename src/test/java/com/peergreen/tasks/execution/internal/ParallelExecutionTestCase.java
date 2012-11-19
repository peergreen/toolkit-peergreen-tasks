package com.peergreen.tasks.execution.internal;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.model.Job;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.SleepExpectation;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.job.FailingJob;
import com.peergreen.tasks.model.job.HolderJob;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        TaskExecutorService execution = new TaskExecutorService(new ExecutorServiceBuilderManager(executorService));

        execution.execute(parallel);

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(zeroExpectations.passed);
        assertTrue(oneExpectations.passed);
        assertTrue(twoExpectations.passed);

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
        TaskExecutorService execution = new TaskExecutorService(new ExecutorServiceBuilderManager(executorService));

        execution.execute(parallel);

        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(a.getState(), State.COMPLETED);
        assertEquals(b.getState(), State.FAILED);
        assertEquals(c.getState(), State.COMPLETED);
        assertEquals(parallel.getState(), State.FAILED);

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
        TaskExecutorService execution = new TaskExecutorService(new ExecutorServiceBuilderManager(executorService));

        execution.execute(global);

        // Wait for some time
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(c.getState(), State.COMPLETED);

    }


}


