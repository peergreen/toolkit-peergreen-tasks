/*
 * Copyright 2012 Peergreen S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.model.group;

import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.Test;

import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.expect.ExpectationTracker;
import com.peergreen.tasks.model.expect.PropertyExpectation;
import com.peergreen.tasks.model.expect.PropertyNotSetExpectation;
import com.peergreen.tasks.model.job.EmptyJob;

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

        Pipeline master = new Pipeline();
        Pipeline p1 = new Pipeline();
        Pipeline p2 = new Pipeline();
        UnitOfWork u1 = new UnitOfWork(new EmptyJob());
        UnitOfWork u2 = new UnitOfWork(new EmptyJob());
        UnitOfWork u3 = new UnitOfWork(new EmptyJob());
        UnitOfWork u4 = new UnitOfWork(new EmptyJob());
        UnitOfWork u5 = new UnitOfWork(new EmptyJob());

        p2.add(u4, u5);
        p1.add(u1, u2);
        master.add(p1, u3, p2);

        Group a = new Group();
        a.addTask(p1);
        a.addTask(p2);

        SubstituteExecutionContextProvider provider = new SubstituteExecutionContextProvider();
        MutableExecutionContext context = new MutableExecutionContext();
        context.setProperty("luke", "is a jedi");
        provider.addGroup(a, context);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager(
                new GroupTaskContextFactory(Collections.singleton(a), provider), executorService
        );
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);

        ExpectationTracker tracker = new ExpectationTracker();
        manager.registerTracker(tracker);
        tracker.addExpectation(u1, new PropertyExpectation("luke", "is a jedi"));
        tracker.addExpectation(u2, new PropertyExpectation("luke", "is a jedi"));
        tracker.addExpectation(u3, new PropertyNotSetExpectation("luke"));
        tracker.addExpectation(u4, new PropertyExpectation("luke", "is a jedi"));
        tracker.addExpectation(u5, new PropertyExpectation("luke", "is a jedi"));

        execution.execute(master).get();

        assertTrue(tracker.verify());

    }
}
