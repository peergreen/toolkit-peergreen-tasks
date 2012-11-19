package com.peergreen.tasks.execution.internal;

import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.model.Delegate;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.EmptyJob;
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
        Delegate<UnitOfWork> delegate = new Delegate<UnitOfWork>();
        delegate.setDelegate(new UnitOfWork(new EmptyJob()));

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        TaskExecutorService execution = new TaskExecutorService(new ExecutorServiceBuilderManager(executorService));

        execution.execute(delegate);

        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertEquals(delegate.getState(), State.COMPLETED);
        assertEquals(delegate.getDelegate().getState(), State.COMPLETED);
    }

    @Test
    public void testEmptyDelegateExecution() throws Exception {
        Delegate<?> delegate = new Delegate<Task>();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        TaskExecutorService execution = new TaskExecutorService(new ExecutorServiceBuilderManager(executorService));

        execution.execute(delegate);

        assertEquals(delegate.getState(), State.COMPLETED);
    }
}
