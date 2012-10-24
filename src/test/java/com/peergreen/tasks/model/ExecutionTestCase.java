package com.peergreen.tasks.model;

import com.peergreen.tasks.runtime.Job;
import com.peergreen.tasks.runtime.JobContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionTestCase {

    @Test
    public void testSequentialExecution() throws Exception {
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
                Assert.assertEquals(task0.getState(), State.COMPLETED);
                System.out.printf("Task 2 Execution%n");
            }
        });
        Task task2 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                Assert.assertEquals(task1.getState(), State.RUNNING);
                System.out.printf("Task 3 Execution%n");
            }
        });

        pipeline.addTask(task0);
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Execution execution = new Execution(executorService, pipeline);

        execution.start();

    }

    @Test
    public void testParallelExecution() throws Exception {
        Pipeline pipelineA = new Pipeline();
        Pipeline pipelineB = new Pipeline();

        Task taskA0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A0 Execution%n");
            }
        });
        Task taskA1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A1 Execution%n");
            }
        });
        Task taskA2 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A2 Execution%n");
            }
        });

        pipelineA.addTask(taskA0);
        pipelineA.addTask(taskA1);
        pipelineA.addTask(taskA2);

        Task taskB0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B0 Execution%n");
            }
        });
        Task taskB1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B1 Execution%n");
            }
        });

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        taskA0.getDependencies().add(taskB1);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Execution execution = new Execution(executorService, Arrays.asList(pipelineA, pipelineB));

        execution.start();

    }

    @Test
    public void testExecutionOfWaitingTasks() throws Exception {
        Pipeline pipelineA = new Pipeline();
        Pipeline pipelineB = new Pipeline();

        Task taskA0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A0 Execution%n");
            }
        });
        Task taskA1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task A1 Execution%n");
            }
        });

        pipelineA.addTask(taskA0);
        pipelineA.addTask(taskA1);

        Task taskB0 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B0 Execution%n");
            }
        });
        Task taskB1 = new Task(new Job() {
            @Override
            public void execute(JobContext context) {
                System.out.printf("Task B1 Execution%n");
            }
        });

        pipelineB.addTask(taskB0);
        pipelineB.addTask(taskB1);

        taskA1.getDependencies().add(taskB0);
        taskB1.getDependencies().add(taskA0);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Execution execution = new Execution(executorService, Arrays.asList(pipelineA, pipelineB));

        execution.start();

    }
}
