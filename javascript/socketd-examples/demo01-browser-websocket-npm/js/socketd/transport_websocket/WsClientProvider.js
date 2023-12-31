"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.WsClientProvider = void 0;
const WsClient_1 = require("./WsClient");
class WsClientProvider {
    schemas() {
        return ["ws", "wss", "sd:ws", "sd:wss"];
    }
    createClient(clientConfig) {
        return new WsClient_1.WsClient(clientConfig);
    }
}
exports.WsClientProvider = WsClientProvider;
