package com.peergreen.tasks.execution.tracker.time;

import com.peergreen.tasks.execution.RootExecution;
import com.peergreen.tasks.execution.builder.TrackerManagerEnabler;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.util.Executions;
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

        Duration sequential = execute(newParallel(), 1);

        // Re-run the same Pipeline, with 2 Threads
        // Pipeline execution time should be less than the first execution

        Duration parallel = execute(newParallel(), 2);

        assertTrue(parallel.value < sequential.value);


    }

    private Duration execute(Parallel parallel, int executors) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(executors);
        RootExecution execution = Executions.newRootExecution(executorService, parallel);

        TrackerManager trackerManager = new TrackerManager();
        execution.addExecutionBuilder(new TrackerManagerEnabler(trackerManager));

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

        trackerManager.registerTracker(new ElapsedTimeTaskTracker(visitor));

        execution.execute();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        return d;
    }

    private Parallel newParallel() {
        Parallel parallel = new Parallel("pipeline");

        UnitOfWork task0 = new UnitOfWork(new SleepJob(100), "task-0");
        UnitOfWork task1 = new UnitOfWork(new SleepJob(200), "task-1");
        UnitOfWork task2 = new UnitOfWork(new SleepJob(300), "task-2");

        parallel.add(task0);
        parallel.add(task1);
        parallel.add(task2);
        return parallel;
    }

    private static final class Duration {
        public long value;
    }

}
