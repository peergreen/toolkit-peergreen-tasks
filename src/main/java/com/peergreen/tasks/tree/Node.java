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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;
    private NodeAdapter<T> adapter;

    public Node(NodeAdapter<T> adapter, T data) {
        this.adapter = adapter;
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
        }
    }

    public T getData() {
        return data;
    }

    public List<Node<T>> getChildren() {
        if (children == null) {
            // Initialize
            Iterable<T> items = adapter.getChildren(this.getData());
            if ((items != null) && items.iterator().hasNext()) {
                // Node has children
                children = new ArrayList<Node<T>>();
                for (T item : items) {
                    Node<T> child = new Node<T>(adapter, item);
                    child.setParent(this);
                    //children.add(child);
                }
            } else {
                // Node has no children
                children = Collections.emptyList();
            }
        }
        return children;
    }

    public void walk(NodeVisitor<T> visitor) {
        visitor.visit(this);
        for (Node<T> child : getChildren()) {
            child.walk(visitor);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (!data.equals(node.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
