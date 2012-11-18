package com.peergreen.tasks.model.context;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.execution.RootExecution;
import com.peergreen.tasks.model.expect.BreadcrumbExpectation;
import com.peergreen.tasks.model.expect.ExtensionExpectation;
import com.peergreen.tasks.model.expect.PropertyExpectation;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.util.Executions;
import com.peergreen.tasks.runtime.Job;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionContextTestCase {

    @Test
    public void testExecutionContextIsSharedBetweenTasks() throws Exception {
        Pipeline pipeline = new Pipeline("pipeline");
        UnitOfWork unitOfWork1 = new UnitOfWork(new Job() {
            @Override
            public void execute(TaskContext context) throws Exception {
                context.add("hello");
                context.setProperty("obiwan", "kenobi");
                System.out.printf("%s%n", context.getBreadcrumb());
            }
        });

        ExpectationsJob job = new ExpectationsJob(
                new PropertyExpectation("obiwan", "kenobi"),
                new ExtensionExpectation(String.class, "hello")
        );
        UnitOfWork unitOfWork2 = new UnitOfWork(job);

        pipeline.add(unitOfWork1, unitOfWork2);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        RootExecution execution = Executions.newRootExecution(executorService, pipeline);

        execution.execute();

        executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
        assertTrue(job.passed);

    }

    @Test
    public void testBreadcrumbIsUpdated() throws Exception {
        Pipeline master = new Pipeline("master");
        Pipeline pipeline = new Pipeline("pipeline");
        Parallel parallel = new Parallel("parallel");
        ExpectationsJob first = new ExpectationsJob(
                new BreadcrumbExpectation("/master/pipeline/uow")
        );
        UnitOfWork uow = new UnitOfWork(first, "uow");
        ExpectationsJob second = new ExpectationsJob(
                new BreadcrumbExpectation("/master/parallel/uow2")
        );
        UnitOfWork uow2 = new UnitOfWork(second, "uow2");

        master.add(pipeline, parallel);
        pipeline.add(uow);
        parallel.add(uow2);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        RootExecution execution = Executions.newRootExecution(executorService, master);

        execution.execute();

        executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
        assertTrue(first.passed);
        assertTrue(second.passed);

    }
}
