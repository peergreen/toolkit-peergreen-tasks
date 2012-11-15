package com.peergreen.tasks.model.tree;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public interface NodeAdapter<T> {
    Iterable<T> getChildren(T object);
}
