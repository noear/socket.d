import {WsClient} from "../WsClient";
import {IoConsumer} from "../../socketd/transport/core/Typealias";
import {ClientHandshakeResult} from "../../socketd/transport/client/ClientHandshakeResult";
import {ChannelInternal} from "../../socketd/transport/core/Channel";
import {ChannelDefault} from "../../socketd/transport/core/ChannelDefault";
import {Flags} from "../../socketd/transport/core/Constants";
import {SocketdConnectionException} from "../../socketd/exception/SocketdException";

export class WebSocketClientImpl {
    _real: WebSocket;
    _client: WsClient;
    _channel: ChannelInternal;
    _handshakeFuture: IoConsumer<ClientHandshakeResult>;

    constructor(url: string, client: WsClient, handshakeFuture: IoConsumer<ClientHandshakeResult>) {
        this._real = new WebSocket(url);
        this._client = client;
        this._channel = new ChannelDefault(this._real, client);
        this._handshakeFuture = handshakeFuture;

        this._real.binaryType = "arraybuffer";
        this._real.onopen = this.onOpen.bind(this);
        this._real.onmessage = this.onMessage.bind(this);
        this._real.onclose = this.onClose.bind(this);
        this._real.onerror = this.onError.bind(this);
    }

    onOpen(e: Event) {
        try {
            this._channel.sendConnect(this._client.getConfig().getUrl());
        } catch (err) {
            console.warn("Client channel sendConnect error", err);
        }
    }

    onMessage(e: MessageEvent) {
        if (e.data instanceof String) {
            console.warn("Client channel unsupported onMessage(String test)");
        } else {
            try {
                let frame = this._client.getAssistant().read(e.data);

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

    onClose(e: CloseEvent) {
        this._client.getProcessor().onClose(this._channel);
    }

    onError(e) {
        this._client.getProcessor().onError(this._channel, e);
    }


    close() {
        this._real.close();
    }
}
