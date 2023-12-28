import {ClientBase} from "../socketd/transport/client/Client";
import {ClientConfig} from "../socketd/transport/client/ClientConfig";
import {WsChannelAssistant} from "./WsChannelAssistant";
import {ClientConnector } from "../socketd/transport/client/ClientConnector";
import {WsClientConnector} from "./WsClientConnector";
import {ChannelSupporter} from "../socketd/transport/core/ChannelSupporter";

export class WsClient extends ClientBase<WsChannelAssistant> implements ChannelSupporter<WebSocket> {
    constructor(clientConfig: ClientConfig) {
        super(clientConfig, new WsChannelAssistant(clientConfig));
    }


    protected createConnector(): ClientConnector {
        return new WsClientConnector(this);
    }
}