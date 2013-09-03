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

package com.peergreen.tasks.model.expect;

import com.peergreen.tasks.execution.LiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 12/11/12
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class ExtensionExpectation implements Expectation {

    private Class<?> type;
    private Object value;
    private Object found;

    public ExtensionExpectation(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void record(LiveTask live) {
        found = live.getContext().get(type);
    }

    @Override
    public boolean verify() {
        return value.equals(found);
    }
}
