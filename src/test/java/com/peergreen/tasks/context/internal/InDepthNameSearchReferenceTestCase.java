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

import static junit.framework.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class InDepthNameSearchReferenceTestCase {

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
        //   |     +- uow
        //   +- parallel
        //         +- arouse
        //              +- uow2
    }

    @Test
    public void testLookingForPipeline() throws Exception {
        InDepthNameSearchReference<Pipeline> search = new InDepthNameSearchReference<Pipeline>(Pipeline.class, "pipeline");
        Pipeline found = search.resolve(breadcrumb);
        assertNotNull(found);
        assertEquals(found, pipeline);
    }

    @Test
    public void testLookingForMasterPipeline() throws Exception {
        InDepthNameSearchReference<Pipeline> search = new InDepthNameSearchReference<Pipeline>(Pipeline.class, "master");
        Pipeline found = search.resolve(breadcrumb);
        assertNotNull(found);
        assertEquals(found, master);
    }

    @Test
    public void testLookingForTask() throws Exception {
        InDepthNameSearchReference<Task> search = new InDepthNameSearchReference<Task>(Task.class, "uow");
        Task found = search.resolve(breadcrumb);
        assertNotNull(found);
        assertEquals(found, uow);
    }
}
