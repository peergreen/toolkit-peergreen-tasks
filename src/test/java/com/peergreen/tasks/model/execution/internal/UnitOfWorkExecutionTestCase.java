package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.execution.RootExecution;
import com.peergreen.tasks.model.job.SleepJob;
import com.peergreen.tasks.model.util.Executions;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/10/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class UnitOfWorkExecutionTestCase {

    public static final int N_THREADS = 2;

    @Test
    public void testSimpleExecution() throws Exception {
        UnitOfWork unitOfWork = new UnitOfWork(new SleepJob(500));

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        RootExecution execution = Executions.newRootExecution(executorService, unitOfWork);


        assertEquals(unitOfWork.getState(), State.WAITING);
        execution.execute();
        assertEquals(unitOfWork.getState(), State.SCHEDULED);
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals(unitOfWork.getState(), State.COMPLETED);

    }

}


