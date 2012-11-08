package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.runtime.Job;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecutionTestCase {

    public static final int N_THREADS = 2;

    @Mock
    private Job job;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSequentialExecution() throws Exception {
        Pipeline pipeline = new Pipeline();

        final Task task0 = new UnitOfWork(new EmptyJob(), "task-0");

        ExpectationsJob aExpectations = new ExpectationsJob(
                new StateExpectation(task0, State.COMPLETED)
        );
        final Task task1 = new UnitOfWork(aExpectations, "task-1");

        ExpectationsJob bExpectations = new ExpectationsJob(
                new StateExpectation(task1, State.COMPLETED)
        );
        Task task2 = new UnitOfWork(bExpectations, "task-2");

        pipeline.add(task0);
        pipeline.add(task1);
        pipeline.add(task2);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        PipelineExecution execution = new PipelineExecution(executorService, pipeline);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(aExpectations.passed);
        assertTrue(bExpectations.passed);

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

        final Task taskA = new UnitOfWork(new EmptyJob(), "task-a");

        ExpectationsJob bExpectations = new ExpectationsJob(
                new StateExpectation(taskA, State.COMPLETED)
        );
        final Task taskB = new UnitOfWork(bExpectations, "task-b");

        one.add(taskA);
        one.add(taskB);

        ExpectationsJob cExpectations = new ExpectationsJob(
                new StateExpectation(one, State.COMPLETED)
        );
        final Task taskC = new UnitOfWork(cExpectations, "task-c");

        ExpectationsJob dExpectations = new ExpectationsJob(
                new StateExpectation(taskC, State.COMPLETED)
        );
        final Task taskD = new UnitOfWork(dExpectations, "task-d");

        two.add(taskC);
        two.add(taskD);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        PipelineExecution execution = new PipelineExecution(executorService, master);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(bExpectations.passed);
        assertTrue(cExpectations.passed);
        assertTrue(dExpectations.passed);

    }

    /*
    @Test
    public void testTwoParallelPipelinesWhereTheirSecondTaskAreWaitingForTheOtherPipelineFirstTasks() throws Exception {

//        *
//         * Pipelines description (A > B means A depends on B):
//         * TA0 < TA1
//         *     X
//         * TB0 < TB1

        Pipeline pipelineA = new Pipeline("pipeline-a");
        Pipeline pipelineB = new Pipeline("pipeline-b");

        final Task taskA0 = new UnitOfWork(new EmptyJob(), "task-a-0");
        final Task taskB0 = new UnitOfWork(new EmptyJob(), "task-b-0");

        ExpectationsJob a1Expectations = new ExpectationsJob(
                new StateExpectation(taskA0, State.COMPLETED),
                new StateExpectation(taskB0, State.COMPLETED)
        );

        final Task taskA1 = new UnitOfWork(a1Expectations, "task-a-1");

        ExpectationsJob b1Expectations = new ExpectationsJob(
                new StateExpectation(taskA0, State.COMPLETED),
                new StateExpectation(taskB0, State.COMPLETED)
        );

        final Task taskB1 = new UnitOfWork(b1Expectations, "task-b-1");

        pipelineA.addTask(taskA0);
        pipelineA.addTask(taskA1);

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        taskA1.getRequirements().add(completed(taskB0));
        taskB1.getRequirements().add(completed(taskA0));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, pipelineA, pipelineB);

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(a1Expectations.passed);
        assertTrue(b1Expectations.passed);

    }
    */

    @Test
    public void testPipelineStateIsUpdated() throws Exception {
        Pipeline pipeline = new Pipeline();

        // Add a sleep task
        pipeline.add(new UnitOfWork(new SleepJob(500)));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        PipelineExecution execution = new PipelineExecution(executorService, pipeline);

        // Before execution, the pipeline is WAITING
        assertEquals(pipeline.getState(), State.WAITING);
        execution.execute();
        // Just after, it is RUNNING
        assertEquals(pipeline.getState(), State.RUNNING);

        // Wait 1 second, the sleep task should have been executed
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        // When tasks have been executed, the new state is COMPLETED
        assertEquals(pipeline.getState(), State.COMPLETED);

    }

    /*
    @Test
    public void testTaskIsExecutedWhenPreviousIsFailed() throws Exception {

//        *
//         *           ok  +---+
//         *             --| b |
//         *   +---+    /  +---+
//         *   | a | <--
//         *   +---+    \  +---+
//         *             --| c |
//         *           ko  +---+

        Parallel master = new Parallel("master");

        UnitOfWork a = new UnitOfWork(job, "a");

        ExpectationsJob bJob = new ExpectationsJob(
                new StateExpectation(a, State.COMPLETED)
        );
        UnitOfWork b = new UnitOfWork(bJob, "b");

        ExpectationsJob cJob = new ExpectationsJob(
                new StateExpectation(a, State.FAILED)
        );
        UnitOfWork c = new UnitOfWork(cJob, "c");

        master.addTask(a);
        master.addTask(b);
        master.addTask(c);

        // Manually manage links
        b.getRequirements().add(completed(a));
        c.getRequirements().add(failed(a));

        doThrow(new RuntimeException(""))
                .when(job).execute(null);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, master);

        execution.start();

        // Wait for some time
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertTrue(cJob.passed);
        assertEquals(master.getState(), State.FAILED);
        assertEquals(a.getState(), State.FAILED);
        assertEquals(b.getState(), State.WAITING);
        assertEquals(c.getState(), State.COMPLETED);
    }  */

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

        ExpectationsJob aJob = new ExpectationsJob(
                new StateExpectation(master, State.RUNNING)
        );
        UnitOfWork a = new UnitOfWork(aJob, "a");

        ExpectationsJob bJob = new ExpectationsJob(
                new StateExpectation(inner, State.RUNNING),
                new StateExpectation(a, State.COMPLETED)
        );
        UnitOfWork b = new UnitOfWork(bJob, "b");

        ExpectationsJob cJob = new ExpectationsJob(
                new StateExpectation(inner, State.RUNNING),
                new StateExpectation(b, State.COMPLETED)
        );
        UnitOfWork c = new UnitOfWork(cJob, "c");

        ExpectationsJob dJob = new ExpectationsJob(
                new StateExpectation(inner, State.COMPLETED)
        );
        UnitOfWork d = new UnitOfWork(dJob, "d");

        // Build inner pipeline first
        inner.add(b, c);

        // Then build master Pipeline
        master.add(a, inner, d);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        PipelineExecution execution = new PipelineExecution(executorService, master);

        execution.execute();

        // Wait for some time
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(inner.getState(), State.COMPLETED);
        assertEquals(master.getState(), State.COMPLETED);

        // Assertions verification
        assertTrue(aJob.passed);
        assertTrue(bJob.passed);
        assertTrue(cJob.passed);
        assertTrue(dJob.passed);

    }


}
