"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.HeartbeatHandlerDefault = void 0;
class HeartbeatHandlerDefault {
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
exports.HeartbeatHandlerDefault = HeartbeatHandlerDefault;
