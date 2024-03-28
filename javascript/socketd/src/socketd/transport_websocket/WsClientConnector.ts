import {ClientConnectorBase} from "../transport/client/ClientConnector";
import type { ChannelInternal } from "../transport/core/Channel";
import type {WsClient} from "./WsClient";
import {
    SdWebSocket,
    SdWebSocketCloseEvent,
    SdWebSocketErrorEvent,
    SdWebSocketEvent,
    SdWebSocketListener,
    SdWebSocketMessageEvent
} from "./impl/SdWebSocket";
import type {IoConsumer} from "../transport/core/Typealias";
import {ClientHandshakeResult} from "../transport/client/ClientHandshakeResult";
import {EnvBridge} from "./impl/EnvBridge";
import {ChannelDefault} from "../transport/core/ChannelDefault";
import {Flags} from "../transport/core/Flags";
import {SocketDConnectionException} from "../exception/SocketDException";

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
            try {
                this._real = new WebSocketClientImpl(url, this._client, (r) => {
                    if (r.getThrowable()) {
                        reject(r.getThrowable());
                    } else {
                        resolve(r.getChannel()!);
                    }
                });
            } catch (err) {
                reject(err);
            }
        })
    }


    close() {
        if (this._real) {
            this._real.close();
        }
    }
}


export class WebSocketClientImpl implements SdWebSocketListener {
    private _real: SdWebSocket;
    private _client: WsClient;
    private _channel: ChannelInternal;
    private _handshakeFuture: IoConsumer<ClientHandshakeResult> | null;

    constructor(url: string, client: WsClient, handshakeFuture: IoConsumer<ClientHandshakeResult>) {
        try {
            this._real = EnvBridge.createSdWebSocketClient(url, this);
        } catch (err) {
            //首次连接有可能会失败
            handshakeFuture(new ClientHandshakeResult(null, err));
        }
        this._client = client;
        this._channel = new ChannelDefault(this._real, client);
        this._handshakeFuture = handshakeFuture;
    }

    onOpen(e: SdWebSocketEvent) {
        try {
            this._channel.sendConnect(this._client.getConfig().getUrl(), this._client.getConfig().getMetaMap());
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
                            this.handshakeFutureDo(err);
                        });
                    }

                    this._client.getProcessor().onReceive(this._channel, frame);
                }
            } catch (e) {
                if (e instanceof SocketDConnectionException) {
                    this.handshakeFutureDo(e);
                }


                console.warn("WebSocket client onMessage error", e);
            }
        }
    }

    onClose(e: SdWebSocketCloseEvent) {
        this._client.getProcessor().onClose(this._channel);
    }

    onError(e: SdWebSocketErrorEvent) {
        this.handshakeFutureDo(e.error());
        this._client.getProcessor().onError(this._channel, e.error());
    }

    private handshakeFutureDo(e) {
        if (this._handshakeFuture) {
            this._handshakeFuture(new ClientHandshakeResult(this._channel, e));
        } else {
            this._handshakeFuture = null;
        }
    }


    close() {
        this._real.close();
    }
}