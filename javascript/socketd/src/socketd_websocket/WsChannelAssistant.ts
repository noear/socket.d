import type {ChannelAssistant} from "../socketd/transport/core/ChannelAssistant";
import type {Frame} from "../socketd/transport/core/Frame";
import type {Config} from "../socketd/transport/core/Config";
import {ArrayBufferCodecWriter} from "./impl/ArrayBufferCodecWriter";
import {ArrayBufferCodecReader} from "./impl/ArrayBufferCodecReader";

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