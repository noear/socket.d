import type {ChannelAssistant} from "../transport/core/ChannelAssistant";
import type {Frame} from "../transport/core/Frame";
import type {Config} from "../transport/core/Config";
import {ArrayBufferCodecReader, ArrayBufferCodecWriter} from "../transport/core/Codec";
import {BridgeWsClient} from "../bridge/BridgeWsClient";

export class WsChannelAssistant implements ChannelAssistant<BridgeWsClient> {
    _config: Config;

    constructor(config: Config) {
        this._config = config;
    }

    read(buffer: ArrayBuffer): Frame | null{
        return this._config.getCodec().read(new ArrayBufferCodecReader(buffer));
    }

    write(target: BridgeWsClient, frame: Frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new ArrayBufferCodecWriter(n));
        target.send(tmp.getBuffer());
    }

    isValid(target: BridgeWsClient): boolean {
        return target.isOpen();
    }

    close(target: BridgeWsClient) {
        target.close();
    }

    getRemoteAddress(target: BridgeWsClient): string {
        throw new Error("Method not implemented.");
    }

    getLocalAddress(target: BridgeWsClient): string {
        throw new Error("Method not implemented.");
    }
}
