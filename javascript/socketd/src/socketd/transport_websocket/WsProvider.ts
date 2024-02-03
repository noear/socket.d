import type { Client } from "../transport/client/Client";
import type { ClientConfig } from "../transport/client/ClientConfig";
import type {ClientProvider} from "../transport/client/ClientProvider";
import {WsClient} from "./WsClient";
import {ServerProvider} from "../transport/server/ServerProvider";
import { Server } from "../transport/server/Server";
import { ServerConfig } from "../transport/server/ServerConfig";
import {WsServer} from "./WsServer";

export class WsProvider implements ClientProvider, ServerProvider {
    schemas(): string[] {
        return ["ws", "wss", "sd:ws", "sd:wss"];
    }

    createClient(clientConfig: ClientConfig): Client {
        return new WsClient(clientConfig);
    }

    createServer(serverConfig: ServerConfig): Server {
        return new WsServer(serverConfig);
    }
}
