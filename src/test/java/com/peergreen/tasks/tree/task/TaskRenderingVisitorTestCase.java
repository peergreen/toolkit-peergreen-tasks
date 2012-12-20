/*
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

package com.peergreen.tasks.tree.task;

import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.WakeUp;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.tree.Node;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Future;

import static java.util.Collections.singleton;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 20/11/12
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
public class TaskRenderingVisitorTestCase {

    private ByteArrayOutputStream baos;
    private TaskRenderingVisitor visitor;

    @BeforeMethod
    public void setUp() throws Exception {
        baos = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(baos);
        visitor = new TaskRenderingVisitor(stream);
    }

    @Test
    public void testSingleNodeRendering() throws Exception {
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), unitOfWork);

        node.walk(visitor);

        Assert.assertEquals(baos.toString(), "UnitOfWork [uow, UNKNOWN]\n");
    }

    @Test
    public void testPipelineNodeRendering() throws Exception {
        Pipeline master = new Pipeline("master");
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        master.add(unitOfWork);
        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), master);

        node.walk(visitor);

        String expected = "Pipeline [master, UNKNOWN]\n" +
                "`-- UnitOfWork [uow, UNKNOWN]\n";
        Assert.assertEquals(baos.toString(), expected);
    }

    @Test
    public void testPipelineNodeRenderingWithGroup() throws Exception {
        Pipeline master = new Pipeline("master");
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        master.add(unitOfWork);

        Group group = new Group("test");
        group.addTask(unitOfWork);

        visitor.setGroups(singleton(group));

        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), master);

        node.walk(visitor);

        String expected = "Pipeline [master, UNKNOWN]\n" +
                "`-- UnitOfWork [uow, UNKNOWN] @test\n";
        Assert.assertEquals(baos.toString(), expected);
    }

    @Test
    public void testPipelineNodeRenderingWithTwoChildren() throws Exception {
        Pipeline master = new Pipeline("master");
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        UnitOfWork unitOfWork2 = new UnitOfWork(new EmptyJob(), "uow2");
        master.add(unitOfWork, unitOfWork2);
        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), master);

        node.walk(visitor);

        String expected = "Pipeline [master, UNKNOWN]\n" +
                "|-- UnitOfWork [uow, UNKNOWN]\n" +
                "`-- UnitOfWork [uow2, UNKNOWN]\n";
        Assert.assertEquals(baos.toString(), expected);
    }

    @Test
    public void testNodeRenderingWithBlankElement() throws Exception {
        Pipeline master = new Pipeline("master");
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        master.add(unitOfWork);

        Pipeline sub = new Pipeline("sub");
        sub.add(new UnitOfWork(new EmptyJob(), "task"));
        master.add(sub);
        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), master);

        node.walk(visitor);

        String expected = "Pipeline [master, UNKNOWN]\n" +
                "|-- UnitOfWork [uow, UNKNOWN]\n" +
                "`-- Pipeline [sub, UNKNOWN]\n" +
                "    `-- UnitOfWork [task, UNKNOWN]\n";
        Assert.assertEquals(baos.toString(), expected);
    }

    @Test
    public void testNodeRenderingWithBarElement() throws Exception {
        Pipeline master = new Pipeline("master");
        Pipeline pipeline = new Pipeline("pipeline");
        master.add(pipeline);
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        pipeline.add(unitOfWork);

        Pipeline sub = new Pipeline("sub");
        sub.add(new UnitOfWork(new EmptyJob(), "task"));
        master.add(sub);
        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), master);

        node.walk(visitor);

        String expected = "Pipeline [master, UNKNOWN]\n" +
                "|-- Pipeline [pipeline, UNKNOWN]\n" +
                "|   `-- UnitOfWork [uow, UNKNOWN]\n" +
                "`-- Pipeline [sub, UNKNOWN]\n" +
                "    `-- UnitOfWork [task, UNKNOWN]\n";
        Assert.assertEquals(baos.toString(), expected);
    }

    @Test
    public void testNodeRenderingWithTaskTrackingEnabled() throws Exception {
        Pipeline master = new Pipeline("master");
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        WakeUp wakeUp = new WakeUp("wake", unitOfWork);

        master.add(wakeUp);
        Node<Task> node = new Node<Task>(new TaskNodeAdapter(), master);

        ExecutorServiceBuilderManager builderManager = new ExecutorServiceBuilderManager();
        TaskExecutorService execution = new TaskExecutorService(builderManager);

        TrackerManager manager = new TrackerManager();
        builderManager.setTrackerManager(manager);
        manager.registerTracker(visitor);

        Future<?> future = execution.execute(master);

        node.walk(visitor);

        String expected = "Pipeline [master, RUNNING]\n" +
                "`-- WakeUp [wake, SCHEDULED]\n" +
                "    `-- UnitOfWork [uow, UNKNOWN]\n";
        Assert.assertEquals(baos.toString(), expected);

        // unblock the execution flow and wait for completion
        wakeUp.wakeUp();
        future.get();

        baos.reset();
        node.walk(visitor);

        String expected2 = "Pipeline [master, COMPLETED]\n" +
                "`-- WakeUp [wake, COMPLETED]\n" +
                "    `-- UnitOfWork [uow, COMPLETED]\n";
        Assert.assertEquals(baos.toString(), expected2);


    }

}
