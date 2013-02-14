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

package com.peergreen.tasks.tree;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tree.Node;
import com.peergreen.tree.NodeVisitor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptySet;

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
public class TaskRenderingVisitor extends TaskTracker<Object> implements NodeVisitor<Task> {

    @Override
    public Object newSource(LiveTask source) {
        return this;
    }

    @Override
    public void sourceChanged(LiveTask source, State previous, Object bag) {
        states.put(source.getModel(), source.getState());
    }

    private enum Element {
        TEE("|-- "), BAR("|   "), LAST("`-- "), BLANK("    ");

        private final String value;

        Element(String value) {
            this.value = value;
        }
    }

    private PrintStream stream ;
    private Iterable<Group> groups;
    private Map<Task, State> states;

    public TaskRenderingVisitor() {
        this(System.out);
    }

    public TaskRenderingVisitor(PrintStream stream) {
        this.stream = stream;
        this.states = new HashMap<Task, State>();
        this.groups = emptySet();
    }

    public void setGroups(Iterable<Group> groups) {
        this.groups = groups;
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
        State state = states.get(task);
        stream.printf("%s [%s, %S]",
                task.getClass().getSimpleName(),
                task.getName(),
                (state!= null) ? state : "unknown");

        for (Group group : groups) {
            if (group.contains(task)) {
                stream.printf(" @%s", group.getName());
            }
        }

        stream.println();
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