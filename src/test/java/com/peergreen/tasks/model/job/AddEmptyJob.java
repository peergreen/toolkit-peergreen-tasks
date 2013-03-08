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

package com.peergreen.tasks.model.job;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Container;
import com.peergreen.tasks.model.Job;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;

/**
* Add a job in another container.
*/
public class AddEmptyJob implements Job {

    private final Container container;

    private final UnitOfWork unitOfWork;


    public AddEmptyJob(Container container) {
        this.container = container;
        this.unitOfWork = new UnitOfWork(new EmptyJob());
    }

    public Task getCreatedTask() {
        return unitOfWork;
    }




    @Override
    public void execute(TaskContext context) throws Exception {
        // Add empty job
        container.add(unitOfWork);
    }
}
