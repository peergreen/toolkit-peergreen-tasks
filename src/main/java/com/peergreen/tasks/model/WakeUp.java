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
