package com.peergreen.tasks.model;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/10/12
 * Time: 20:54
 * To change this template use File | Settings | File Templates.
 */
public class PipelineTestCase {

    @Mock
    private Task task1;

    @Mock
    private Task task2;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFirstTaskHasNoDependency() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        verifyZeroInteractions(task1);
    }

    @Test
    public void testSecondTaskHasFirstTaskAsDependency() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        verify(task2).getRequirements();
    }

    @Test
    public void testIsTerminated() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        when(task1.getState()).thenReturn(State.COMPLETED);
        assertTrue(pipeline.isTerminated());
    }

    @Test
    public void testIsTerminatedWithTwoTasks() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);
        when(task1.getState()).thenReturn(State.COMPLETED);
        when(task2.getState()).thenReturn(State.COMPLETED);
        assertTrue(pipeline.isTerminated());
    }

    @Test
    public void testIsTerminatedWithTwoTasksOneBeingRunning() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);
        when(task1.getState()).thenReturn(State.COMPLETED);
        when(task2.getState()).thenReturn(State.RUNNING);
        assertFalse(pipeline.isTerminated());
    }
}
