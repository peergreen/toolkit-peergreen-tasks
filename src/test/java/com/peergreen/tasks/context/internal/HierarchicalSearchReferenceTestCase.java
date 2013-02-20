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

package com.peergreen.tasks.context.internal;

import static org.testng.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.tasks.context.Breadcrumb;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.WakeUp;
import com.peergreen.tasks.model.job.EmptyJob;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class HierarchicalSearchReferenceTestCase {

    private Pipeline master = new Pipeline("master");
    private Pipeline pipeline = new Pipeline("pipeline");
    private Parallel parallel = new Parallel("parallel");
    private UnitOfWork uow = new UnitOfWork(new EmptyJob(), "uow");
    private UnitOfWork uow2 = new UnitOfWork(new EmptyJob(), "uow2");
    private WakeUp arouse = new WakeUp("arouse", uow2);
    private Breadcrumb breadcrumb;

    @BeforeMethod
    public void setUp() throws Exception {
        master.add(pipeline, parallel);
        pipeline.add(uow);
        parallel.add(arouse);

        Breadcrumb one = new Breadcrumb(master);
        Breadcrumb two = new Breadcrumb(one, pipeline);
        breadcrumb = new Breadcrumb(two, uow);

        // master
        //   +- pipeline
        //   |     +- uow (*)
        //   +- parallel
        //         +- arouse
        //              +- uow2
    }

    @Test
    public void testParentResolution() throws Exception {
        HierarchicalSearchReference<Task> search = new HierarchicalSearchReference<Task>(Task.class, Collections.singletonList(".."));
        Task found = search.resolve(breadcrumb);
        assertSame(found, pipeline);

    }

    @Test
    public void testDoubleParentResolution() throws Exception {
        HierarchicalSearchReference<Task> search = new HierarchicalSearchReference<Task>(Task.class, Arrays.asList("..", ".."));
        Task found = search.resolve(breadcrumb);
        assertSame(found, master);

    }

    @Test
    public void testMixedResolution() throws Exception {
        HierarchicalSearchReference<Task> search = new HierarchicalSearchReference<Task>(Task.class, Arrays.asList("..", "..", "parallel"));
        Task found = search.resolve(breadcrumb);
        assertSame(found, parallel);

    }

    @Test
    public void testMixedResolution2() throws Exception {
        HierarchicalSearchReference<Task> search = new HierarchicalSearchReference<Task>(Task.class, Arrays.asList("..", "uow", "..", "..", "parallel", "arouse", ".."));
        Task found = search.resolve(breadcrumb);
        assertSame(found, parallel);

    }
}
