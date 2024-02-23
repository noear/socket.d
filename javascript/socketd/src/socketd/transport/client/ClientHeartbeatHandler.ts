import {Session} from "../core/Session";
import {IoConsumer} from "../core/Typealias";


export interface ClientHeartbeatHandler {
    clientHeartbeat(session:Session);
}

export class ClientHeartbeatHandlerDefault implements ClientHeartbeatHandler {
    private _heartbeatHandler: IoConsumer<Session> | null;

    constructor(heartbeatHandler: IoConsumer<Session> | null) {
        this._heartbeatHandler = heartbeatHandler;

    }

    clientHeartbeat(session: Session) {
        if (this._heartbeatHandler) {
            this._heartbeatHandler!(session);
        } else {
            session.sendPing();
        }
    }
}