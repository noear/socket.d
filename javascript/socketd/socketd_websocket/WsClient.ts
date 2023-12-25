import {ClientSession} from "../socketd/transport/client/ClientSession";
import {ClientBase} from "../socketd/transport/client/Client";
import {ClientConfig} from "../socketd/transport/client/ClientConfig";
import {WsChannelAssistant} from "./WsChannelAssistant";
import {ClientConnector } from "../socketd/transport/client/ClientConnector";
import {WsClientConnector} from "./WsClientConnector";
import {ChannelSupporter} from "../socketd/transport/core/ChannelSupporter";
import { ChannelAssistant } from "../socketd/transport/core/ChannelAssistant";
import { Processor } from "../socketd/transport/core/Processor";

export class WsClient extends ClientBase<WsChannelAssistant> implements ChannelSupporter<WebSocket> {
    constructor(clientConfig: ClientConfig) {
        super(clientConfig, new WsChannelAssistant());
    }

    processor(): Processor {
        throw new Error("Method not implemented.");
    }
    assistant(): ChannelAssistant<WebSocket> {
        throw new Error("Method not implemented.");
    }

    protected createConnector(): ClientConnector {
        return new WsClientConnector(this);
    }
}