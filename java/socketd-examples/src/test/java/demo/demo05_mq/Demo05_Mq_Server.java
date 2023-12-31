package demo.demo05_mq;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.utils.StrUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Demo05_Mq_Server {
    public static void main(String[] args) throws Exception {
        Set<Session> userList = new HashSet<>();

        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new EventListener()
                        .doOnOpen(s -> {
                            userList.add(s);
                        })
                        .doOnClose(s -> {
                            userList.remove(s);
                        })
                        .doOn("mq.sub", (s, m) -> {
                            //::订阅指令
                            String topic = m.meta("topic");
                            if (StrUtils.isNotEmpty(topic)) {
                                //标记订阅关系
                                s.attrPut(topic, "1");
                            }
                        }).doOn("mq.push", (s, m) -> {
                            //::推送指令
                            String topic = m.meta("topic");
                            String id = m.meta("id");

                            if (StrUtils.isNotEmpty(topic) && StrUtils.isNotEmpty(id)) {
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
