import { ClientBase } from "../transport/client/Client";
import { WsChannelAssistant } from "./WsChannelAssistant";
import { WsClientConnector } from "./WsClientConnector";
export class WsClient extends ClientBase {
    constructor(clientConfig) {
        super(clientConfig, new WsChannelAssistant(clientConfig));
    }
    createConnector() {
        return new WsClientConnector(this);
    }
}
