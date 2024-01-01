import { WsClient } from "./WsClient";
export class WsClientProvider {
    schemas() {
        return ["ws", "wss", "sd:ws", "sd:wss"];
    }
    createClient(clientConfig) {
        return new WsClient(clientConfig);
    }
}
