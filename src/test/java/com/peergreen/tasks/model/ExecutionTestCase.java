package com.peergreen.tasks.model;

import com.peergreen.tasks.runtime.Job;
import com.peergreen.tasks.runtime.JobContext;
import com.peergreen.tasks.runtime.JobException;
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
import static org.mockito.Mockito.when;
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
        final StringBuffer sb = new StringBuffer();
        Pipeline pipeline = new Pipeline();
        final Task task0 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task 1 Execution%n");
            }
        });
        final Task task1 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task 1] Task 0 is " + task0.getState().name() + "\n");
                System.out.printf("Task 2 Execution%n");
            }
        });
        Task task2 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task 2] Task 1 is " + task1.getState().name() + "\n");
                System.out.printf("Task 3 Execution%n");
            }
        });

        pipeline.addTask(task0);
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, pipeline);

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(sb.toString().contains("[Task 1] Task 0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task 2] Task 1 is COMPLETED"));

    }

    @Test
    public void testParallelExecution() throws Exception {

        final StringBuffer sb = new StringBuffer();

        /**
         * Pipelines description (A > B means A depends on B):
         * TA0 < TA1 < TA2
         *  |
         *  ------
         *        v
         * TB0 < TB1
         */
        Pipeline pipelineA = new Pipeline();
        Pipeline pipelineB = new Pipeline();

        final Task taskB0 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B0 Execution%n");
            }
        });
        final Task taskB1 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task B1] Task B0 is " + taskB0.getState().name() + "\n");
                System.out.printf("Task B1 Execution%n");
            }
        });

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        final Task taskA0 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A0] Task B1 is " + taskB1.getState().name() + "\n");
                System.out.printf("Task A0 Execution%n");
            }
        });
        final Task taskA1 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A1] Task A0 is " + taskA0.getState().name() + "\n");
                System.out.printf("Task A1 Execution%n");
            }
        });
        final Task taskA2 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A2] Task A1 is " + taskA0.getState().name() + "\n");
                System.out.printf("Task A2 Execution%n");
            }
        });

        pipelineA.addTask(taskA0);
        pipelineA.addTask(taskA1);
        pipelineA.addTask(taskA2);

        // Add dependency
        taskA0.getRequirements().add(completed(taskB1));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, Arrays.asList(pipelineA, pipelineB));

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(sb.toString().contains("[Task B1] Task B0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task A0] Task B1 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task A1] Task A0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task A2] Task A1 is COMPLETED"));


    }

    @Test
    public void testExecutionOfWaitingTasks() throws Exception {

        final StringBuffer sb = new StringBuffer();

        /**
         * Pipelines description (A > B means A depends on B):
         * TA0 < TA1
         *     X
         * TB0 < TB1
         */

        Pipeline pipelineA = new Pipeline();
        Pipeline pipelineB = new Pipeline();

        final Task taskA0 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A0 Execution%n");
            }
        });

        final Task taskB0 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B0 Execution%n");
            }
        });

        final Task taskA1 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A1] Task A0 is " + taskA0.getState().name() + "\n");
                sb.append("[Task A1] Task B0 is " + taskB0.getState().name() + "\n");
                System.out.printf("Task A1 Execution%n");
            }
        });

        final Task taskB1 = new UnitOfWork(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task B1] Task A0 is " + taskA0.getState().name() + "\n");
                sb.append("[Task B1] Task B0 is " + taskB0.getState().name() + "\n");
                System.out.printf("Task B1 Execution%n");
            }
        });

        pipelineA.addTask(taskA0);
        pipelineA.addTask(taskA1);

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        taskA1.getRequirements().add(completed(taskB0));
        taskB1.getRequirements().add(completed(taskA0));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, Arrays.asList(pipelineA, pipelineB));

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(sb.toString().contains("[Task A1] Task A0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task A1] Task B0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task B1] Task A0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task B1] Task B0 is COMPLETED"));

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

        Pipeline master = new Pipeline("master");

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
        master.addTask(b, false); // do not auto-link to previous
        master.addTask(c, false); // do not auto-link to previous

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

    private static class SleepJob implements Job {
        private long time;

        public SleepJob(long time) {
            this.time = time;
        }

        @Override
        public void execute(JobContext context) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                // Ignored
            }
        }
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
