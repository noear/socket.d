package org.noear.android_test
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.noear.socketd.SocketD
import org.noear.socketd.transport.core.Entity
import org.noear.socketd.transport.core.entity.StringEntity

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class HelloTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        //val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        //打开客户端会话（以 url 形式打开）
        val session = SocketD.createClient("sd:tcp://192.168.3.2:8602/?token=1b0VsGusEkddgr3d")
            .config{ c ->
                c.ioThreads(1).codecThreads(1)
                System.out.println(c.linkUrl)
            }.open()


        val message: Entity = StringEntity("Hello wrold!").metaPut("user", "noear")

        //发送
        session.send("/demo", message)

        //发送并请求（且，等待一个答复）
        val reply: Entity = session.sendAndRequest("/demo", message).await()
        System.out.println(reply)

        //发送并订阅（且，接收零个或多个答复流）
        session.sendAndSubscribe("/demo", message).thenReply { reply ->
            //打印
            System.out.println(reply)
        }

        Thread.sleep(100)
    }
}