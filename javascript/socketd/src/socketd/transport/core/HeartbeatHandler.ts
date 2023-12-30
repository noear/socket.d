import type {Session} from "./Session";
import type {IoConsumer} from "./Typealias";

export interface HeartbeatHandler{
    heartbeat(session:Session);
}

export class HeartbeatHandlerDefault implements HeartbeatHandler {
    private _heartbeatHandler: IoConsumer<Session> | null;

    constructor(heartbeatHandler: IoConsumer<Session> | null) {
        this._heartbeatHandler = heartbeatHandler;

    }

    heartbeat(session: Session) {
        if (this._heartbeatHandler == null) {
            session.sendPing();
        } else {
            this._heartbeatHandler!(session);
        }
    }
}