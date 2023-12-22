import {ChannelAssistant} from "../socketd/transport/core/ChannelAssistant";
import { Frame } from "../socketd/transport/core/Message";

export class WsChannelAssistant implements ChannelAssistant<WebSocket> {
    write(target: WebSocket, frame: Frame) {
        throw new Error("Method not implemented.");
    }
    isValid(target: WebSocket): boolean {
        throw new Error("Method not implemented.");
    }
    close(target: WebSocket) {
        throw new Error("Method not implemented.");
    }
    getRemoteAddress(target: WebSocket): string {
        throw new Error("Method not implemented.");
    }
    getLocalAddress(target: WebSocket): string {
        throw new Error("Method not implemented.");
    }
}