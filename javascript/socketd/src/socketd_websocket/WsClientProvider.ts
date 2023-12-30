import type { Client } from "../socketd/transport/client/Client";
import type { ClientConfig } from "../socketd/transport/client/ClientConfig";
import type {ClientProvider} from "../socketd/transport/client/ClientProvider";
import {WsClient} from "./WsClient";

export class WsClientProvider implements ClientProvider {
    schemas(): string[] {
        return ["ws", "wss", "sd:ws", "sd:wss"];
    }

    createClient(clientConfig: ClientConfig): Client {
        return  new WsClient(clientConfig);
    }
}
