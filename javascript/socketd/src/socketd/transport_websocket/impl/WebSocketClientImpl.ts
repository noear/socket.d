import type {WsClient} from "../WsClient";
import type {IoConsumer} from "../../transport/core/Typealias";
import {ClientHandshakeResult} from "../../transport/client/ClientHandshakeResult";
import type {ChannelInternal} from "../../transport/core/Channel";
import {ChannelDefault} from "../../transport/core/ChannelDefault";
import {Flags} from "../../transport/core/Constants";
import {SocketdConnectionException} from "../../exception/SocketdException";
import {createWsClient} from '../../bridge/Bridge'

import {
    BridgeWsClient,
    BridgeWsClientConnector,
    BridgeWsCloseEvent,
    BridgeWsEvent,
    BridgeWsMsgEvent
} from "../../bridge/BridgeWsClient";

export class WebSocketClientImpl implements BridgeWsClientConnector {
    _real: BridgeWsClient;
    _client: WsClient;
    _channel: ChannelInternal;
    _handshakeFuture: IoConsumer<ClientHandshakeResult>;

    constructor(url: string, client: WsClient, handshakeFuture: IoConsumer<ClientHandshakeResult>) {
        this._real = createWsClient(url, this);
        this._client = client;
        this._channel = new ChannelDefault(this._real, client);
        this._handshakeFuture = handshakeFuture;
    }

    onOpen(e: BridgeWsEvent) {
        try {
            this._channel.sendConnect(this._client.getConfig().getUrl());
        } catch (err) {
            console.warn("Client channel sendConnect error", err);
        }
    }

    onMessage(e: BridgeWsMsgEvent) {
        if (e.data() instanceof String) {
            console.warn("Client channel unsupported onMessage(String test)");
        } else {
            try {
                let frame = this._client.getAssistant().read(e.data());

                if (frame != null) {
                    if (frame.flag() == Flags.Connack) {
                        this._channel.onOpenFuture((r,err)=>{
                            if (err == null) {
                                this._handshakeFuture(new ClientHandshakeResult(this._channel, null));
                            } else {
                                this._handshakeFuture(new ClientHandshakeResult(this._channel, err));
                            }
                        });
                    }

                    this._client.getProcessor().onReceive(this._channel, frame);
                }
            } catch ( e) {
                if(e instanceof SocketdConnectionException){
                    this._handshakeFuture(new ClientHandshakeResult(this._channel, e));
                }


                console.warn("WebSocket client onMessage error", e);
            }
        }
    }

    onClose(e: BridgeWsCloseEvent) {
        this._client.getProcessor().onClose(this._channel);
    }

    onError(e) {
        this._client.getProcessor().onError(this._channel, e);
    }


    close() {
        this._real.close();
    }
}
