import {Session} from "./Session";
import {IoConsumer} from "./Types";

export interface HeartbeatHandler{
    heartbeat(session:Session);
}

export class HeartbeatHandlerDefault implements HeartbeatHandler {
    _heartbeatHandler: IoConsumer<Session>;

    constructor(heartbeatHandler: IoConsumer<Session>) {
        this._heartbeatHandler = heartbeatHandler;

    }

    heartbeat(session: Session) {
        if (this._heartbeatHandler == null) {
            session.sendPing();
        } else {
            this._heartbeatHandler(session);
        }
    }
}