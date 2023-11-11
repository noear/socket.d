package demo.demo05_mq;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class Demo05_Mq_Server {
    public static void main(String[] args) throws Exception {
        Map<String, Session> userList = new HashMap<>();

        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new BuilderListener()
                        .on("mq.sub", (s, m) -> {
                            //::订阅指令
                            String topic = m.getMeta("topic");

                            if (Utils.isNotEmpty(topic)) {
                                System.out.println("有新的订阅：s=" + s.getSessionId() + ",t=" + topic);

                                //标记订阅关系
                                s.setAttr(topic, "1");
                                userList.put(s.getSessionId(), s);

                                if (m.isRequest() || m.isSubscribe()) {
                                    //如果有 qos1 要求，签复一下
                                    s.replyEnd(m, new StringEntity(""));
                                }
                            }
                        }).on("mq.push", (s, m) -> {
                            //::推送指令
                            String topic = m.getMeta("topic");
                            String id = m.getMeta("id");

                            if (Utils.isNotEmpty(topic) && Utils.isNotEmpty(id)) {
                                System.out.println("有新的消息推送：from=" + s.getSessionId() + ",t=" + topic);

                                Entity tmp = new StringEntity(m.getDataAsString())
                                        .meta("topic", topic)
                                        .meta("id", id);

                                //开始给订阅用户广播
                                userList.values().parallelStream().filter(s1 -> "1".equals(s.getAttr(topic)))
                                        .forEach(s1 -> {
                                            RunUtils.runAnTry(() -> {
                                                //发送广播（如果要 Ack，可改用 sendAndSubscribe ）//服务端支持 ACK 有点复杂，就不搞了
                                                s1.send("mq.broadcast", tmp);
                                            });
                                        });
                            }
                        })
                ).start();
    }
}
