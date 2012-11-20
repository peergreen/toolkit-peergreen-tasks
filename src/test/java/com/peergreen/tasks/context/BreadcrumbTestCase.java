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

package com.peergreen.tasks.context;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.job.EmptyJob;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/11/12
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class BreadcrumbTestCase {

    @Test
    public void testBreadcrumbIteration() throws Exception {
        Breadcrumb parent = new Breadcrumb(new Pipeline("master"));
        Breadcrumb breadcrumb = new Breadcrumb(parent, new UnitOfWork(new EmptyJob(), "uow"));

        Iterator<Task> i = breadcrumb.iterator();
        assertEquals(i.next().getName(), "master");
        assertEquals(i.next().getName(), "uow");
        assertFalse(i.hasNext());

        Iterator<Task> i2 = breadcrumb.reverseIterator();
        assertEquals(i2.next().getName(), "uow");
        assertEquals(i2.next().getName(), "master");
        assertFalse(i2.hasNext());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testBreadcrumbIsImmutable() throws Exception {
        Breadcrumb parent = new Breadcrumb(new Pipeline("master"));
        Breadcrumb breadcrumb = new Breadcrumb(parent, new UnitOfWork(new EmptyJob(), "uow"));

        breadcrumb.iterator().remove();
    }

    @Test
    public void testBreadcrumbParent() throws Exception {
        Breadcrumb parent = new Breadcrumb(new Pipeline("master"));
        Breadcrumb breadcrumb = new Breadcrumb(parent, new UnitOfWork(new EmptyJob(), "uow"));

        assertEquals(breadcrumb.getParent().getName(), "master");
    }

    @Test
    public void testBreadcrumbCurrent() throws Exception {
        Breadcrumb parent = new Breadcrumb(new Pipeline("master"));
        Breadcrumb breadcrumb = new Breadcrumb(parent, new UnitOfWork(new EmptyJob(), "uow"));

        assertEquals(breadcrumb.getCurrent().getName(), "uow");
    }

    @Test
    public void testBreadcrumbRoot() throws Exception {
        Breadcrumb parent = new Breadcrumb(new Pipeline("master"));
        Breadcrumb breadcrumb = new Breadcrumb(parent, new UnitOfWork(new EmptyJob(), "uow"));

        assertEquals(breadcrumb.getRoot().getName(), "master");
    }
}
