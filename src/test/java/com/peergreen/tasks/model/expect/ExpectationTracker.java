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

package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 19/12/12
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class ExpectationTracker extends TaskTracker<Expectation> {

    private Map<Task, Expectation> expectations = new HashMap<Task, Expectation>();
    private Set<Expectation> used = new HashSet<Expectation>();

    public void addExpectation(Task task, Expectation expectation) {
        expectations.put(task, expectation);
    }

    public void addExpectation(TaskExpectation expectation) {
        expectations.put(expectation.getTask(), expectation);
    }

    @Override
    public Expectation newSource(LiveTask source) {
        Expectation e = expectations.get(source.getModel());
        if (e != null) {
            used.add(e);
        }
        return e;
    }

    @Override
    public void sourceChanged(LiveTask source, State previous, Expectation bag) {
        bag.record(source);
    }

    public boolean verify() {

        // Quick check: ensure all expectations have been used
        if (used.size() != expectations.size()) {
            System.err.printf("Some expectations were not used.");
            return false;
        }

        boolean result = true;

        // If any is false, global result will be false
        for (Expectation expectation : expectations.values()) {
            boolean verify = expectation.verify();
            if (!verify) {
                System.err.printf("Expectation not satisfied " + expectation);
            }
            result &= verify;
        }

        return result;
    }
}
