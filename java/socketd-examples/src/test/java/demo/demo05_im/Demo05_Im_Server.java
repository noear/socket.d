package demo.demo05_im;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.core.listener.RouterListener;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class Demo05_Im_Server {
    public static void main(String[] args) throws Exception {
        Map<String, Session> userList = new HashMap<>();

        SocketD.createServer(new ServerConfig("sd:udp").port(8602))
                .listen(new RouterListener()
                        //::::::::::用户频道处理
                        .of("/", new BuilderListener()
                                .onOpen(s -> {
                                    //用户连接
                                    String user = s.getHandshake().getParam("u");
                                    if (Utils.isNotEmpty(user)) {
                                        //有用户名，才登录成功
                                        userList.put(s.getSessionId(), s);
                                        s.setAttr("user", user);
                                    } else {
                                        //否则说明是非法的
                                        s.close();
                                    }
                                }).onClose(s -> {
                                    userList.remove(s.getSessionId());

                                    String room = s.getAttr("room");

                                    if (Utils.isNotEmpty(room)) {
                                        for (Session s2 : userList.values().stream()
                                                .filter(s1 -> room.equals(s.getAttr("room")))
                                                .collect(Collectors.toList())) {

                                            //给房间的人转发消息
                                            RunUtils.runAnTry(() -> {
                                                s2.send("cmd.chat", new StringEntity("新人加入聊天室：" + s.getAttr("user")));
                                            });
                                        }
                                    }
                                }).on("cmd.join", (s, m) -> {
                                    //::加入房间指令
                                    String room = m.getMeta("room");

                                    if (Utils.isNotEmpty(room)) {
                                        s.setAttr("room", room);

                                        for (Session s2 : userList.values().stream()
                                                .filter(s1 -> room.equals(s.getAttr("room")))
                                                .collect(Collectors.toList())) {

                                            //给房间的人转发消息
                                            RunUtils.runAnTry(() -> {
                                                s2.send("cmd.chat", new StringEntity("新人加入聊天室：" + s.getAttr("user")));
                                            });
                                        }
                                    }
                                }).on("cmd.chat", (s, m) -> {
                                    //::聊天指令
                                    String room = m.getMeta("room");

                                    if (Utils.isNotEmpty(room)) {

                                        StringBuilder buf = new StringBuilder();
                                        buf.append(m.getMeta("sender")).append(": ")
                                                .append(m.getDataAsString());


                                        for (Session s2 : userList.values().stream()
                                                .filter(s1 -> room.equals(s.getAttr("room")))
                                                .collect(Collectors.toList())) {
                                            //给房间的人转发消息
                                            RunUtils.runAnTry(() -> {
                                                s2.send("cmd.chat", new StringEntity(buf.toString()));
                                            });
                                        }
                                    }
                                }))
                        //::::::::::管理频道处理
                        .of("/admin", new BuilderListener()
                                .onOpen((session) -> {
                                    //管理员签权
                                    String user = session.getHandshake().getParam("u");
                                    String token = session.getHandshake().getParam("t");

                                    if ("admin".equals(user) && "pzuVU7MCXVTcRkve".equals(token)) {

                                    } else {
                                        session.close();
                                    }
                                }).on("cmd.t", (s, m) -> {
                                    String user = m.getMeta("u");
                                    String room = m.getMeta("room");

                                    Session s2 = userList.values().stream().filter(s1 -> user.equals(s1.getAttr("user"))).findFirst().get();
                                    if (s2 != null) {
                                        s2.setAttr("room", null);
                                        s2.send("cmd.t", new StringEntity("你被T出聊天室: " + room));
                                    }
                                })))
                .start();
    }
}
