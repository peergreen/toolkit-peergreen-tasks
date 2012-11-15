package com.peergreen.tasks.model.tree.task;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.tree.Node;
import com.peergreen.tasks.model.tree.NodeVisitor;

import java.io.PrintStream;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
public class TaskRenderingVisitor implements NodeVisitor<Task> {

    public static final String SPACES = "  ";
    private PrintStream stream;

    public TaskRenderingVisitor() {
        this(System.out);
    }

    public TaskRenderingVisitor(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void visit(Node<Task> node) {
        int depth = getDepth(node);

        // Print increment
        for (int i = 0; i < depth; i++) {
            stream.print(SPACES);
        }

        Task task = node.getData();
        stream.printf("%s [%s, %S]%n",
                task.getClass().getSimpleName(),
                task.getName(),
                task.getState());
    }

    private int getDepth(Node<?> node) {
        int depth = 0;
        Node<?> current = node;
        while(current.getParent() != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }
}
