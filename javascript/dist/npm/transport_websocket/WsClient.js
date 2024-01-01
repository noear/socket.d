"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.WsClient = void 0;
const Client_1 = require("../transport/client/Client");
const WsChannelAssistant_1 = require("./WsChannelAssistant");
const WsClientConnector_1 = require("./WsClientConnector");
class WsClient extends Client_1.ClientBase {
    constructor(clientConfig) {
        super(clientConfig, new WsChannelAssistant_1.WsChannelAssistant(clientConfig));
    }
    createConnector() {
        return new WsClientConnector_1.WsClientConnector(this);
    }
}
exports.WsClient = WsClient;
