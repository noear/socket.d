package org.noear.android_test

import org.junit.Test
import org.noear.socketd.SocketD
import org.noear.socketd.transport.core.Entity
import org.noear.socketd.transport.core.entity.StringEntity

class HelloTest {
    @Test
    fun addition_isCorrect() {
        //打开客户端会话（以 url 形式打开）
        val session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3d")
            .config{ c -> c.ioThreads(1).codecThreads(1) }
            .open()


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