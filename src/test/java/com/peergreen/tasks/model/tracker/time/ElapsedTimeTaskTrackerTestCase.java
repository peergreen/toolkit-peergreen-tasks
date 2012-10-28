package com.peergreen.tasks.model.tracker.time;

import com.peergreen.tasks.model.Execution;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.tracker.state.StateTaskTracker;
import com.peergreen.tasks.runtime.Job;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class ElapsedTimeTaskTrackerTestCase {

    @Test
    public void testElapsedTime() throws Exception {

        Duration sequential = execute(newPipeline(), 1);

        // Re-run the same Pipeline, with 2 Threads
        // Pipeline execution time should be less than the first execution

        Duration parallel = execute(newPipeline(), 2);

        assertTrue(parallel.value < sequential.value);


    }

    private Duration execute(Pipeline pipeline, int executors) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(executors);
        Execution execution = new Execution(executorService, pipeline);

        final Duration d = new Duration();

        TimesVisitor visitor = new TimesVisitor() {
            @Override
            public void visitDuration(Task task, long duration) {
                if ("pipeline".equals(task.getName())) {
                    d.value = duration;
                    //System.out.printf("Terminated in %d%n", d.value);
                }
            }
        };

        execution.getTrackerManager().registerTracker(new ElapsedTimeTaskTracker(visitor));

        execution.start();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        return d;
    }

    private Pipeline newPipeline() {
        Pipeline pipeline = new Pipeline("pipeline");

        UnitOfWork task0 = new UnitOfWork(new SleepJob(100), "task-0");
        UnitOfWork task1 = new UnitOfWork(new SleepJob(200), "task-1");
        UnitOfWork task2 = new UnitOfWork(new SleepJob(300), "task-2");

        pipeline.addTask(task0);
        pipeline.addTask(task1);
        pipeline.addTask(task2, false); // task 2 can run concurrently of the other tasks
        return pipeline;
    }

    private static final class Duration {
        public long value;
    }

}
