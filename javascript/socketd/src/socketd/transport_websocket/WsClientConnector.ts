import {ClientConnectorBase} from "../transport/client/ClientConnector";
import type { ChannelInternal } from "../transport/core/Channel";
import type {WsClient} from "./WsClient";
import {
    SdWebSocket,
    SdWebSocketCloseEvent,
    SdWebSocketEvent,
    SdWebSocketListener,
    SdWebSocketMessageEvent
} from "./impl/SdWebSocket";
import type {IoConsumer} from "../transport/core/Typealias";
import {ClientHandshakeResult} from "../transport/client/ClientHandshakeResult";
import {EnvBridge} from "./impl/EnvBridge";
import {ChannelDefault} from "../transport/core/ChannelDefault";
import {Flags} from "../transport/core/Constants";
import {SocketdConnectionException} from "../exception/SocketdException";

export class WsClientConnector extends ClientConnectorBase<WsClient> {
    _real: WebSocketClientImpl;

    constructor(client: WsClient) {
        super(client);
    }

    connect(): Promise<ChannelInternal> {
        //关闭之前的资源
        this.close();

        //处理自定义架构的影响（重连时，新建实例比原生重链接口靠谱）
        let url = this._client.getConfig().getUrl();


        return new Promise<ChannelInternal>((resolve, reject) => {
            this._real = new WebSocketClientImpl(url, this._client, (r) => {
                if (r.getThrowable()) {
                    reject(r.getThrowable());
                } else {
                    resolve(r.getChannel());
                }
            });
        })
    }


    close() {
        if (this._real) {
            this._real.close();
        }
    }
}


export class WebSocketClientImpl implements SdWebSocketListener {
    _real: SdWebSocket;
    _client: WsClient;
    _channel: ChannelInternal;
    _handshakeFuture: IoConsumer<ClientHandshakeResult>;

    constructor(url: string, client: WsClient, handshakeFuture: IoConsumer<ClientHandshakeResult>) {
        this._real = EnvBridge.createSdWebSocketClient(url, this);
        this._client = client;
        this._channel = new ChannelDefault(this._real, client);
        this._handshakeFuture = handshakeFuture;
    }

    onOpen(e: SdWebSocketEvent) {
        try {
            this._channel.sendConnect(this._client.getConfig().getUrl());
        } catch (err) {
            console.warn("Client channel sendConnect error", err);
        }
    }

    onMessage(e: SdWebSocketMessageEvent) {
        if (e.data() instanceof String) {
            console.warn("Client channel unsupported onMessage(String test)");
        } else {
            try {
                let frame = this._client.getAssistant().read(e.data());

                if (frame != null) {
                    if (frame.flag() == Flags.Connack) {
                        this._channel.onOpenFuture((r, err) => {
                            if (err == null) {
                                this._handshakeFuture(new ClientHandshakeResult(this._channel, null));
                            } else {
                                this._handshakeFuture(new ClientHandshakeResult(this._channel, err));
                            }
                        });
                    }

                    this._client.getProcessor().onReceive(this._channel, frame);
                }
            } catch (e) {
                if (e instanceof SocketdConnectionException) {
                    this._handshakeFuture(new ClientHandshakeResult(this._channel, e));
                }


                console.warn("WebSocket client onMessage error", e);
            }
        }
    }

    onClose(e: SdWebSocketCloseEvent) {
        this._client.getProcessor().onClose(this._channel);
    }

    onError(e) {
        this._client.getProcessor().onError(this._channel, e);
    }


    close() {
        this._real.close();
    }
}