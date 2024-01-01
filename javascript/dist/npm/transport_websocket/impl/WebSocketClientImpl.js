"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.WebSocketClientImpl = void 0;
const ClientHandshakeResult_1 = require("../../transport/client/ClientHandshakeResult");
const ChannelDefault_1 = require("../../transport/core/ChannelDefault");
const Constants_1 = require("../../transport/core/Constants");
const SocketdException_1 = require("../../exception/SocketdException");
class WebSocketClientImpl {
    constructor(url, client, handshakeFuture) {
        this._real = new WebSocket(url);
        this._client = client;
        this._channel = new ChannelDefault_1.ChannelDefault(this._real, client);
        this._handshakeFuture = handshakeFuture;
        this._real.binaryType = "arraybuffer";
        this._real.onopen = this.onOpen.bind(this);
        this._real.onmessage = this.onMessage.bind(this);
        this._real.onclose = this.onClose.bind(this);
        this._real.onerror = this.onError.bind(this);
    }
    onOpen(e) {
        try {
            this._channel.sendConnect(this._client.getConfig().getUrl());
        }
        catch (err) {
            console.warn("Client channel sendConnect error", err);
        }
    }
    onMessage(e) {
        if (e.data instanceof String) {
            console.warn("Client channel unsupported onMessage(String test)");
        }
        else {
            try {
                let frame = this._client.getAssistant().read(e.data);
                if (frame != null) {
                    if (frame.flag() == Constants_1.Flags.Connack) {
                        this._channel.onOpenFuture((r, err) => {
                            if (err == null) {
                                this._handshakeFuture(new ClientHandshakeResult_1.ClientHandshakeResult(this._channel, null));
                            }
                            else {
                                this._handshakeFuture(new ClientHandshakeResult_1.ClientHandshakeResult(this._channel, err));
                            }
                        });
                    }
                    this._client.getProcessor().onReceive(this._channel, frame);
                }
            }
            catch (e) {
                if (e instanceof SocketdException_1.SocketdConnectionException) {
                    this._handshakeFuture(new ClientHandshakeResult_1.ClientHandshakeResult(this._channel, e));
                }
                console.warn("WebSocket client onMessage error", e);
            }
        }
    }
    onClose(e) {
        this._client.getProcessor().onClose(this._channel);
    }
    onError(e) {
        this._client.getProcessor().onError(this._channel, e);
    }
    close() {
        this._real.close();
    }
}
exports.WebSocketClientImpl = WebSocketClientImpl;
