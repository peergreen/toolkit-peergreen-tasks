/**
 * Copyright 2012-2013 Peergreen S.A.S. All rights reserved.
 * Proprietary and confidential.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.tasks.model;

/**
 * A container task is a task on which some task may be add. <br>
 * The way the tasks are added are delegated to the implementation.
 * @author Florent Benoit
 */
public interface Container extends Scope {

    /**
     * Add the given tasks to this container.
     * @param tasks the list of tasks
     */
    void add(Task... tasks);

}
