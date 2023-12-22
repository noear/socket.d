import {Session} from "./Session";

export interface HeartbeatHandler {
    heartbeat(session: Session)
}