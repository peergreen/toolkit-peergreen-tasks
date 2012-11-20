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

package com.peergreen.tasks.model.job;

import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Job;

/**
* Created with IntelliJ IDEA.
* User: guillaume
* Date: 28/10/12
* Time: 08:24
* To change this template use File | Settings | File Templates.
*/
public class SleepJob implements Job {
    private long time;

    public SleepJob(long time) {
        this.time = time;
    }

    @Override
    public void execute(TaskContext context) throws Exception {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // Ignored
        }
    }
}
