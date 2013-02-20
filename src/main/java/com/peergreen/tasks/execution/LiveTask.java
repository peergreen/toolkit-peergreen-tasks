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

package com.peergreen.tasks.execution;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/12/12
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public interface LiveTask {

    Task getModel();

    TaskContext getContext();

    State getState();

    void setState(State state);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
