import {Session} from "../Session";
import {Channel} from "../Channel";
import {Entity} from "../Entity";
import {Consumer} from "../../../utils/Consumer";
import {Message} from "../Message";

export class SessionDefault implements Session {
    constructor(channel: Channel) {
        this.channel = channel;
    }

    channel: Channel;

    attr<T>(name: string): T {
        return undefined;
    }

    // @ts-ignore
    attrMap(): Map<string, object> {
        return undefined;
    }

    attrOrDefault<T>(name: string, def: T): T {
        return undefined;
    }

    attrSet<T>(name: string, value: T): void {
    }

    handshake(): object {
        return undefined;
    }

    isValid(): boolean {
        return false;
    }

    localAddress(): object {
        return undefined;
    }

    param(name: string): string {
        return "";
    }

    paramOrDefault(name: string, value: string): string {
        return "";
    }

    path(): string {
        return "";
    }

    pathNew(pathNew: string): void {
    }

    reconnect(): void {
    }

    remoteAddress(): object {
        return undefined;
    }

    reply(from: Message, entity: Entity): void {
    }

    replyEnd(from: Message, entity: Entity): void {
    }

    send(topic: string, entity: Entity): void {
    }

    sendAndRequest(topic: string, entity: Entity): Entity {
        return undefined;
    }

    sendAndSubscribe(topic: string, entity: Entity, consumer: Consumer<Entity>): void {
    }

    sendPing(): void {
    }

    sessionId(): string {
        return "";
    }

}

