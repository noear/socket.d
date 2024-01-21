package demo.demo03;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.listener.EventListener;

import java.nio.ByteBuffer;

public class Demo03_FlowControl {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new EventListener().doOn("/demo", (s, m) -> {
                    if (m.isSubscribe() == false) {
                        s.sendAlarm(m, "此事件只支持订阅模式");
                        return;
                    }

                    long videoId = m.metaAsLong("videoId");
                    int rangeStart = m.rangeStart();
                    int rangeSize = m.rangeSize();

                    if (videoId == 0 || rangeSize == 0) {
                        s.sendAlarm(m, "参数不合规");
                        return;
                    }

                    ByteBuffer[] fragments = new ByteBuffer[rangeSize];
                    for (int i = 0; i < rangeSize; i++) {
                        s.reply(m, new EntityDefault().dataSet(fragments[i]));
                    }
                    s.replyEnd(m, new EntityDefault());
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        ClientSession clientSession = SocketD.createClient("sd:udp://127.0.0.1:8602/?u=a&p=2")
                .openOrThow();

        //发送并请求
        clientSession.sendAndSubscribe("/demo", new EntityDefault().range(5,5)
                        .metaPut("videoId", "42E056E1-B4B7-4EF4-AC4B-AEE77EDB0B86"))
                .thenReply(r -> {
                    if (r.dataSize() > 0) {
                        System.out.println(r);
                    }
                }).thenError(err -> {
                    err.printStackTrace();
                });
    }
}
