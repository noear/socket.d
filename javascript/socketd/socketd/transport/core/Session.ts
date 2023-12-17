import {Message} from "./Message";
import {Entity, Reply} from "./Entity";
import {IoConsumer} from "./Types";
import {Channel} from "./Channel";
import {Stream} from "./Stream";
import {ClientSession} from "../client/ClientSession";



export interface Session extends ClientSession {
    reply(from: Message, entity: Entity);

    replyEnd(from: Message, entity: Entity);
}

export class SessionDefault implements Session {
    constructor(channel:Channel) {
    }
    reply(from: Message, entity: Entity) {
    }

    replyEnd(from: Message, entity: Entity) {
    }

    send(event: string, entity: Entity) {
    }

    sendAndRequest(event: string, entity: Entity, callback: IoConsumer<Reply>): Stream {
        return null;
    }

    sendAndSubscribe(event: string, entity: Entity, callback: IoConsumer<Reply>): Stream {
        return null;
    }
}
