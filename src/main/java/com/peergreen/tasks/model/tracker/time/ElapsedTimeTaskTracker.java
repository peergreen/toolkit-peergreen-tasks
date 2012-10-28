package com.peergreen.tasks.model.tracker.time;

import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.tracker.TaskTracker;
import com.peergreen.tasks.model.tracker.Tracker;

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
    public Times newSource(Task source) {
        return new Times();
    }

    @Override
    public void sourceChanged(Task source, State previous, Times times) {
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
        public void visitDuration(Task task, long duration) {
            System.out.printf("Task %s executed in %d milliseconds%n",
                    task.getName(),
                    duration);
        }
    }
}
