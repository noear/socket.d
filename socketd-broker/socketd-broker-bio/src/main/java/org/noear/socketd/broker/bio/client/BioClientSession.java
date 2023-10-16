package org.noear.socketd.broker.bio.client;

import com.sun.xml.internal.ws.api.message.Attachment;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.Message;
import org.noear.socketd.protocol.Session;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author noear 2023/10/13 created
 */
public class BioClientSession implements Session, Listener {
    Socket socket;
    Listener listener;

    Map<String, Attachment> attachmentMap = new HashMap<>();

    public BioClientSession(Socket socket, Listener listener){
        this.socket = socket;
        this.listener = listener;
    }

    protected Socket getSocket() {
        return socket;
    }

    @Override
    public <T> T getAttachment(String key) {
        return null;
    }

    @Override
    public <T> void setAttachment(String key, T value) {

    }

    @Override
    public void send(Message message) {

    }

    @Override
    public Message sendAndRequest(Message message) {
        return null;
    }

    @Override
    public void sendAndSubscribe(Message message, Consumer<Message> subscriber) {

    }

    public void start(){
        new Thread(() -> {
            while (true) {
                if (socket.isClosed()) {
                    onClose(this);
                    break;
                }

                try {
                    Message message = receive();

                    if (message != null) {
                        onMessage(this, message);
                    }
                } catch (Exception ex) {
                    onError(this, ex);
                }
            }
        }).start();
    }

    private Message receive(){
        return null;
    }

    @Override
    public void onOpen(Session session) {

    }

    @Override
    public void onMessage(Session session, Message message) {

    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable error) {

    }
}
