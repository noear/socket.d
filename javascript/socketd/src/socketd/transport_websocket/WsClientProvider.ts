import type { Client } from "../transport/client/Client";
import type { ClientConfig } from "../transport/client/ClientConfig";
import type {ClientProvider} from "../transport/client/ClientProvider";
import {WsClient} from "./WsClient";

export class WsClientProvider implements ClientProvider {
    schemas(): string[] {
        return ["ws", "wss", "sd:ws", "sd:wss"];
    }

    createClient(clientConfig: ClientConfig): Client {
        return  new WsClient(clientConfig);
    }
}
