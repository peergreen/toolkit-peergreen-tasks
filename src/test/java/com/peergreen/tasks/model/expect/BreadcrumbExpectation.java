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
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class BreadcrumbExpectation implements Expectation {

    private String expected;
    private String latest;

    public BreadcrumbExpectation(String expected) {
        this.expected = expected;
    }

    @Override
    public void record(LiveTask live) {
        latest = live.getContext().getBreadcrumb().toString();
    }

    @Override
    public boolean verify() {
        return latest.contains(expected);
    }
}
