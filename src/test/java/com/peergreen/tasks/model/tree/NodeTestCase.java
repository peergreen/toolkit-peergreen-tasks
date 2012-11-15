package com.peergreen.tasks.model.tree;

import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.EmptyJob;
import com.peergreen.tasks.model.tree.task.TaskNodeAdapter;
import com.peergreen.tasks.model.tree.task.TaskRenderingVisitor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
public class NodeTestCase {
    private Pipeline pipeline = new Pipeline("master");

    @BeforeMethod
    public void setUp() throws Exception {

        // Pipeline [master]
        //   Parallel [parallel]
        //     UnitOfWork [b]
        //     UnitOfWork [a]
        //   UnitOfWork [c]

        Parallel parallel = new Parallel("parallel");
        parallel.add(new UnitOfWork(new EmptyJob(), "a"));
        parallel.add(new UnitOfWork(new EmptyJob(), "b"));
        pipeline.add(parallel);
        pipeline.add(new UnitOfWork(new EmptyJob(), "c"));
    }

    @Test
    public void testNodeCreation() throws Exception {

        Node<Task> root = new Node<Task>(new TaskNodeAdapter(), pipeline);

        Node<Task> parallelNode = findChildNode(root, "parallel");
        assertNotNull(parallelNode);
        Node<Task> aNode = findChildNode(parallelNode, "a");
        assertNotNull(aNode);
        Node<Task> bNode = findChildNode(parallelNode, "b");
        assertNotNull(bNode);

        Node<Task> cNode = findChildNode(root, "c");
        assertNotNull(cNode);
    }

    @Test
    public void testNodeVisitorVisitsAllNodes() throws Exception {
        Node<Task> root = new Node<Task>(new TaskNodeAdapter(), pipeline);

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

        Node<Task> child = new Node<Task>(new TaskNodeAdapter(), unitOfWork);
        Node<Task> parent = new Node<Task>(new TaskNodeAdapter(), pipeline1);

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
