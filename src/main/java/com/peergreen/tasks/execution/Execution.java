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

package com.peergreen.tasks.execution;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 09/11/12
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public interface Execution extends LiveTask {
    void execute();
}
