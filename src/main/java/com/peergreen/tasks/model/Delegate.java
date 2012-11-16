package com.peergreen.tasks.model;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/11/12
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class Delegate extends AbstractTask {

    private Task delegate;

    public Delegate() {
        this(null);
    }

    public Delegate(String name) {
        super(name);
    }

    public Task getDelegate() {
        return delegate;
    }

    public void setDelegate(Task delegate) {
        if (isModifiable()) {
            this.delegate = delegate;
        }
    }
}
