import {Client} from "./Client";
import {ClientConfig} from "./ClientConfig";
import {Processor, ProcessorDefault} from "../core/Processor";

export abstract class ClientBase implements Client {
    _serverUrl: string;
    _config: ClientConfig;
    _processor: Processor;
    _listener: EventListener;

    constructor(serverUrl: string) {
        this._serverUrl = serverUrl;
        this._listener = new EventListener();
        this._config = new Config();
        this._processor = new ProcessorDefault();
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