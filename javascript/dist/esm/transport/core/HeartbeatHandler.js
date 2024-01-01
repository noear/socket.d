export class HeartbeatHandlerDefault {
    constructor(heartbeatHandler) {
        this._heartbeatHandler = heartbeatHandler;
    }
    heartbeat(session) {
        if (this._heartbeatHandler == null) {
            session.sendPing();
        }
        else {
            this._heartbeatHandler(session);
        }
    }
}
