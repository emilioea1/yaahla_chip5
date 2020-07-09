/*
 *  Copyright 2019 CHIP (Creative Hardware for Integrated Products). All rights reserved.
 *
 *  Created by Ali Fakih on 4/16/19 5:17 PM
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org.licenses/>.
 *
 *  Last modified 4/3/19 6:34 PM.
 *  Contact with author (Ali Fakih)
 *  on Github:    https://github.com/fakiho
 *  on Linkedin:  https://www.linkedin/in/fakiho/
 *
 */

package com.chip.parkpro1;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private final String TAG = ExampleInstrumentedTest.class.getSimpleName();
    private static Handler handlerThread;
    private static Thread thread;
    private final int START_UPDATING = 9900;
    private final int FINISH_UPDATING = 9901;
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.chip.parkpro1", appContext.getPackageName());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"thread");
            }
        });
    }

    private void hanlderInit() {
        handlerThread = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == START_UPDATING) {
                    Log.d(TAG,"START UPDATING");
                    thread.start();
                } else if (msg.what == FINISH_UPDATING) {
                    Log.d(TAG,"FINISH UPDATING");
                }
            }
        };

    }
}
