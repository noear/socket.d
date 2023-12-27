import {ChannelAssistant} from "../socketd/transport/core/ChannelAssistant";
import { Frame } from "../socketd/transport/core/Message";
import {Config} from "../socketd/transport/core/Config";
import {BufferWriterImpl} from "./impl/BufferWriterImpl";
import {BufferReaderImpl} from "./impl/BufferReaderImpl";

export class WsChannelAssistant implements ChannelAssistant<WebSocket> {
    _config: Config;

    constructor(config: Config) {
        this._config = config;
    }

    read(buffer: ArrayBuffer): Frame {
        let frame = this._config.getCodec().read(new BufferReaderImpl(buffer));
        return frame;
    }

    write(target: WebSocket, frame: Frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new BufferWriterImpl(n));
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