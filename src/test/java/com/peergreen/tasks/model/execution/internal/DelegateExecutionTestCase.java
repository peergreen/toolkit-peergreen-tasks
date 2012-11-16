package com.peergreen.tasks.model.execution.internal;

import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.execution.RootExecution;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.util.Executions;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/11/12
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class DelegateExecutionTestCase {

    @Test
    public void testNormalDelegateExecution() throws Exception {
        Delegate delegate = new Delegate();
        delegate.setDelegate(new UnitOfWork(new EmptyJob()));

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        RootExecution execution = Executions.newRootExecution(executorService, delegate);

        execution.execute();

        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertEquals(delegate.getState(), State.COMPLETED);
        assertEquals(delegate.getDelegate().getState(), State.COMPLETED);
    }

    @Test
    public void testEmptyDelegateExecution() throws Exception {
        Delegate delegate = new Delegate();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        RootExecution execution = Executions.newRootExecution(executorService, delegate);

        execution.execute();

        assertEquals(delegate.getState(), State.COMPLETED);
    }
}
