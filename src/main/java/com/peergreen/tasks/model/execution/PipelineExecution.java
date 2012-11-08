package com.peergreen.tasks.model.execution;

import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.state.State;
import com.peergreen.tasks.model.state.StateListener;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 22/10/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class PipelineExecution extends AbstractExecution {

    private Pipeline pipeline;
    private Iterator<Task> cursor;

    public PipelineExecution(ExecutorService executorService, Pipeline pipeline) {
        super(executorService);
        this.pipeline = pipeline;
        this.cursor = pipeline.getTasks().iterator();
    }

    protected PipelineExecution(AbstractExecution parent, Pipeline pipeline) {
        super(parent);
        this.pipeline = pipeline;
        this.cursor = pipeline.getTasks().iterator();

        pipeline.addStateListener(parent);
    }

    public void execute() {
        pipeline.addStateListener(getTrackerManager());
        pipeline.setState(State.RUNNING);

        // Start execution flow
        executeNext();
    }

    private void executeNext() {
        if (cursor.hasNext()) {
            // Schedule the next one on the list
            executeTask(cursor.next());
        } else {
            // Change Pipeline's state
            pipeline.setState(State.COMPLETED);
        }

    }


    @Override
    public void stateChanged(Task source, State previous, State current) {
        switch (current) {
            case FAILED:
                pipeline.setState(State.FAILED);
                break;
            case COMPLETED:
                // The inner Task has been completed
                executeNext();
        }

    }

}
