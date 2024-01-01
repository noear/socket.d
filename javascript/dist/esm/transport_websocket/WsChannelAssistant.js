import { ArrayBufferCodecReader, ArrayBufferCodecWriter } from "../transport/core/Codec";
export class WsChannelAssistant {
    constructor(config) {
        this._config = config;
    }
    read(buffer) {
        return this._config.getCodec().read(new ArrayBufferCodecReader(buffer));
    }
    write(target, frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new ArrayBufferCodecWriter(n));
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
