package labs.demo82_mvc;

import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.SimpleListener;
import org.noear.socketd.protocol.entity.StringEntity;
import org.noear.socketd.solon.annotation.SocketdClient;
import org.noear.solon.boot.web.MimeType;

import java.io.IOException;

@SocketdClient(url = "tcp://127.0.0.1:6329/test?a=12&b=1")
public class ClientDemo extends SimpleListener {
    @Override
    public void onOpen(Session session) throws IOException {
        super.onOpen(session);

        //设定内容
        StringEntity entity = new StringEntity("{\"order\":1111}");

        //设定头信息
        entity.putMeta("Content-Type", MimeType.APPLICATION_JSON_UTF8_VALUE);
        entity.putMeta("user", "noear");

        //发送
        session.send("/demo", entity);

        //发送
        entity.putMeta("user", "solon");
        session.sendAndSubscribe("/demo", entity, r -> {
            System.out.println(r);
        });
    }
}
