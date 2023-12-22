import {Entity, Message, Reply} from "./Message";
import {IoConsumer} from "./Types";
import {Channel} from "./Channel";
import {Stream} from "./Stream";
import {ClientSession} from "../client/ClientSession";
import {Handshake} from "./Handshake";


export interface Session extends ClientSession {
    handshake(): Handshake;

    name(): string;

    param(name: string): string;

    paramOrDefault(name: string, def: string): string;

    path(): string;

    pathNew(pathNew: string);

    attrMap(): Map<string, object>;

    attrHas(name: string);

    attr(name: string): object;

    attrOrDefault(name: string, def: object): object;

    attrSet(name: string, val: object);

    sendPing();

    sendAlarm(from: Message, alarm: string);

    reply(from: Message, entity: Entity);

    replyEnd(from: Message, entity: Entity);
}

export class SessionBase implements Session {
    _channel: Channel;
    _sessionId: string;
    _attrMap: Map<string, object>;

    constructor(channel: Channel) {
        this._channel = channel;
        this._sessionId = this.generateId();
    }


    handshake(): Handshake {
        throw new Error("Method not implemented.");
    }

    name(): string {
        throw new Error("Method not implemented.");
    }

    param(name: string): string {
        throw new Error("Method not implemented.");
    }

    paramOrDefault(name: string, def: string): string {
        throw new Error("Method not implemented.");
    }

    path(): string {
        throw new Error("Method not implemented.");
    }

    pathNew(pathNew: string) {
        throw new Error("Method not implemented.");
    }

    attrMap(): Map<string, object> {
        return this._attrMap;
    }

    attrHas(name: string) {
        throw new Error("Method not implemented.");
    }

    attr(name: string): object {
        throw new Error("Method not implemented.");
    }

    attrOrDefault(name: string, def: object): object {
        throw new Error("Method not implemented.");
    }

    attrSet(name: string, val: object) {
        throw new Error("Method not implemented.");
    }

    sendPing() {
        throw new Error("Method not implemented.");
    }

    sendAlarm(from: Message, alarm: string) {
        throw new Error("Method not implemented.");
    }

    reply(from: Message, entity: Entity) {
        throw new Error("Method not implemented.");
    }

    replyEnd(from: Message, entity: Entity) {
        throw new Error("Method not implemented.");
    }

    isValid(): boolean {
        throw new Error("Method not implemented.");
    }

    sessionId(): string {
        throw new Error("Method not implemented.");
    }

    reconnect() {
        throw new Error("Method not implemented.");
    }

    send(event: string, content: Entity) {
        throw new Error("Method not implemented.");
    }

    sendAndRequest(event: string, content: Entity, callback: IoConsumer<Reply>, timeout?: number): Stream {
        throw new Error("Method not implemented.");
    }

    sendAndSubscribe(event: string, content: Entity, callback: IoConsumer<Reply>, timeout?: number): Stream {
        throw new Error("Method not implemented.");
    }

    generateId() {
        return this._channel.getConfig().getIdGenerator().generate();
    }
}

export class SessionDefault extends SessionBase {


}
