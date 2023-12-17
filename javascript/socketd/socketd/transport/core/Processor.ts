import {Listener, SimpleListener} from "./Listener";
import {Channel} from "./Channel";
import {Flags} from "./Flags";
import {Frame, Message} from "./Message";

export interface Processor {
    setListener(listener: Listener);

    onReceive(channel: Channel, frame: Frame);

    onOpen(channel: Channel);

    onMessage(channel: Channel, message: Message);


    onClose(channel: Channel);


    onError(channel: Channel, error: Error);
}

export class ProcessorDefault implements Processor{
    _listener:Listener;
    constructor() {
        this._listener = new SimpleListener();
    }

    setListener(listener: Listener) {
        if(listener != null){
            this._listener = listener;
        }
    }


    onOpen(channel: Channel) {
        this._listener.onOpen(channel.getSession())
    }

    onMessage(channel: Channel, message) {
        this._listener.onMessage(channel.getSession(), message)
    }

    onCloseInternal(channel: Channel) {

    }

    onClose(channel: Channel) {
        this._listener.onClose(channel.getSession())
    }

    onError(channel: Channel, error: Error) {
        this._listener.onError(channel.getSession(), error)
    }

    onReceive(channel: Channel, frame) {
        if (frame.flag == Flags.Connect) {
            channel.setHandshake(frame.message);
            channel.onError = this.onError;
            channel.onOpenFuture = function () {
                if (channel.isValid()) {
                    //如果还有效，则发送链接确认
                    try {
                        channel.sendConnack(frame.getMessage()); //->Connack
                    } catch (err) {
                        this.onError(channel, err);
                    }
                }
            }
            this.onOpen(channel);
        } else if (frame.flag == Flags.Connack) {
            //if client
            channel.setHandshake(frame.message);
            this.onOpen(channel);
        } else {
            if (channel.handshake == null) {
                channel.close(Constants.CLOSE1_PROTOCOL);

                if (frame.flag == Flags.Close) {
                    throw new Error("Connection request was rejected");
                }
                return
            }

            try {
                switch (frame.flag) {
                    case Flags.Ping: {
                        channel.sendPong();
                        break;
                    }
                    case Flags.Pong: {
                        break;
                    }
                    case Flags.Close: {
                        //关闭通道
                        channel.close(Constants.CLOSE1_PROTOCOL);
                        this.onCloseInternal(channel);
                        break;
                    }
                    case Flags.Alarm: {
                        //结束流，并异常通知
                        let exception = new Error(frame.getMessage());
                        let acceptor = channel.config.streamManger.getAcceptor(frame.getMessage().sid());
                        if (acceptor == null) {
                            this.onError(channel, exception);
                        } else {
                            channel.config.streamManger.removeAcceptor(frame.getMessage().sid());
                            acceptor.onError(exception);
                        }
                        break;
                    }
                    case Flags.Message:
                    case Flags.Request:
                    case Flags.Subscribe: {
                        this.onReceiveDo(channel, frame, false);
                        break;
                    }
                    case Flags.Reply:
                    case Flags.ReplyEnd: {
                        this.onReceiveDo(channel, frame, true);
                        break;
                    }
                    default: {
                        channel.close(Constants.CLOSE2_PROTOCOL_ILLEGAL);
                        this.onCloseInternal(channel);
                    }
                }
            } catch (e) {
                this.onError(channel, e);
            }
        }
    }

    onReceiveDo(channel: Channel, frame, isReply) {

    }
}
