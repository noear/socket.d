package labs.demo83_mvc2;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.bean.LifecycleBean;

@Component
public class ClientDemo implements LifecycleBean {
    @Override
    public void start() throws Throwable {
        Session session = SocketD.createClient("tcp://127.0.0.1:8602/test?a=12&b=1").open();

        //设定内容
        StringEntity entity = new StringEntity("{\"order\":12345}");

        //设定头信息
        entity.putMeta("Content-Type", MimeType.APPLICATION_JSON_UTF8_VALUE);
        entity.putMeta("user", "noear");

        //发送
        session.send("/demo", entity);

        //发送2
        entity.putMeta("user", "solon");
        entity.getData().reset();
        Entity response = session.sendAndRequest("/demo2", entity);
        System.out.println(response);
    }
}
