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

package com.peergreen.tasks.model;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 13/11/12
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class PipelineTestCase {

    @Mock
    private Task task0;

    @Mock
    private Task task1;

    @Mock
    private Task task2;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddFirst() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.addFirst(task2);

        assertPipelineOrder(pipeline, task2, task0, task1);
    }

    @Test
    public void testAddFirstWhenEmpty() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.addFirst(task2);

        assertPipelineOrder(pipeline, task2);
    }

    @Test
    private void testPipelineIsUnmodifiableAfterReadOnly() {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.setReadOnly();

        pipeline.add(task2);
        assertPipelineOrder(pipeline, task0, task1);
        pipeline.addFirst(task2);
        assertPipelineOrder(pipeline, task0, task1);
        pipeline.addLast(task2);
        assertPipelineOrder(pipeline, task0, task1);
        pipeline.addTaskAfter(task0, task2);
        assertPipelineOrder(pipeline, task0, task1);
        pipeline.addTaskBefore(task1, task2);
        assertPipelineOrder(pipeline, task0, task1);
    }

    private void assertPipelineOrder(Pipeline pipeline, Task... tasks) {
        Iterator<Task> i = pipeline.getTasks().iterator();
        for (Task task : tasks) {
            assertEquals(i.next(), task);
        }
        assertFalse(i.hasNext());
    }

    @Test
    public void testAddLast() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.addLast(task2);

        assertPipelineOrder(pipeline, task0, task1, task2);
    }

    @Test
    public void testAddLastWhenEmpty() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.addLast(task2);

        assertPipelineOrder(pipeline, task2);
    }

    @Test
    public void testAddTaskAfterInMiddle() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.addTaskAfter(task0, task2);

        assertPipelineOrder(pipeline, task0, task2, task1);
    }

    @Test
    public void testAddTaskAfterAtEnd() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.addTaskAfter(task1, task2);

        assertPipelineOrder(pipeline, task0, task1, task2);
    }

    @Test
    public void testAddTaskAfterWithUnknownReferencedTask() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);

        pipeline.addTaskAfter(task1, task2);

        assertPipelineOrder(pipeline, task0);
    }

    @Test
    public void testAddTaskBeforeInMiddle() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.addTaskBefore(task1, task2);

        assertPipelineOrder(pipeline, task0, task2, task1);
    }

    @Test
    public void testAddTaskBeforeAtBeginning() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task0);
        pipeline.add(task1);

        pipeline.addTaskBefore(task0, task2);

        assertPipelineOrder(pipeline, task2, task0, task1);
    }

    @Test
    public void testAddTaskBeforeUnreferencedTask() throws Exception {
        Pipeline pipeline = new Pipeline();

        pipeline.add(task1);

        pipeline.addTaskBefore(task0, task2);

        assertPipelineOrder(pipeline, task1);
    }
}
