package com.peergreen.tasks.model;

import com.peergreen.tasks.model.state.State;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
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

    @Mock
    private Task task3;

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

    @Test
    public void testAddAfter() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        pipeline.addTaskAfter(task1, task3);

        Iterator<Task> i = pipeline.getTasks().iterator();
        Task one = i.next();
        Task two = i.next();
        Task three = i.next();
        assertEquals(one, task1);
        assertEquals(two, task3);
        assertEquals(three, task2);
    }

    @Test
    public void testAddAfterAtEnd() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        pipeline.addTaskAfter(task2, task3);

        Iterator<Task> i = pipeline.getTasks().iterator();
        Task one = i.next();
        Task two = i.next();
        Task three = i.next();
        assertEquals(one, task1);
        assertEquals(two, task2);
        assertEquals(three, task3);
    }

    @Test
    public void testAddBefore() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        pipeline.addTaskBefore(task2, task3);

        Iterator<Task> i = pipeline.getTasks().iterator();
        Task one = i.next();
        Task two = i.next();
        Task three = i.next();
        assertEquals(one, task1);
        assertEquals(two, task3);
        assertEquals(three, task2);
    }

    @Test
    public void testAddBeforeAtBeginning() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);
        pipeline.addTask(task2);

        pipeline.addTaskBefore(task1, task3);

        Iterator<Task> i = pipeline.getTasks().iterator();
        Task one = i.next();
        Task two = i.next();
        Task three = i.next();
        assertEquals(one, task3);
        assertEquals(two, task1);
        assertEquals(three, task2);
    }

    @Test
    public void testAddFirst() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.addTask(task1);

        pipeline.addFirst(task2);

        Iterator<Task> i = pipeline.getTasks().iterator();
        Task one = i.next();
        Task two = i.next();
        assertEquals(one, task2);
        assertEquals(two, task1);
    }

    @Test
    public void testAddFirstWhenEmpty() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.addFirst(task1);

        Iterator<Task> i = pipeline.getTasks().iterator();
        Task one = i.next();
        assertEquals(one, task1);
    }
}
