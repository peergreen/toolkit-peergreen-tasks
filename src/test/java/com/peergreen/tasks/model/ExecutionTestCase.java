package com.peergreen.tasks.model;

import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.runtime.Job;
import com.peergreen.tasks.runtime.JobContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.peergreen.tasks.model.requirement.Requirements.completed;
import static com.peergreen.tasks.model.requirement.Requirements.failed;
import static org.mockito.Mockito.doThrow;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionTestCase {

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

        pipeline.addTask(task0);
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, pipeline);

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(aExpectations.passed);
        assertTrue(bExpectations.passed);

    }

    @Test
    public void testTwoParallelPipelinesWhereFirstTaskOfFirstPipelineDependsOnLastTaskOfSecondPipeline() throws Exception {

        /**
         * Pipelines description (A > B means A depends on B):
         * TA0 < TA1 < TA2
         *  |
         *  ------
         *        v
         * TB0 < TB1
         */
        Pipeline pipelineA = new Pipeline("pipeline-a");
        Pipeline pipelineB = new Pipeline("pipeline-b");

        final Task taskB0 = new UnitOfWork(new EmptyJob(), "task-b-0");

        ExpectationsJob b1Expectations = new ExpectationsJob(
                new StateExpectation(taskB0, State.COMPLETED)
        );
        final Task taskB1 = new UnitOfWork(b1Expectations, "task-b-1");

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        ExpectationsJob a0Expectations = new ExpectationsJob(
                new StateExpectation(taskB1, State.COMPLETED)
        );
        final Task taskA0 = new UnitOfWork(a0Expectations, "task-a-0");

        ExpectationsJob a1Expectations = new ExpectationsJob(
                new StateExpectation(taskA0, State.COMPLETED)
        );
        final Task taskA1 = new UnitOfWork(a1Expectations, "task-a-1");

        ExpectationsJob a2Expectations = new ExpectationsJob(
                new StateExpectation(taskA1, State.COMPLETED)
        );
        final Task taskA2 = new UnitOfWork(a2Expectations, "task-a-2");

        pipelineA.addTask(taskA0);
        pipelineA.addTask(taskA1);
        pipelineA.addTask(taskA2);

        // Add dependency
        taskA0.getRequirements().add(completed(taskB1));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, pipelineA, pipelineB);

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(b1Expectations.passed);
        assertTrue(a0Expectations.passed);
        assertTrue(a1Expectations.passed);
        assertTrue(a2Expectations.passed);

    }

    @Test
    public void testTwoParallelPipelinesWhereTheirSecondTaskAreWaitingForTheOtherPipelineFirstTasks() throws Exception {

        /**
         * Pipelines description (A > B means A depends on B):
         * TA0 < TA1
         *     X
         * TB0 < TB1
         */

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

    @Test
    public void testPipelineStateIsUpdated() throws Exception {
        Pipeline pipeline = new Pipeline();

        // Add a sleep task
        pipeline.addTask(new UnitOfWork(new SleepJob(500)));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, pipeline);

        // Before execution, the pipeline is WAITING
        assertEquals(pipeline.getState(), State.WAITING);
        execution.start();
        // Just after, it is RUNNING
        assertEquals(pipeline.getState(), State.RUNNING);

        // Wait 1 second, the sleep task should have been executed
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        // When tasks have been executed, the new state is COMPLETED
        assertEquals(pipeline.getState(), State.COMPLETED);

    }

    @Test
    public void testTaskIsExecutedWhenPreviousIsFailed() throws Exception {

        /**
         *           ok  +---+
         *             --| b |
         *   +---+    /  +---+
         *   | a | <--
         *   +---+    \  +---+
         *             --| c |
         *           ko  +---+
         */

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
    }

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
        inner.addTask(b);
        inner.addTask(c);

        // Then build master Pipeline
        master.addTask(a);
        master.addTask(inner);
        master.addTask(d);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, master);

        execution.start();

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

    private static class ExpectationsJob implements Job {
        public boolean passed;
        private Collection<Expectation> expectations;

        public ExpectationsJob(Expectation expectation) {
            this(Collections.singleton(expectation));
        }

        public ExpectationsJob(Expectation... expectations) {
            this(Arrays.asList(expectations));
        }

        public ExpectationsJob(Collection<Expectation> expectations) {
            this.expectations = expectations;
        }

        @Override
        public void execute(JobContext context) {
            passed = true;
            for (Expectation expectation : expectations) {
                if (!expectation.verify()) {
                    passed = false;
                    return;
                }
            }
        }
    }

    private static interface Expectation {
        boolean verify();
    }

    public static class StateExpectation implements Expectation {

        private Task task;
        private State state;

        public StateExpectation(Task task, State state) {
            this.task = task;
            this.state = state;
        }

        @Override
        public boolean verify() {
            return task.getState() == state;
        }
    }


}
