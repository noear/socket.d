import {Server, ServerBase} from "../transport/server/Server";
import {WsChannelAssistant} from "./WsChannelAssistant";
import {ChannelSupporter} from "../transport/core/ChannelSupporter";
import {SdWebSocket, SdWebSocketCloseEvent, SdWebSocketErrorEvent, SdWebSocketEvent,
    SdWebSocketListener,
    SdWebSocketMessageEvent
} from "./impl/SdWebSocket";
import {SocketD} from "../SocketD";
import NodeWebSocket from 'ws';
import {SdWebSocketNodeJs} from "./impl/SdWebSocketNodeJs";
import {ServerConfig} from "../transport/server/ServerConfig";
import {Constants} from "../transport/core/Constants";

export class WsServer extends ServerBase<WsChannelAssistant> implements ChannelSupporter<SdWebSocket> {
    private _server: NodeWebSocket.Server;

    constructor(config: ServerConfig) {
        super(config, new WsChannelAssistant(config));
    }

    getTitle(): string {
        return "ws/js-websocket/v" + SocketD.version();
    }

    start(): Server {
        if (this._isStarted) {
            throw new Error("Socket.D server started");
        } else {
            this._isStarted = true;
        }

        if(this.getConfig().getHttpServer()){
            this._server = new NodeWebSocket.Server({
                server: this.getConfig().getHttpServer(),
                maxPayload: Constants.MAX_SIZE_FRAME
            });
        }else{
            if (this.getConfig().getHost()) {
                this._server = new NodeWebSocket.Server({
                    port: this.getConfig().getPort(),
                    host: this.getConfig().getHost(),
                    maxPayload: Constants.MAX_SIZE_FRAME
                });
            } else {
                this._server = new NodeWebSocket.Server({
                    port: this.getConfig().getPort(),
                    maxPayload: Constants.MAX_SIZE_FRAME
                });
            }
        }

        const serverListener: SdWebSocketServerListener = new SdWebSocketServerListener(this);

        this._server.on("connection", (ws,req) => {
            //做转换与绑定
            new SdWebSocketNodeJs(ws, req, serverListener);
        });

        console.info("Socket.D server started: {server=" + this.getConfig().getLocalUrl() + "}");

        return this;
    }

    stop() {
        if (this._isStarted) {
            this._isStarted = false;
        } else {
            return;
        }

        super.stop();

        try {
            if (this._server != null) {
                this._server.close();
            }
        } catch (e) {
            console.debug("Server stop error", e);
        }
    }
}

export class SdWebSocketServerListener implements SdWebSocketListener {
    private _server: WsServer;

    constructor(server: WsServer) {
        this._server = server;
    }

    getServer(): WsServer {
        return this._server;
    }

    onOpen(e: SdWebSocketEvent): void {
        let channel = e.socket().attachment();
        this._server.getProcessor().onClose(channel);
    }

    onMessage(e: SdWebSocketMessageEvent): void {
        let channel = e.socket().attachment();
        let frame = this._server.getAssistant().read(e.data());

        if (frame != null) {
            this._server.getProcessor().onReceive(channel, frame);
        }
    }

    onClose(e: SdWebSocketCloseEvent): void {
        let channel = e.socket().attachment();
        this._server.getProcessor().onClose(channel);
    }

    onError(e: SdWebSocketErrorEvent): void {
        let channel = e.socket().attachment();
        if (channel) {
            //有可能未 onOpen，就 onError 了；此时通道未成
            this._server.getProcessor().onError(channel, e.error());
        }
    }
}