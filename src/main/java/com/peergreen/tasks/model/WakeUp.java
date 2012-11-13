package com.peergreen.tasks.model;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 09:45
 * To change this template use File | Settings | File Templates.
 */
public class WakeUp extends AbstractTask implements Scope {

    private Task delegate;
    private boolean wakeUp = false;

    public WakeUp(Task delegate) {
        this(null, delegate);
    }

    public WakeUp(String name, Task delegate) {
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

    @Override
    public Iterator<Task> iterator() {
        return Collections.singleton(delegate).iterator();
    }
}
