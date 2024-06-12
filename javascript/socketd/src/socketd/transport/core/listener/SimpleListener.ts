import type {Session} from "../Session";
import type {Message} from "../Message";
import {Listener} from "../Listener";

/**
 * 简单监听器（一般用于占位）
 *
 * @author noear
 * @since 2.0
 */
export class SimpleListener implements Listener {
    onOpen(session: Session) {

    }

    onMessage(session: Session, message: Message) {

    }

    onReply(session: Session, message: Message) {

    }

    onSend(session: Session, message: Message) {
    }

    onClose(session: Session) {

    }

    onError(session: Session, error: Error) {

    }
}