package com.peergreen.tasks.model.tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Node<T>> children;
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
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    public T getData() {
        return data;
    }

    public Set<Node<T>> getChildren() {
        if (children == null) {
            // Initialize
            Iterable<T> items = adapter.getChildren(this.getData());
            if ((items != null) && items.iterator().hasNext()) {
                // Node has children
                children = new HashSet<Node<T>>();
                for (T item : items) {
                    Node<T> child = new Node<T>(adapter, item);
                    child.setParent(this);
                    //children.add(child);
                }
            } else {
                // Node has no children
                children = Collections.emptySet();
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
