package demo.demo06_im;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.PathListener;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;

import java.util.HashMap;
import java.util.Map;


public class Demo06_Im_Server {
    static Map<String, Session> userList = new HashMap<>();
    public static void main(String[] args) throws Exception {
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602))
                .listen(new PathListener()
                        //::::::::::用户频道处理
                        .doOf("/", new EventListener()
                                .doOnOpen(s -> {
                                    //用户连接
                                    String user = s.param("u");
                                    if (StrUtils.isNotEmpty(user)) {
                                        //有用户名，才登录成功
                                        userList.put(s.sessionId(), s);
                                        s.attrPut("user", user);
                                    } else {
                                        //否则说明是非法的
                                        s.close();
                                    }
                                }).doOnClose(s -> {
                                    userList.remove(s.sessionId());

                                    String room = s.attr("room");

                                    if (StrUtils.isNotEmpty(room)) {
                                        pushToRoom(room, new StringEntity("有人退出聊天室：" + s.attr("user")));
                                    }
                                }).doOn("cmd.join", (s, m) -> {
                                    //::加入房间指令
                                    String room = m.meta("room");

                                    if (StrUtils.isNotEmpty(room)) {
                                        s.attrPut("room", room);

                                        pushToRoom(room, new StringEntity("新人加入聊天室：" + s.attr("user")));
                                    }
                                }).doOn("cmd.chat", (s, m) -> {
                                    //::聊天指令
                                    String room = m.meta("room");

                                    if (StrUtils.isNotEmpty(room)) {
                                        StringBuilder buf = new StringBuilder();
                                        buf.append(m.meta("sender")).append(": ").append(m.dataAsString());

                                        pushToRoom(room, new StringEntity(buf.toString()));
                                    }
                                }))
                        //::::::::::管理频道处理
                        .doOf("/admin", new EventListener()
                                .doOnOpen((session) -> {
                                    //管理员签权
                                    String user = session.param("u");
                                    String token = session.param("t");

                                    if ("admin".equals(user) && "admin".equals(token)) {

                                    } else {
                                        session.close();
                                    }
                                }).doOn("cmd.t", (s, m) -> {
                                    String user = m.meta("u");
                                    String room = m.meta("room");

                                    Session s2 = userList.values().parallelStream().filter(s1 -> user.equals(s1.attr("user"))).findFirst().get();
                                    if (s2 != null) {
                                        s2.attrPut("room", null);
                                        s2.send("cmd.t", new StringEntity("你被T出聊天室: " + room));
                                    }
                                })
                        )
                ).start();
    }

    static void pushToRoom(String room, Entity message) {
        userList.values().parallelStream().filter(s1 -> room.equals(s1.attr("room")))
                .forEach(s1 -> {
                    RunUtils.runAndTry(() -> {
                        s1.send("cmd.chat", message); //给房间的每个人转发消息
                    });
                });
    }
}