import {Config} from "../socketd/transport/core/Config";
import {Processor} from "../socketd/transport/core/Processor";
import {EventListener} from "../socketd/transport/core/Listener";
import {IoBiConsumer, IoConsumer} from "../socketd/transport/core/Types";
import {Session, SessionDefault} from "../socketd/transport/core/Session";
import {Message} from "../socketd/transport/core/Message";
import {ClientSession} from "../socketd/transport/client/ClientSession";
import {ChannelDefault} from "../socketd/transport/core/Channel";
import {Client} from "../socketd/transport/client/Client";

export class ClientDefault implements Client {
    _serverUrl: string;
    _config: Config;
    _processor: Processor;
    _listener: EventListener;

    constructor(serverUrl: string) {
        this._serverUrl = serverUrl;
        this._listener = new EventListener();
        this._config = new Config();
        this._processor = new Processor(this._listener);
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