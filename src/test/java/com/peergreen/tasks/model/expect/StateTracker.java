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

package com.peergreen.tasks.model.expect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 20/12/12
 * Time: 09:53
 * To change this template use File | Settings | File Templates.
 */
public class StateTracker extends TaskTracker<Task> {

    private final Map<Task, State> states = new ConcurrentHashMap<Task, State>();

    @Override
    public Task newSource(LiveTask source) {
        return source.getModel();
    }

    @Override
    public void sourceChanged(LiveTask source, State previous, Task task) {
        states.put(task, source.getState());
    }

    public State getState(Task task) {
        return states.get(task);
    }
}
