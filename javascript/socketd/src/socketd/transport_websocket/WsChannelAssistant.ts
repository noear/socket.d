import type {ChannelAssistant} from "../transport/core/ChannelAssistant";
import type {Frame} from "../transport/core/Frame";
import type {Config} from "../transport/core/Config";
import {ArrayBufferCodecReader, ArrayBufferCodecWriter} from "../transport/core/Codec";

export class WsChannelAssistant implements ChannelAssistant<WebSocket> {
    _config: Config;

    constructor(config: Config) {
        this._config = config;
    }

    read(buffer: ArrayBuffer): Frame | null{
        return this._config.getCodec().read(new ArrayBufferCodecReader(buffer));
    }

    write(target: WebSocket, frame: Frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new ArrayBufferCodecWriter(n));
        target.send(tmp.getBuffer());
    }

    isValid(target: WebSocket): boolean {
        return target.readyState === WebSocket.OPEN
    }

    close(target: WebSocket) {
        target.close();
    }

    getRemoteAddress(target: WebSocket): string {
        throw new Error("Method not implemented.");
    }

    getLocalAddress(target: WebSocket): string {
        throw new Error("Method not implemented.");
    }
}