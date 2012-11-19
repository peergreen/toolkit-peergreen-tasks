package com.peergreen.tasks.execution.internal;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.RootExecution;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.job.FailingJob;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.util.Executions;
import com.peergreen.tasks.runtime.Job;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.peergreen.tasks.context.helper.References.pipeline;
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
        RootExecution execution = Executions.newRootExecution(executorService, pipeline);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(aExpectations.passed);
        assertTrue(bExpectations.passed);

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
        RootExecution execution = Executions.newRootExecution(executorService, pipeline);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(a.getState(), State.COMPLETED);
        assertEquals(b.getState(), State.FAILED);
        assertEquals(c.getState(), State.WAITING);
        assertEquals(pipeline.getState(), State.FAILED);

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
        RootExecution execution = Executions.newRootExecution(executorService, master);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(bExpectations.passed);
        assertTrue(cExpectations.passed);
        assertTrue(dExpectations.passed);

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

        ExpectationsJob cExpectations = new ExpectationsJob(
                new StateExpectation(stage1, State.COMPLETED)
        );

        Task c = new UnitOfWork(cExpectations, "task-c");

        ExpectationsJob dExpectations = new ExpectationsJob(
                new StateExpectation(stage1, State.COMPLETED)
        );

        Task d = new UnitOfWork(dExpectations, "task-d");

        stage1.add(a);
        stage1.add(b);

        stage2.add(c);
        stage2.add(d);

        pipeline.add(stage1, stage2);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        RootExecution execution = Executions.newRootExecution(executorService, pipeline);

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(cExpectations.passed);
        assertTrue(dExpectations.passed);

    }

    @Test
    public void testPipelineStateIsUpdated() throws Exception {
        Pipeline pipeline = new Pipeline();

        // Add a sleep task
        pipeline.add(new UnitOfWork(new SleepJob(500)));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        RootExecution execution = Executions.newRootExecution(executorService, pipeline);

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
        RootExecution execution = Executions.newRootExecution(executorService, master);

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
        RootExecution execution = Executions.newRootExecution(executorService, global);

        execution.execute();

        // Wait for some time
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(c.getState(), State.COMPLETED);

    }


}
