"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ChannelDefault = void 0;
const Frame_1 = require("./Frame");
const Message_1 = require("./Message");
const Constants_1 = require("./Constants");
const Channel_1 = require("./Channel");
const SessionDefault_1 = require("./SessionDefault");
class ChannelDefault extends Channel_1.ChannelBase {
    constructor(source, supporter) {
        super(supporter.getConfig());
        this._source = source;
        this._processor = supporter.getProcessor();
        this._assistant = supporter.getAssistant();
        this._streamManger = supporter.getConfig().getStreamManger();
    }
    onOpenFuture(future) {
        this._onOpenFuture = future;
    }
    doOpenFuture(r, e) {
        if (this._onOpenFuture) {
            this._onOpenFuture(r, e);
        }
    }
    isValid() {
        return this.isClosed() == 0 && this._assistant.isValid(this._source);
    }
    config() {
        return this._config;
    }
    sendPing() {
        this.send(Frame_1.Frames.pingFrame(), null);
    }
    sendPong() {
        this.send(Frame_1.Frames.pongFrame(), null);
    }
    send(frame, stream) {
        // if (this.getConfig().clientMode()) {
        //     console.trace("C-SEN:" + frame);
        // } else {
        //     console.trace("S-SEN:" + frame);
        // }
        if (frame.message()) {
            const message = frame.message();
            //注册流接收器
            if (stream != null) {
                this._streamManger.addStream(message.sid(), stream);
            }
            //如果有实体（尝试分片）
            if (message.entity() != null) {
                //确保用完自动关闭
                if (message.dataSize() > this.getConfig().getFragmentSize()) {
                    message.putMeta(Constants_1.EntityMetas.META_DATA_LENGTH, message.dataSize().toString());
                }
                this.getConfig().getFragmentHandler().spliFragment(this, message, fragmentEntity => {
                    //主要是 sid 和 entity
                    const fragmentFrame = new Frame_1.Frame(frame.flag(), new Message_1.MessageBuilder()
                        .flag(frame.flag())
                        .sid(message.sid())
                        .event(message.event())
                        .entity(fragmentEntity)
                        .build());
                    this._assistant.write(this._source, fragmentFrame);
                });
                return;
            }
        }
        //不满足分片条件，直接发
        this._assistant.write(this._source, frame);
    }
    retrieve(frame) {
        const stream = this._streamManger.getStream(frame.message().sid());
        if (stream != null) {
            if (stream.isSingle() || frame.flag() == Constants_1.Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                this._streamManger.removeStream(frame.message().sid());
            }
            if (stream.isSingle()) {
                //单收时，内部已经是异步机制
                stream.onAccept(frame.message(), this);
            }
            else {
                //改为异步处理，避免卡死Io线程
                stream.onAccept(frame.message(), this);
            }
        }
        else {
            console.debug(`${this.getConfig().getRoleName()} stream not found, sid=${frame.message().sid()}, sessionId=${this.getSession().sessionId()}`);
        }
    }
    reconnect() {
        //由 ClientChannel 实现
    }
    onError(error) {
        this._processor.onError(this, error);
    }
    getSession() {
        if (this._session == null) {
            this._session = new SessionDefault_1.SessionDefault(this);
        }
        return this._session;
    }
    setSession(session) {
        this._session = session;
    }
    close(code) {
        console.debug(`${this.getConfig().getRoleName()} channel will be closed, sessionId=${this.getSession().sessionId()}`);
        try {
            super.close(code);
            this._assistant.close(this._source);
        }
        catch (e) {
            console.warn(`${this.getConfig().getRoleName()} channel close error, sessionId=${this.getSession().sessionId()}`, e);
        }
    }
}
exports.ChannelDefault = ChannelDefault;
