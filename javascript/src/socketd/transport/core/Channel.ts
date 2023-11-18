
import {Session} from "./Session";

export interface Channel {
    setHandshake(handshake: object): void

    getHandshake(): object

    send(frame): void

    setSession(session: Session): void

    getSession(): Session
}
