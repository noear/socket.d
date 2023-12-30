import {ClientBase} from "../socketd/transport/client/Client";
import type {ClientConfig} from "../socketd/transport/client/ClientConfig";
import {WsChannelAssistant} from "./WsChannelAssistant";
import type {ClientConnector } from "../socketd/transport/client/ClientConnector";
import {WsClientConnector} from "./WsClientConnector";
import type {ChannelSupporter} from "../socketd/transport/core/ChannelSupporter";

export class WsClient extends ClientBase<WsChannelAssistant> implements ChannelSupporter<WebSocket> {
    constructor(clientConfig: ClientConfig) {
        super(clientConfig, new WsChannelAssistant(clientConfig));
    }


    protected createConnector(): ClientConnector {
        return new WsClientConnector(this);
    }
}