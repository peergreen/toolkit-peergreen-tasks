package com.peergreen.tasks.model.tree;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public interface NodeVisitor<T> {
    void visit(Node<T> node);
}
