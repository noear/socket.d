import {ClientBase} from "../transport/client/Client";
import type {ClientConfig} from "../transport/client/ClientConfig";
import {WsChannelAssistant} from "./WsChannelAssistant";
import type {ClientConnector } from "../transport/client/ClientConnector";
import {WsClientConnector} from "./WsClientConnector";
import type {ChannelSupporter} from "../transport/core/ChannelSupporter";
import {BridgeWsClient} from "./bridge/BridgeWsClient";

export class WsClient extends ClientBase<WsChannelAssistant> implements ChannelSupporter<BridgeWsClient> {
    constructor(clientConfig: ClientConfig) {
        super(clientConfig, new WsChannelAssistant(clientConfig));
    }


    protected createConnector(): ClientConnector {
        return new WsClientConnector(this);
    }
}
