package com.peergreen.tasks.execution.helper;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.SleepJob;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

import static org.testng.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/12/12
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class TaskExecutorServiceTestCase {
    @Test
    public void testFutureWaitTermination() throws Exception {


        /**
         * +---------------------------+
         * |   +---+   +---+   +---+   |
         * |   | a | < | b | < | c |   |
         * |   +---+   +---+   +---+   |
         * +---------------------------+
         */

        Pipeline pipeline = new Pipeline();

        Task task0 = new UnitOfWork(new SleepJob(50), "task-0");
        Task task1 = new UnitOfWork(new SleepJob(50), "task-1");
        Task task2 = new UnitOfWork(new SleepJob(50), "task-2");

        pipeline.add(task0, task1, task2);

        TaskExecutorService service = new TaskExecutorService(new ExecutorServiceBuilderManager());
        Future<State> future = service.execute(pipeline);

        // Just after execution start, the pipeline's state should be RUNNING
        assertEquals(pipeline.getState(), State.RUNNING);

        // Get should wait for termination
        future.get();

        // And all task should be completed
        assertEquals(pipeline.getState(), State.COMPLETED);
        assertEquals(task0.getState(), State.COMPLETED);
        assertEquals(task1.getState(), State.COMPLETED);
        assertEquals(task2.getState(), State.COMPLETED);

    }
}
