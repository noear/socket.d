import {Entity} from "./Entity";

export class Message {
    constructor(sid: string, topic: string, entity?: Entity) {
        this.sid = sid;
        this.topic = topic;
        this.entity = entity;
    }

    sid: string
    topic: string
    entity?: Entity
}