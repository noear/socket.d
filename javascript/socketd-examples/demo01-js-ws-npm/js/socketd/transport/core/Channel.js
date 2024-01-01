"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ChannelBase = void 0;
const Frame_1 = require("./Frame");
class ChannelBase {
    constructor(config) {
        this._config = config;
        this._attachments = new Map();
        this._isClosed = 0;
    }
    getAttachment(name) {
        return this._attachments.get(name);
    }
    putAttachment(name, val) {
        if (val == null) {
            this._attachments.delete(name);
        }
        else {
            this._attachments.set(name, val);
        }
    }
    isClosed() {
        return this._isClosed;
    }
    close(code) {
        this._isClosed = code;
        this._attachments.clear();
    }
    getConfig() {
        return this._config;
    }
    setHandshake(handshake) {
        this._handshake = handshake;
    }
    getHandshake() {
        return this._handshake;
    }
    sendConnect(url) {
        this.send(Frame_1.Frames.connectFrame(this.getConfig().getIdGenerator().generate(), url), null);
    }
    sendConnack(connectMessage) {
        this.send(Frame_1.Frames.connackFrame(connectMessage), null);
    }
    sendPing() {
        this.send(Frame_1.Frames.pingFrame(), null);
    }
    sendPong() {
        this.send(Frame_1.Frames.pongFrame(), null);
    }
    sendClose() {
        this.send(Frame_1.Frames.closeFrame(), null);
    }
    sendAlarm(from, alarm) {
        this.send(Frame_1.Frames.alarmFrame(from, alarm), null);
    }
}
exports.ChannelBase = ChannelBase;
