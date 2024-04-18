import {Listener, SimpleListener} from "./Listener";
import type {ChannelInternal} from "./Channel";
import type {Message} from "./Message";
import type {Frame} from "./Frame";
import {Constants} from "./Constants";
import {EntityMetas} from "./EntityMetas";
import {Flags} from "./Flags";
import {SocketDAlarmException, SocketDConnectionException} from "../../exception/SocketDException";
import {HandshakeDefault} from "./HandshakeDefault";
import {StreamInternal} from "../stream/Stream";

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
export interface Processor {
    /**
     * 设置监听器
     */
    setListener(listener: Listener);

    /**
     * 接收处理
     */
    onReceive(channel: ChannelInternal, frame: Frame);

    /**
     * 打开时
     *
     * @param channel 通道
     */
    onOpen(channel: ChannelInternal);

    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param message 消息
     */
    onMessage(channel: ChannelInternal, message: Message);


    /**
     * 关闭时
     *
     * @param channel 通道
     */
    onClose(channel: ChannelInternal);


    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    onError(channel: ChannelInternal, error: Error);
}

export class ProcessorDefault implements Processor {
    private _listener: Listener;

    constructor() {
        this._listener = new SimpleListener();
    }

    setListener(listener: Listener) {
        if (listener != null) {
            this._listener = listener;
        }
    }


    onReceive(channel: ChannelInternal, frame: Frame) {
        if (channel.getConfig().clientMode()) {
            //console.debug("C-REV:" + frame);
        } else {
            //只打印服务端的（客户端的容易被人看光）
            console.debug("S-REV:" + frame);
        }

        if (frame.flag() == Flags.Connect) {
            channel.setHandshake(new HandshakeDefault(frame.message()!));
            channel.onOpenFuture((r, err) => {
                if (r) {
                    //如果无异常
                    if (channel.isValid()) {
                        //如果还有效，则发送链接确认
                        try {
                            channel.sendConnack(); //->Connack
                        } catch (err) {
                            this.onError(channel, err);
                        }
                    }
                }else{
                    //如果有异常
                    if (channel.isValid()) {
                        //如果还有效，则关闭通道
                        this.onCloseInternal(channel, Constants.CLOSE2001_ERROR);
                    }
                }
            })
            this.onOpen(channel);
        } else if (frame.flag() == Flags.Connack) {
            //if client
            channel.setHandshake(new HandshakeDefault(frame.message()!));
            this.onOpen(channel);
        } else {
            if (channel.getHandshake() == null) {
                channel.close(Constants.CLOSE1001_PROTOCOL_CLOSE);

                if (frame.flag() == Flags.Close) {
                    //说明握手失败了
                    throw new SocketDConnectionException("Connection request was rejected");
                }

                console.warn(`${channel.getConfig().getRoleName()} channel handshake is null, sessionId=${channel.getSession().sessionId()}`);
                return
            }

            //更新最后活动时间
            channel.setLiveTimeAsNow();

            try {
                switch (frame.flag()) {
                    case Flags.Ping: {
                        channel.sendPong();
                        break;
                    }
                    case Flags.Pong: {
                        break;
                    }
                    case Flags.Close: {
                        //关闭通道
                        let code = 0;

                        if (frame.message() != null) {
                            code = frame.message()!.metaAsInt("code");
                        }

                        if (code == 0) {
                            code = Constants.CLOSE1001_PROTOCOL_CLOSE;
                        }

                        this.onCloseInternal(channel, code);
                        break;
                    }
                    case Flags.Alarm: {
                        //结束流，并异常通知
                        const exception = new SocketDAlarmException(frame.message()!);
                        const stream = channel.getConfig().getStreamManger().getStream(frame.message()!.sid());
                        if (stream == null) {
                            this.onError(channel, exception);
                        } else {
                            channel.getConfig().getStreamManger().removeStream(frame.message()!.sid());
                            stream.onError(exception);
                        }
                        break;
                    }
                    case Flags.Pressure: //预留
                        break;
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
                        this.onCloseInternal(channel, Constants.CLOSE1002_PROTOCOL_ILLEGAL);
                    }
                }
            } catch (e) {
                this.onError(channel, e);
            }
        }
    }

    onReceiveDo(channel: ChannelInternal, frame: Frame, isReply: boolean) {
        let stream: StreamInternal<any> | null = null;
        let streamIndex = 1;
        let streamTotal = 1;
        if (isReply) {
            stream = channel.getStream(frame.message()!.sid());
        }


        //如果启用了聚合!
        if (channel.getConfig().getFragmentHandler().aggrEnable()) {
            //尝试聚合分片处理
            const fragmentIdxStr = frame.message()!.meta(EntityMetas.META_DATA_FRAGMENT_IDX);
            if (fragmentIdxStr) {
                //解析分片索引
                streamIndex = parseInt(fragmentIdxStr);
                const frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, streamIndex, frame.message()!);

                if (stream) {
                    streamTotal = parseInt(frame.message()!.metaOrDefault(EntityMetas.META_DATA_FRAGMENT_TOTAL, "1"));
                }

                if (frameNew == null) {
                    if (stream) {
                        stream.onProgress(false, streamIndex, streamTotal);
                    }
                    return;
                } else {
                    frame = frameNew;
                }
            }
        }

        //执行接收处理
        if (isReply) {
            if (stream) {
                stream.onProgress(false, streamIndex, streamTotal);
            }
            channel.retrieve(frame, stream);
        } else {
            this.onMessage(channel, frame.message());
        }
    }

    onOpen(channel: ChannelInternal) {
        try {
            this._listener.onOpen(channel.getSession())
            channel.doOpenFuture(true, null);
        } catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onOpen error`, e);

            channel.doOpenFuture(false, e);
        }
    }

    onMessage(channel: ChannelInternal, message) {
        try {
            this._listener.onMessage(channel.getSession(), message)
        } catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onMessage error`, e);

            this.onError(channel, e);
        }
    }

    onClose(channel: ChannelInternal) {
        if (channel.isClosed() <= Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this.onCloseInternal(channel, Constants.CLOSE2003_DISCONNECTION);
        }
    }

    onCloseInternal(channel: ChannelInternal, code: number) {
        channel.close(code);

        if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this._listener.onClose(channel.getSession());
        }
    }

    onError(channel: ChannelInternal, error: any) {
        this._listener.onError(channel.getSession(), error)
    }
}
