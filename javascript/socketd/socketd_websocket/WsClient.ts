import {Config} from "../socketd/transport/core/Config";
import {Processor} from "../socketd/transport/core/Processor";
import {EventListener} from "../socketd/transport/core/Listener";
import {IoBiConsumer, IoConsumer} from "../socketd/transport/core/Types";
import {Session, SessionDefault} from "../socketd/transport/core/Session";
import {Message} from "../socketd/transport/core/Message";
import {ClientSession} from "../socketd/transport/client/ClientSession";
import {ChannelDefault} from "../socketd/transport/core/Channel";
import {Client, ClientBase} from "../socketd/transport/client/Client";
import {ClientConfig} from "../socketd/transport/client/ClientConfig";
import {WsChannelAssistant} from "./WsChannelAssistant";

export class WsClient extends ClientBase<WsChannelAssistant> {
    _serverUrl: string;
    _listener: EventListener;

    constructor(clientConfig:ClientConfig) {
        super(clientConfig, new WsChannelAssistant());
        this._listener = new EventListener();
    }

    onOpen(consumer: IoConsumer<Session>): Client {
        this._listener.doOnOpen(consumer);
        return this;
    }

    onMessage(consumer: IoBiConsumer<Session, Message>): Client {
        this._listener.doOnMessage(consumer);
        return this;
    }

    on(event: any, consumer: IoBiConsumer<Session, Message>): Client {
        this._listener.doOn(event, consumer);
        return this;
    }

    onClose(consumer: IoConsumer<Session>): Client {
        this._listener.doOnClose(consumer);
        return this;
    }

    onError(consumer: IoBiConsumer<Session, Error>): Client {
        this._listener.doOnError(consumer);
        return this;
    }


    open(): ClientSession {
        let socket = new WebSocket(this._serverUrl);
        let channel = new ChannelDefault(socket, this._config);

        // socket.onopen = function () {
        //     channel.sendConnect(this._serverUrl);
        // }
        //
        // socket.onmessage = function (e) {
        //     let frame = this.readFrame(e);
        //     this.processor.onReceive(frame);
        // }
        //
        //
        // socket.onclose = function (e) {
        //     this.processor.onClose(e);
        // }
        //
        // socket.onerror = function (e) {
        //     this.processor.onError(e);
        // }
        //
        return new SessionDefault(channel);
    }
}