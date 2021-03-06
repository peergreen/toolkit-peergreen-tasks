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

package com.peergreen.tasks.context;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 17/11/12
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutionContext {

    void setProperty(String name, Object value);
    Object getProperty(String name);
    Object getProperty(String name, Object defaultValue);
    <T> T getProperty(String name, Class<T> type);
    <T> T getProperty(String name, Class<T> type, T defaultValue);
    void removeProperty(String name);

    <T> T get(Class<T> type);
    void remove(Object instance);
    void add(Object instance);

}
