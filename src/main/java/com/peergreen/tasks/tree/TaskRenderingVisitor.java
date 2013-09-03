/**
 * Copyright 2012-2013 Peergreen S.A.S. All rights reserved.
 * Proprietary and confidential.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.tree;

import static java.util.Collections.emptySet;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tree.Node;
import com.peergreen.tree.NodeVisitor;
import com.peergreen.tree.visitor.print.TreePrettyPrintNodeVisitor;

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

    private final NodeVisitor<Task> pretty;
    private Iterable<Group> groups;
    private final Map<Task, State> states;

    public TaskRenderingVisitor() {
        this(System.out);
    }

    public TaskRenderingVisitor(PrintStream stream) {
        this.pretty = new TaskPrettyPrintVisitor(stream);
        this.states = new ConcurrentHashMap<Task, State>();
        this.groups = emptySet();
    }

    public void setGroups(Iterable<Group> groups) {
        this.groups = groups;
    }

    @Override
    public void visit(Node<Task> node) {
        pretty.visit(node);
    }

    /**
     * I need to have a delegating class here since the visitor already extends TaskTracker.
     */
    private class TaskPrettyPrintVisitor extends TreePrettyPrintNodeVisitor<Task> {

        public TaskPrettyPrintVisitor(PrintStream stream) {
            super(stream);
        }

        @Override
        protected void doPrintInfo(PrintStream stream, Node<Task> node) {
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
    }

}
