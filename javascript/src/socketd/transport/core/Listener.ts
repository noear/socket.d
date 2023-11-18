import {Session} from "./Session";
import {Message} from "./Message";

export interface Listener {
    onOpen(session: Session): void;

    onMessage(session: Session, message: Message): void;

    onClose(session: Session): void;

    onError(session: Session, error: Error): void;
}