package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.ArousableTask;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.execution.RootExecution;
import com.peergreen.tasks.model.expect.SleepExpectation;
import com.peergreen.tasks.model.expect.StateExpectation;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.job.FailingJob;
import com.peergreen.tasks.model.job.HolderJob;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.util.Executions;
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
public class ArousableExecutionTestCase {

    public static final int N_THREADS = 2;

    @Test
    public void testTaskAwakening() throws Exception {

        Task delayedTask = new UnitOfWork(new SleepJob(100), "delayed");
        ArousableTask arousable = new ArousableTask(delayedTask);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        RootExecution execution = Executions.newRootExecution(executorService, arousable);

        execution.execute();

        assertEquals(arousable.getState(), State.SCHEDULED);
        assertEquals(delayedTask.getState(), State.WAITING);

        Thread.sleep(200);

        arousable.wakeUp();

        assertEquals(arousable.getState(), State.RUNNING);
        assertTrue((delayedTask.getState() == State.SCHEDULED) || (delayedTask.getState() == State.RUNNING));

        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(arousable.getState(), State.COMPLETED);
        assertEquals(delayedTask.getState(), State.COMPLETED);

    }

    @Test
    public void testTaskAwakeningWithFailure() throws Exception {

        Task delayedTask = new UnitOfWork(new FailingJob(), "delayed");
        ArousableTask arousable = new ArousableTask(delayedTask);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        RootExecution execution = Executions.newRootExecution(executorService, arousable);

        execution.execute();

        assertEquals(arousable.getState(), State.SCHEDULED);
        assertEquals(delayedTask.getState(), State.WAITING);

        Thread.sleep(200);

        arousable.wakeUp();

        assertEquals(arousable.getState(), State.RUNNING);
        assertTrue((delayedTask.getState() == State.SCHEDULED) || (delayedTask.getState() == State.RUNNING));

        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(arousable.getState(), State.FAILED);
        assertEquals(delayedTask.getState(), State.FAILED);

    }
}


