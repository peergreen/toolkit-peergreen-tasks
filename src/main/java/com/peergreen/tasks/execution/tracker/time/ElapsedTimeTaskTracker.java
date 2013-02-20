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

package com.peergreen.tasks.execution.tracker.time;

import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 26/10/12
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public class ElapsedTimeTaskTracker extends TaskTracker<Times> {

    private TimesVisitor visitor;

    public ElapsedTimeTaskTracker() {
        this(new SystemTimesVisitor());
    }

    public ElapsedTimeTaskTracker(TimesVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public Times newSource(LiveTask source) {
        return new Times();
    }

    @Override
    public void sourceChanged(LiveTask source, State previous, Times times) {
        switch (source.getState()) {
            case RUNNING:
                times.setBeginTimestamp(System.currentTimeMillis());
                return;
            case COMPLETED:
            case FAILED:
                visitor.visitDuration(source, System.currentTimeMillis() - times.getBeginTimestamp());
        }
    }

    private static class SystemTimesVisitor implements TimesVisitor {
        @Override
        public void visitDuration(LiveTask task, long duration) {
            System.out.printf("Task %s executed in %d milliseconds%n",
                    task.getModel().getName(),
                    duration);
        }
    }
}
