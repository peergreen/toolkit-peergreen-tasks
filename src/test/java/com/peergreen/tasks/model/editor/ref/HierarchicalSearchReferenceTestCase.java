package com.peergreen.tasks.model.editor.ref;

import com.peergreen.tasks.model.WakeUp;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.context.Breadcrumb;
import com.peergreen.tasks.model.job.EmptyJob;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertSame;

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
