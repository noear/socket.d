package org.noear.android_test

import org.junit.Assert
import org.junit.Test
import org.noear.socketd.SocketD
import org.noear.socketd.transport.client.ClientSession
import org.noear.socketd.transport.core.Entity
import org.noear.socketd.transport.core.entity.StringEntity

class HelloTest {
    @Test
    fun addition_isCorrect() {
        //打开客户端会话（以 url 形式打开）
        val session: ClientSession =
            SocketD.createClient("sd:ws://127.0.0.1:8602/?token=1b0VsGusEkddgr3d")
                .open()


        val message: Entity = StringEntity("Hello wrold!").meta("user", "noear")

        //发送
        session.send("/demo", message)

        //发送并请求（且，等待一个答复）
        val reply: Entity = session.sendAndRequest("/demo", message)
        System.out.println(reply)

        //发送并订阅（且，接收零个或多个答复流）
        session.sendAndSubscribe("/demo", message) { reply ->
            //打印
            System.out.println(reply)
        }
    }
}