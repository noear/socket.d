
import {IoConsumer} from "../core/Types";
import {Stream} from "../core/Stream";
import {Entity, Reply} from "../core/Message";

export interface ClientSession {
    sessionId():string;

    send(event: string, entity: Entity);

    sendAndRequest(event: string, entity: Entity, callback: IoConsumer<Reply>): Stream;

    sendAndSubscribe(event: string, entity: Entity, callback: IoConsumer<Reply>): Stream;
}
