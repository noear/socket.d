import type {ChannelAssistant} from "../transport/core/ChannelAssistant";
import type {Frame} from "../transport/core/Frame";
import type {Config} from "../transport/core/Config";
import {ArrayBufferCodecReader, ArrayBufferCodecWriter} from "../transport/core/Codec";
import {SdWebSocket} from "./impl/SdWebSocket";
import {SocketAddress} from "../transport/core/SocketAddress";

export class WsChannelAssistant implements ChannelAssistant<SdWebSocket> {
    _config: Config;

    constructor(config: Config) {
        this._config = config;
    }

    read(buffer: ArrayBuffer): Frame | null {
        return this._config.getCodec().read(new ArrayBufferCodecReader(buffer));
    }

    write(target: SdWebSocket, frame: Frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new ArrayBufferCodecWriter(n));
        target.send(tmp.getBuffer());
    }

    isValid(target: SdWebSocket): boolean {
        return target.isOpen();
    }

    close(target: SdWebSocket) {
        target.close();
    }

    getRemoteAddress(target: SdWebSocket): SocketAddress | null {
        return target.remoteAddress();
    }

    getLocalAddress(target: SdWebSocket): SocketAddress | null {
        return target.localAddress();
    }
}
