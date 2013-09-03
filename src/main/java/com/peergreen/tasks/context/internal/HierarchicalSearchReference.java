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

package com.peergreen.tasks.context.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.context.Reference;
import com.peergreen.tasks.model.Scope;
import com.peergreen.tasks.model.Task;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 12/11/12
* Time: 15:12
* To change this template use File | Settings | File Templates.
*/
public class HierarchicalSearchReference<T extends Task> implements Reference<T> {

    private Class<T> type;
    private List<String> name;

    public HierarchicalSearchReference(Class<T> type, List<String> name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public T resolve(Breadcrumb breadcrumb) {

        Node<Task> here = initTree(breadcrumb);

        for (String part : name) {
            if ("..".equals(part)) {
                if (here.getParent() == null) {
                    throw new IllegalStateException();
                }
                here = here.getParent();
            } else if (".".equals(part)) {
                continue;
            } else {
                // Search an immediate child with the expected name
                if (!here.isCompleted()) {
                    completeNode(here);
                }

                Node<Task> matching = findNamedNode(here, part);
                if (matching != null) {
                    here = matching;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        if (type.isInstance(here.getData())) {
            return type.cast(here.getData());
        }

        throw new IllegalStateException("Cannot resolve");
    }

    private Node<Task> findNamedNode(Node<Task> node, String part) {
        for (Node<Task> child : node.getChildren()) {
            if (child.getData().getName().equals(part)) {
                return child;
            }
        }

        return null;
    }

    private void completeNode(Node<Task> node) {
        Task task = node.getData();
        if (task instanceof Scope) {
            Scope scope = (Scope) task;
            for (Task sub : scope) {
                Node<Task> child = new Node<Task>(sub);
                child.setParent(node);
            }
        }
        node.setCompleted(true);

    }

    private Node<Task> initTree(Breadcrumb breadcrumb) {
        Node<Task> parent = null;
        Node<Task> node = null;
        for (Task task : breadcrumb) {
            node = new Node<Task>(task);
            node.setParent(parent);
            parent = node;
        }

        return node;
    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private boolean completed = false;
        private Set<Node<T>> children = new HashSet<Node<T>>();

        public Node(T data) {
            this.data = data;
        }

        public Node<T> getParent() {
            return parent;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
            if (parent != null) {
                parent.getChildren().add(this);
            }
        }

        public T getData() {
            return data;
        }

        public Set<Node<T>> getChildren() {
            return children;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

}
