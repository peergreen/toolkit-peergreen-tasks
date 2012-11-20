package com.peergreen.tasks.tree.task;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.tree.Node;
import com.peergreen.tasks.tree.NodeVisitor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 * Sample output:
 * <pre>
 * Pipeline #master
 * |-- Pipeline #p1
 * |   |-- UnitOfWork #1
 * |   `-- UnitOfWork #2
 * |-- UnitOfWork #3
 * `-- Pipeline #p2
 *     |-- UnitOfWork #4
 *     `-- UnitOfWork #5
 * </pre>
 */
public class TaskRenderingVisitor implements NodeVisitor<Task> {

    private enum Element {
        TEE("|-- "), BAR("|   "), LAST("`-- "), BLANK("    ");

        private final String value;

        Element(String value) {
            this.value = value;
        }
    }

    private PrintStream stream ;

    public TaskRenderingVisitor() {
        this(System.out);
    }

    public TaskRenderingVisitor(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void visit(Node<Task> node) {

        List<Element> prefix = new ArrayList<Element>();
        if (node.getParent() != null) {
            // we're visiting an item with parent

            // handle last element
            if (isLastChild(node)) {
                prefix.add(0, Element.LAST);
            } else {
                prefix.add(0, Element.TEE);
            }

            // handle other elements
            Node<Task> current = node.getParent();
            while (current.getParent() != null) {

                if (isLastChild(current)) {
                    prefix.add(0, Element.BLANK);
                } else {
                    prefix.add(0, Element.BAR);
                }

                // Move cursor
                current = current.getParent();
            }

        } // else no parent (thus no/empty prefix)

        for (Element element : prefix) {
            stream.print(element.value);
        }

        // Print Task info
        Task task = node.getData();
        stream.printf("%s [%s, %S]%n",
                task.getClass().getSimpleName(),
                task.getName(),
                task.getState());
    }

    private boolean isLastChild(Node<?> node) {
        Node<?> parent = node.getParent();
        Iterator<? extends Node<?>> i = parent.getChildren().iterator();

        // Consume the Iterator until we find the current Node
        Node<?> child = i.next();
        while (!node.equals(child)) {
            child = i.next();
        }

        // It is the last child if this is the last item of the Iterator
        return !i.hasNext();
    }

}
