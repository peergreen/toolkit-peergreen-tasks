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

import java.util.ArrayList;
import java.util.List;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/12/12
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class SequenceTracker extends TaskTracker<Object> {
    private List<Triple> sequence = new ArrayList<Triple>();
    private int level = 0;

    public void addStep(Task task, State state) {
        sequence.add(new Triple(task, state));
    }

    @Override
    public Object newSource(LiveTask source) {
        for (Triple triple : sequence) {
            if (triple.task.equals(source.getModel())) {
                return this;
            }
        }
        
        return null;
    }

    @Override
    public void sourceChanged(LiveTask source, State previous, Object unused) {
        if (level < sequence.size()) {
            Triple triple = sequence.get(level);
            triple.ok = triple.task.equals(source.getModel()) && triple.state.equals(source.getState());
            if (triple.ok) {
                level++;
            }
        }
    }

    public boolean verify() {
        boolean result = true;
        for (Triple triple : sequence) {
            result &= triple.ok;
        }
        return result;
    }

    private class Triple {
        Task task;
        State state;
        boolean ok = false;

        private Triple(Task task, State state) {
            this.task = task;
            this.state = state;
        }
    }

}
