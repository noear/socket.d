package org.noear.socketd.protocol;

import org.noear.socketd.Listener;
import org.noear.socketd.Message;
import org.noear.socketd.Session;

import java.io.IOException;

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
public abstract class Processor implements Listener {
    public void onConnect(Channel channel){
        channel.sendHandshaked();
    }

    public void onRead(Channel channel, Frame frame) throws IOException{
        switch (frame.getFlag()) {
            case Connect:{
                //if server
                channel.sendHandshaked(); //->Connack
            }
            case Connack: {
                //
                onOpen(channel.getSession());
            }
            case Ping: {
                if(channel.getHandshaker() == null) {

                }else{
                    channel.sendPong();
                }
            }
            case Pong: {
                channel.sendPing();
            }
            case Message: {
                onMessage(channel.getSession(), frame.getMessage());
            }
        }
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
