package demo.demo05_im;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.core.listener.RouterListener;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.Utils;

import java.util.HashMap;
import java.util.Map;


public class Demo05_Im_Server {
    static Map<String, Session> userList = new HashMap<>();
    public static void main(String[] args) throws Exception {
        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new RouterListener()
                        //::::::::::用户频道处理
                        .of("/", new BuilderListener()
                                .onOpen(s -> {
                                    //用户连接
                                    String user = s.param("u");
                                    if (Utils.isNotEmpty(user)) {
                                        //有用户名，才登录成功
                                        userList.put(s.sessionId(), s);
                                        s.attr("user", user);
                                    } else {
                                        //否则说明是非法的
                                        s.close();
                                    }
                                }).onClose(s -> {
                                    userList.remove(s.sessionId());

                                    String room = s.attr("room");

                                    if (Utils.isNotEmpty(room)) {
                                        pushToRoom(room, new StringEntity("有人退出聊天室：" + s.attr("user")));
                                    }
                                }).on("cmd.join", (s, m) -> {
                                    //::加入房间指令
                                    String room = m.meta("room");

                                    if (Utils.isNotEmpty(room)) {
                                        s.attr("room", room);

                                        pushToRoom(room, new StringEntity("新人加入聊天室：" + s.attr("user")));
                                    }
                                }).on("cmd.chat", (s, m) -> {
                                    //::聊天指令
                                    String room = m.meta("room");

                                    if (Utils.isNotEmpty(room)) {
                                        StringBuilder buf = new StringBuilder();
                                        buf.append(m.meta("sender")).append(": ").append(m.dataAsString());

                                        pushToRoom(room, new StringEntity(buf.toString()));
                                    }
                                }))
                        //::::::::::管理频道处理
                        .of("/admin", new BuilderListener()
                                .onOpen((session) -> {
                                    //管理员签权
                                    String user = session.param("u");
                                    String token = session.param("t");

                                    if ("admin".equals(user) && "mahuateng".equals(token)) {

                                    } else {
                                        session.close();
                                    }
                                }).on("cmd.t", (s, m) -> {
                                    String user = m.meta("u");
                                    String room = m.meta("room");

                                    Session s2 = userList.values().parallelStream().filter(s1 -> user.equals(s1.attr("user"))).findFirst().get();
                                    if (s2 != null) {
                                        s2.attr("room", null);
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