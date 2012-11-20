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

package com.peergreen.tasks.tree.task;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.EmptyJob;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 15/11/12
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class TaskNodeAdapterTestCase {

    @Test
    public void testPipelineWithChild() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.add(mock(Task.class));

        Iterable<Task> iterable = new TaskNodeAdapter().getChildren(pipeline);
        assertNotNull(iterable);
        Iterator<Task> i = iterable.iterator();
        assertNotNull(i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testEmptyPipeline() throws Exception {
        Pipeline pipeline = new Pipeline();

        Iterable<Task> iterable = new TaskNodeAdapter().getChildren(pipeline);
        assertNotNull(iterable);
        Iterator<Task> i = iterable.iterator();
        assertFalse(i.hasNext());
    }

    @Test
    public void testUnitOfWork() throws Exception {
        UnitOfWork unitOfWork = new UnitOfWork(new EmptyJob());

        Iterable<Task> iterable = new TaskNodeAdapter().getChildren(unitOfWork);
        assertNull(iterable);
    }
}
