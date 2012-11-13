package com.peergreen.tasks.model.tracker;

import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 27/10/12
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class TaskTrackerTestCase {

    @Mock
    private Task source;
    @Mock
    private Tracker<String> tracker;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTrackerIsNotifiedWhenANewTaskAppears() throws Exception {
        when(source.getState()).thenReturn(State.RUNNING);

        TaskTracker<String> taskTracker = new TaskTracker<String>(tracker);
        taskTracker.stateChanged(source, State.WAITING, State.RUNNING);

        verify(tracker).newSource(source);
    }

    @Test
    public void testUninterestedTrackerIsNotNotified() throws Exception {
        when(source.getState()).thenReturn(State.RUNNING);

        TaskTracker<String> taskTracker = new TaskTracker<String>(tracker);
        taskTracker.stateChanged(source, State.WAITING, State.RUNNING);

        verify(tracker).newSource(source);
        verify(tracker, times(0)).sourceChanged(source, State.WAITING, null);
    }

    @Test
    public void testInterestedTrackerIsNotified() throws Exception {
        when(source.getState()).thenReturn(State.RUNNING);
        when(tracker.newSource(source)).thenReturn("ok");

        TaskTracker<String> taskTracker = new TaskTracker<String>(tracker);
        taskTracker.stateChanged(source, State.WAITING, State.RUNNING);

        verify(tracker).sourceChanged(source, State.WAITING, "ok");
    }
}
