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

package com.peergreen.tasks.execution.builder;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.execution.Execution;
import com.peergreen.tasks.execution.ExecutionBuilder;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 13/11/12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public class TrackerManagerEnabler implements ExecutionBuilder {

    private TrackerManager trackerManager;

    public TrackerManagerEnabler() {
        this(new TrackerManager());
    }

    public TrackerManagerEnabler(TrackerManager trackerManager) {
        this.trackerManager = trackerManager;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    @Override
    public Execution newExecution(TaskContext taskContext) {
        Task task = taskContext.getBreadcrumb().getCurrent();
        task.addPropertyChangeListener("state", trackerManager);

        // Do not return anything to not break the builder chain
        return null;
    }
}
