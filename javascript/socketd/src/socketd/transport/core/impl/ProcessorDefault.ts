import {FrameIoHandler} from "../FrameIoHandler";
import {Listener} from "../Listener";
import type {ChannelInternal} from "../Channel";
import {Frame} from "../Frame";
import {ChannelAssistant} from "../ChannelAssistant";
import {IoBiConsumer} from "../Typealias";
import {Flags} from "../Flags";
import {HandshakeDefault} from "../HandshakeDefault";
import {Constants} from "../Constants";
import {SocketDAlarmException, SocketDConnectionException} from "../../../exception/SocketDException";
import {StreamInternal} from "../../stream/Stream";
import {EntityMetas} from "../EntityMetas";
import {Processor} from "../Processor";
import {SimpleListener} from "../listener/SimpleListener";

export class ProcessorDefault implements Processor, FrameIoHandler {
    private _listener: Listener;

    constructor() {
        this._listener = new SimpleListener();
    }

    setListener(listener: Listener) {
        if (listener != null) {
            this._listener = listener;
        }
    }

    sendFrame<S>(channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant<S>, target: S) {
        this.sendFrameHandle(channel, frame, channelAssistant, target, (r,err)=>{});
    }

    sendFrameHandle<S>(channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant<S>, target: S, completionHandler: IoBiConsumer<Boolean, Error|any>) {
        try {
            channelAssistant.write(target, frame);

            if (frame.flag() >= Flags.Message) {
                this._listener.onSend(channel.getSession(), frame.message()!);
            }

            completionHandler(true, null);
        } catch (err) {
            completionHandler(false, err);
        }
    }


    reveFrame(channel: ChannelInternal, frame: Frame) {
        this.reveFrameHandle(channel, frame);
    }

    reveFrameHandle(channel: ChannelInternal, frame: Frame) {
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
                } else {
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
                        channel.setAlarmCode(exception.getAlarmCode());

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
                        const code = frame.message()!.metaAsInt("code");
                        channel.setAlarmCode(code);
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
            this.onReply(channel, frame, stream);
        } else {
            this.onMessage(channel, frame);
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

    onMessage(channel: ChannelInternal, frame: Frame) {
        try {
            this._listener.onMessage(channel.getSession(), frame.message()!)
        } catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onMessage error`, e);

            this.onError(channel, e);
        }
    }

    onReply(channel: ChannelInternal, frame: Frame, stream: StreamInternal<any> | null) {
        if (stream) {
            if (stream.demands() < Constants.DEMANDS_MULTIPLE || frame.flag() == Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                channel.getConfig().getStreamManger().removeStream(frame.message()!.sid());
            }

            stream.onReply(frame.message()!);
            this._listener.onReply(channel.getSession(), frame.message()!);
        } else {
            this._listener.onReply(channel.getSession(), frame.message()!);
            console.debug(`${channel.getConfig().getRoleName()} stream not found, sid=${frame.message()!.sid()}, sessionId=${channel.getSession().sessionId()}`);
        }
    }

    onClose(channel: ChannelInternal) {
        if (channel.closeCode() <= Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this.onCloseInternal(channel, Constants.CLOSE2003_DISCONNECTION);
        }
    }

    onCloseInternal(channel: ChannelInternal, code: number) {
        channel.close(code);
    }

    onError(channel: ChannelInternal, error: any) {
        try {
            this._listener.onError(channel.getSession(), error);
        } catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onError error`, e);
        }
    }

    doCloseNotice(channel: ChannelInternal) {
        try {
            this._listener.onClose(channel.getSession());
        } catch (err) {
            this.onError(channel, err)
        }
    }
}