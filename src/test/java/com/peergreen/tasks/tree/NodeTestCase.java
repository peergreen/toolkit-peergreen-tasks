/*
 * Copyright 2012-2013 Peergreen S.A.S.
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

package com.peergreen.tasks.tree;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tree.Node;
import com.peergreen.tree.NodeVisitor;
import com.peergreen.tree.node.LazyNode;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
public class NodeTestCase {
    private final Pipeline pipeline = new Pipeline("master");
    private Parallel parallel;
    private UnitOfWork unitOfWorkA;
    private UnitOfWork unitOfWorkB;
    private UnitOfWork unitOfWorkC;

    @BeforeMethod
    public void setUp() throws Exception {

        // Pipeline [master]
        //   Parallel [parallel]
        //     UnitOfWork [b]
        //     UnitOfWork [a]
        //   UnitOfWork [c]

        parallel = new Parallel("parallel");
        unitOfWorkA = new UnitOfWork(new EmptyJob(), "a");
        parallel.add(unitOfWorkA);
        unitOfWorkB = new UnitOfWork(new EmptyJob(), "b");
        parallel.add(unitOfWorkB);
        pipeline.add(parallel);
        unitOfWorkC = new UnitOfWork(new EmptyJob(), "c");
        pipeline.add(unitOfWorkC);
    }

    @Test
    public void testNodeCreation() throws Exception {

        Node<Task> root = new LazyNode<Task>(new TaskNodeAdapter(), pipeline);

        // Children of Pipeline should be ordered
        Iterator<Node<Task>> i = root.getChildren().iterator();
        assertEquals(i.next().getData(), parallel);
        assertEquals(i.next().getData(), unitOfWorkC);
        assertFalse(i.hasNext());

        // Children of Parallel have no order
        List<Node<Task>> children = root.getChildren().get(0).getChildren();
        assertEquals(children.size(), 2);
        assertTrue(children.contains(new LazyNode<Task>(new TaskNodeAdapter(), unitOfWorkA)));
        assertTrue(children.contains(new LazyNode<Task>(new TaskNodeAdapter(), unitOfWorkB)));
    }

    @Test
    public void testNodeCreationInReverse() throws Exception {

        Pipeline p = new Pipeline();
        UnitOfWork uow = new UnitOfWork(new EmptyJob());
        p.add(uow);

        LazyNode<Task> child = new LazyNode<Task>(new TaskNodeAdapter(), uow);
        LazyNode<Task> parent = new LazyNode<Task>(new TaskNodeAdapter(), p);
        child.setParent(parent);

        Iterator<Node<Task>> i = parent.getChildren().iterator();
        assertEquals(i.next().getData(), uow);
        assertFalse(i.hasNext());
    }

    @Test
    public void testNodeVisitorVisitsAllNodes() throws Exception {
        Node<Task> root = new LazyNode<Task>(new TaskNodeAdapter(), pipeline);

        final StringBuilder sb = new StringBuilder();

        root.walk(new NodeVisitor<Task>() {
            @Override
            public void visit(Node<Task> node) {
                Task task = node.getData();
                sb.append(task.getClass().getSimpleName());
                sb.append(" ");
                sb.append(task.getName());
                sb.append(" ");
            }
        });

        assertTrue(sb.toString().contains("Pipeline master"));
        assertTrue(sb.toString().contains("Parallel parallel"));
        assertTrue(sb.toString().contains("UnitOfWork a"));
        assertTrue(sb.toString().contains("UnitOfWork b"));
        assertTrue(sb.toString().contains("UnitOfWork c"));

    }

    @Test
    public void testNodeSetParentAlsoAddChildren() throws Exception {

        Pipeline pipeline1 = new Pipeline("master");
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob(), "uow");
        pipeline1.add(unitOfWork);

        LazyNode<Task> child = new LazyNode<Task>(new TaskNodeAdapter(), unitOfWork);
        Node<Task> parent = new LazyNode<Task>(new TaskNodeAdapter(), pipeline1);

        child.setParent(parent);

        Iterator<Node<Task>> i = parent.getChildren().iterator();
        assertEquals(i.next(), child);
        assertFalse(i.hasNext());
    }


    private Node<Task> findChildNode(Node<Task> node, String name) {
        for (Node<Task> taskNode : node.getChildren()) {
            if (name.equals(taskNode.getData().getName())) {
                return taskNode;
            }
        }

        return null;
    }
}
