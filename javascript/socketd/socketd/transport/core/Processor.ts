import {Listener, SimpleListener} from "./Listener";
import {Channel, ChannelInternal} from "./Channel";
import {Frame, Message} from "./Message";
import {Constants, Flags} from "./Constants";

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


    onOpen(channel: ChannelInternal) {
        this._listener.onOpen(channel.getSession())
    }

    onMessage(channel: ChannelInternal, message) {
        this._listener.onMessage(channel.getSession(), message)
    }

    onCloseInternal(channel: ChannelInternal) {

    }

    onClose(channel: ChannelInternal) {
        this._listener.onClose(channel.getSession())
    }

    onError(channel: ChannelInternal, error: Error) {
        this._listener.onError(channel.getSession(), error)
    }

    onReceive(channel: ChannelInternal, frame) {
        if (frame.flag == Flags.Connect) {
            channel.setHandshake(frame.message);
            channel.onOpenFuture((r,err)=>{
                if (r && channel.isValid()) {
                    //如果还有效，则发送链接确认
                    try {
                        channel.sendConnack(frame.getMessage()); //->Connack
                    } catch (err) {
                        this.onError(channel, err);
                    }
                }
            })
            this.onOpen(channel);
        } else if (frame.flag == Flags.Connack) {
            //if client
            channel.setHandshake(frame.message);
            this.onOpen(channel);
        } else {
            if (channel.getHandshake() == null) {
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
                        let acceptor = channel.getConfig().getStreamManger().getStream(frame.getMessage().sid());
                        if (acceptor == null) {
                            this.onError(channel, exception);
                        } else {
                            channel.getConfig().getStreamManger().removeStream(frame.getMessage().sid());
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

    onReceiveDo(channel: ChannelInternal, frame, isReply) {

    }
}
