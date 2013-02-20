/*
 * Copyright 2012 Peergreen S.A.S.
 *
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
 * Date: 16/11/12
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class Delegate<T extends Task> extends AbstractTask implements Scope {

    private T delegate;

    public Delegate() {
        this(null);
    }

    public Delegate(String name) {
        super(name);
    }

    public Task getDelegate() {
        return delegate;
    }

    public void setDelegate(T delegate) {
        if (isModifiable()) {
            this.delegate = delegate;
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return Collections.<Task>singleton(delegate).iterator();
    }
}
