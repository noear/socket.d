package demo.demo06_im;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Demo06_Im_Client {
    private static String ADMIN_TOKEN = "admin";// 方便demo测试输入

    private static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    private static String user = null;
    private static String token = null;
    private static ClientSession clientSession = null;
    private static String room;

    public static void main(String[] args) throws Exception {
        //登录
        login();

        while (true) {
            //加入聊天室
            joinRoom();

            //聊天开始
            chatStart();
        }
    }

    /**
     * 开始聊天
     * */
    private static void chatStart() throws Exception {
        if (token == null) {
            System.out.println("开始聊天：");

            while (true) {
                String msg = console.readLine();

                if(room == null){
                    System.out.println("被T出聊天室，需要重新选择聊天室！");
                    return;
                }

                clientSession.send("cmd.chat", new StringEntity(msg)
                        .metaPut("room", room)
                        .metaPut("sender", user));
            }
        }
    }

    /**
     * 加入聊天室
     * */
    private static void joinRoom() throws Exception {
        if (token == null) {
            System.out.println("请选择聊天室进入: c1 或 c2");
            room = console.readLine();

            while ("c1".equals(room) == false && "c2".equals(room) == false) {
                System.out.println("错，请重新选择聊天室进入: c1 或 c2");
                room = console.readLine();
            }

            //加入聊天室
            clientSession.send("cmd.join", new StringEntity("").metaPut("room", room));
        }
    }

    /**
     * 登录
     * */
    private static void login() throws Exception {
        System.out.println("输入用户名：");
        user = console.readLine();

        if ("admin".equals(user)) {
            System.out.println("请输入管理令牌：");
            token = console.readLine();

            while (ADMIN_TOKEN.equals(token) == false) {
                System.out.println("错，请重新输入管理令牌：");
                token = console.readLine();
            }
        }

        System.out.println("开始登录服务器...");

        if (token == null) {
            //进入用户频道
            clientSession = SocketD.createClient("sd:ws://127.0.0.1:8602/?u=" + user).listen(new EventListener().doOnMessage((s, m) -> {
                System.err.println("聊到室：" + m.dataAsString());
            }).doOn("cmd.t", (s, m)->{
                //把房间置空
                room = null;
            })).openOrThow();
        } else {
            System.out.println("进入管理频道");
            //进入管理频道
            clientSession = SocketD.createClient("sd:ws://127.0.0.1:8602/admin?u=" + user + "&t=" + token).openOrThow();
            // 群主上身
            adminStart();
        }

        System.out.println("登录服务器成功!");
    }

    /**
     * 群主上身
     * @throws Exception
     */
    private static void adminStart() throws Exception {
        System.out.println("群管理T人模式：");
        while (true) {
            System.out.println("请输入你想踢的人昵称:");
            String id = console.readLine();

            if(id == null){
                System.err.println("请输入正确的昵称:");
                return;
            }

            clientSession.send("cmd.t", new StringEntity("")
                    .metaPut("room", "当前聊天室")
                    .metaPut("u", id));

            System.err.println("用户已下线:" + id);
        }
    }
}
