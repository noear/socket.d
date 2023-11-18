
import {Entity} from "./Entity";
import {Message} from "./Message";
import {Consumer} from "../../utils/Consumer";

export interface Session {
    isValid(): boolean

    remoteAddress(): object

    localAddress(): object

    handshake(): object

    param(name: string): string

    paramOrDefault(name: string, value: string): string

    path(): string

    pathNew(pathNew: string): void

    // @ts-ignore
    attrMap(): Map<string, object>

    attr<T>(name: string): T

    attrOrDefault<T>(name: string, def: T): T

    attrSet<T>(name: string, value: T): void

    sessionId(): string

    reconnect(): void

    sendPing(): void

    send(topic: string, entity: Entity): void

    sendAndRequest(topic: string, entity: Entity): Entity

    sendAndSubscribe(topic: string, entity: Entity, consumer: Consumer<Entity>): void

    reply(from: Message, entity: Entity): void

    replyEnd(from: Message, entity: Entity): void
}