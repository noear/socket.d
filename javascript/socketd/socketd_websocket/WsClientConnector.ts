import {ClientConnectorBase} from "../socketd/transport/client/ClientConnector";
import { ChannelInternal } from "../socketd/transport/core/Channel";
import {WsClient} from "./WsClient";

export class WsClientConnector extends ClientConnectorBase<WsClient> {
    constructor(client: WsClient) {
        super(client);
    }

    connect(): ChannelInternal {
        throw new Error("Method not implemented.");
    }

    close() {
        throw new Error("Method not implemented.");
    }
}