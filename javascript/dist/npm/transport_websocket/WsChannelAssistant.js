"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.WsChannelAssistant = void 0;
const Codec_1 = require("../transport/core/Codec");
class WsChannelAssistant {
    constructor(config) {
        this._config = config;
    }
    read(buffer) {
        return this._config.getCodec().read(new Codec_1.ArrayBufferCodecReader(buffer));
    }
    write(target, frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new Codec_1.ArrayBufferCodecWriter(n));
        target.send(tmp.getBuffer());
    }
    isValid(target) {
        return target.readyState === WebSocket.OPEN;
    }
    close(target) {
        target.close();
    }
    getRemoteAddress(target) {
        throw new Error("Method not implemented.");
    }
    getLocalAddress(target) {
        throw new Error("Method not implemented.");
    }
}
exports.WsChannelAssistant = WsChannelAssistant;
