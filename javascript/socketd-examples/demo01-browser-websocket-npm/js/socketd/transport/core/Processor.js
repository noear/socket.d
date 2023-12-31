"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProcessorDefault = void 0;
const Listener_1 = require("./Listener");
const Constants_1 = require("./Constants");
const SocketdException_1 = require("../../exception/SocketdException");
const HandshakeDefault_1 = require("./HandshakeDefault");
class ProcessorDefault {
    constructor() {
        this._listener = new Listener_1.SimpleListener();
    }
    setListener(listener) {
        if (listener != null) {
            this._listener = listener;
        }
    }
    onReceive(channel, frame) {
        if (channel.getConfig().clientMode()) {
            console.debug("C-REV:" + frame);
        }
        else {
            console.debug("S-REV:" + frame);
        }
        if (frame.flag() == Constants_1.Flags.Connect) {
            channel.setHandshake(new HandshakeDefault_1.HandshakeDefault(frame.message()));
            channel.onOpenFuture((r, err) => {
                if (r && channel.isValid()) {
                    //如果还有效，则发送链接确认
                    try {
                        channel.sendConnack(frame.message()); //->Connack
                    }
                    catch (err) {
                        this.onError(channel, err);
                    }
                }
            });
            this.onOpen(channel);
        }
        else if (frame.flag() == Constants_1.Flags.Connack) {
            //if client
            channel.setHandshake(new HandshakeDefault_1.HandshakeDefault(frame.message()));
            this.onOpen(channel);
        }
        else {
            if (channel.getHandshake() == null) {
                channel.close(Constants_1.Constants.CLOSE1_PROTOCOL);
                if (frame.flag() == Constants_1.Flags.Close) {
                    //说明握手失败了
                    throw new SocketdException_1.SocketdConnectionException("Connection request was rejected");
                }
                console.warn(`${channel.getConfig().getRoleName()} channel handshake is null, sessionId=${channel.getSession().sessionId()}`);
                return;
            }
            try {
                switch (frame.flag()) {
                    case Constants_1.Flags.Ping: {
                        channel.sendPong();
                        break;
                    }
                    case Constants_1.Flags.Pong: {
                        break;
                    }
                    case Constants_1.Flags.Close: {
                        //关闭通道
                        channel.close(Constants_1.Constants.CLOSE1_PROTOCOL);
                        this.onCloseInternal(channel);
                        break;
                    }
                    case Constants_1.Flags.Alarm: {
                        //结束流，并异常通知
                        const exception = new SocketdException_1.SocketdAlarmException(frame.message());
                        const stream = channel.getConfig().getStreamManger().getStream(frame.message().sid());
                        if (stream == null) {
                            this.onError(channel, exception);
                        }
                        else {
                            channel.getConfig().getStreamManger().removeStream(frame.message().sid());
                            stream.onError(exception);
                        }
                        break;
                    }
                    case Constants_1.Flags.Message:
                    case Constants_1.Flags.Request:
                    case Constants_1.Flags.Subscribe: {
                        this.onReceiveDo(channel, frame, false);
                        break;
                    }
                    case Constants_1.Flags.Reply:
                    case Constants_1.Flags.ReplyEnd: {
                        this.onReceiveDo(channel, frame, true);
                        break;
                    }
                    default: {
                        channel.close(Constants_1.Constants.CLOSE2_PROTOCOL_ILLEGAL);
                        this.onCloseInternal(channel);
                    }
                }
            }
            catch (e) {
                this.onError(channel, e);
            }
        }
    }
    onReceiveDo(channel, frame, isReply) {
        //如果启用了聚合!
        if (channel.getConfig().getFragmentHandler().aggrEnable()) {
            //尝试聚合分片处理
            const fragmentIdxStr = frame.message().meta(Constants_1.EntityMetas.META_DATA_FRAGMENT_IDX);
            if (fragmentIdxStr != null) {
                //解析分片索引
                const index = parseInt(fragmentIdxStr);
                const frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, index, frame.message());
                if (frameNew == null) {
                    return;
                }
                else {
                    frame = frameNew;
                }
            }
        }
        //执行接收处理
        if (isReply) {
            channel.retrieve(frame);
        }
        else {
            this.onMessage(channel, frame.message());
        }
    }
    onOpen(channel) {
        try {
            this._listener.onOpen(channel.getSession());
            channel.doOpenFuture(true, null);
        }
        catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onOpen error`, e);
            channel.doOpenFuture(false, e);
        }
    }
    onMessage(channel, message) {
        try {
            this._listener.onMessage(channel.getSession(), message);
        }
        catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onMessage error`, e);
            this.onError(channel, e);
        }
    }
    onClose(channel) {
        if (channel.isClosed() == 0) {
            this.onCloseInternal(channel);
        }
    }
    onCloseInternal(channel) {
        this._listener.onClose(channel.getSession());
    }
    onError(channel, error) {
        this._listener.onError(channel.getSession(), error);
    }
}
exports.ProcessorDefault = ProcessorDefault;
