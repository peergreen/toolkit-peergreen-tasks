package com.peergreen.tasks.model;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 09:45
 * To change this template use File | Settings | File Templates.
 */
public class ArousableTask extends AbstractTask {

    private Task delegate;
    private boolean wakeUp = false;

    public ArousableTask(Task delegate) {
        this(null, delegate);
    }

    public ArousableTask(String name, Task delegate) {
        super(name);
        this.delegate = delegate;
    }

    public Task getDelegate() {
        return delegate;
    }

    public void wakeUp() {
        // Do not wake-up the Task more than 1 time
        if (!wakeUp) {
            wakeUp = true;
            propertyChangeSupport().firePropertyChange("wakeUp", false, true);
        }
    }
}
