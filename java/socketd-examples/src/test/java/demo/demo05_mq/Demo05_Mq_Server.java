package demo.demo05_mq;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Demo05_Mq_Server {
    public static void main(String[] args) throws Exception {
        Set<Session> userList = new HashSet<>();

        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new EventListener()
                        .onOpen(s -> {
                            userList.add(s);
                        })
                        .onClose(s -> {
                            userList.remove(s);
                        })
                        .on("mq.sub", (s, m) -> {
                            //::订阅指令
                            String topic = m.meta("topic");
                            if (Utils.isNotEmpty(topic)) {
                                //标记订阅关系
                                s.attr(topic, "1");
                            }
                        }).on("mq.push", (s, m) -> {
                            //::推送指令
                            String topic = m.meta("topic");
                            String id = m.meta("id");

                            if (Utils.isNotEmpty(topic) && Utils.isNotEmpty(id)) {
                                //开始给订阅用户广播
                                for (Session s1 : userList.stream().filter(s1 -> s.attrMap().containsKey(topic)).collect(Collectors.toList())) {
                                    //Qos0 发送广播
                                    s1.send("mq.broadcast", m);
                                }
                            }
                        })
                ).start();
    }
}
