package com.peergreen.tasks.model;

import com.peergreen.tasks.runtime.Job;
import com.peergreen.tasks.runtime.JobContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
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
public class ExecutionTestCase {


    public static final int N_THREADS = 2;

    @Test
    public void testSequentialExecution() throws Exception {
        final StringBuffer sb = new StringBuffer();
        Pipeline pipeline = new Pipeline();
        final Task task0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task 1 Execution%n");
            }
        });
        final Task task1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task 1] Task 0 is " + task0.getState().name() + "\n");
                System.out.printf("Task 2 Execution%n");
            }
        });
        Task task2 = new Task(new Job() {
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

        final Task taskB0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B0 Execution%n");
            }
        });
        final Task taskB1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task B1] Task B0 is " + taskB0.getState().name() + "\n");
                System.out.printf("Task B1 Execution%n");
            }
        });

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        final Task taskA0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A0] Task B1 is " + taskB1.getState().name() + "\n");
                System.out.printf("Task A0 Execution%n");
            }
        });
        final Task taskA1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A1] Task A0 is " + taskA0.getState().name() + "\n");
                System.out.printf("Task A1 Execution%n");
            }
        });
        final Task taskA2 = new Task(new Job() {
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
        taskA0.getDependencies().add(taskB1);

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

        final Task taskA0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A0 Execution%n");
            }
        });

        final Task taskB0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B0 Execution%n");
            }
        });

        final Task taskA1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                sb.append("[Task A1] Task A0 is " + taskA0.getState().name() + "\n");
                sb.append("[Task A1] Task B0 is " + taskB0.getState().name() + "\n");
                System.out.printf("Task A1 Execution%n");
            }
        });

        final Task taskB1 = new Task(new Job() {
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

        taskA1.getDependencies().add(taskB0);
        taskB1.getDependencies().add(taskA0);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Execution execution = new Execution(executorService, Arrays.asList(pipelineA, pipelineB));

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(sb.toString().contains("[Task A1] Task A0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task A1] Task B0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task B1] Task A0 is COMPLETED"));
        assertTrue(sb.toString().contains("[Task B1] Task B0 is COMPLETED"));

    }
}
