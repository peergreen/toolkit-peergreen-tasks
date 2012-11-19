package com.peergreen.tasks.model.group;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.execution.RootExecution;
import com.peergreen.tasks.model.expect.PropertyExpectation;
import com.peergreen.tasks.model.expect.PropertyNotSetExpectation;
import com.peergreen.tasks.model.job.ExpectationsJob;
import com.peergreen.tasks.model.util.Executions;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/11/12
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class SubstituteExecutionContextProviderTestCase {
    @Test
    public void testProviderReturnsAMutableContext() throws Exception {

        // Structure
        // Pipeline #master
        // |-- Pipeline #p1 [Group #A]
        // |   |-- UnitOfWork #1
        // |   `-- UnitOfWork #2
        // |-- UnitOfWork #3 [Should not see the property "luke"]
        // `-- Pipeline #p2 [Group #A]
        //     |-- UnitOfWork #4
        //     `-- UnitOfWork #5

        ExpectationsJob j1 = new ExpectationsJob(
                new PropertyExpectation("luke", "is a jedi")
        );
        ExpectationsJob j2 = new ExpectationsJob(
                new PropertyExpectation("luke", "is a jedi")
        );
        ExpectationsJob j3 = new ExpectationsJob(
                new PropertyNotSetExpectation("luke")
        );
        ExpectationsJob j4 = new ExpectationsJob(
                new PropertyExpectation("luke", "is a jedi")
        );
        ExpectationsJob j5 = new ExpectationsJob(
                new PropertyExpectation("luke", "is a jedi")
        );

        Pipeline master = new Pipeline();
        Pipeline p1 = new Pipeline();
        Pipeline p2 = new Pipeline();
        UnitOfWork u1 = new UnitOfWork(j1);
        UnitOfWork u2 = new UnitOfWork(j2);
        UnitOfWork u3 = new UnitOfWork(j3);
        UnitOfWork u4 = new UnitOfWork(j4);
        UnitOfWork u5 = new UnitOfWork(j5);

        p2.add(u4, u5);
        p1.add(u1, u2);
        master.add(p1, u3, p2);

        Group a = new Group();
        a.addTask(p1);
        a.addTask(p2);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        RootExecution execution = Executions.newRootExecution(executorService, master);

        SubstituteExecutionContextProvider provider = new SubstituteExecutionContextProvider();
        MutableExecutionContext context = new MutableExecutionContext();
        context.setProperty("luke", "is a jedi");
        provider.addGroup(a, context);
        execution.setTaskContextFactory(new GroupTaskContextFactory(Collections.singleton(a), provider));

        execution.execute();

        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertTrue(j1.passed);
        assertTrue(j2.passed);
        assertTrue(j3.passed);
        assertTrue(j4.passed);
        assertTrue(j5.passed);

    }
}
