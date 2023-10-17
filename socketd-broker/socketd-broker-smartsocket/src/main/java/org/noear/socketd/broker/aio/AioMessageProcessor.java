package org.noear.socketd.broker.aio;

import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.Processor;
import org.noear.socketd.protocol.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

/**
 * @author noear
 * @since 2.0
 */
public class AioMessageProcessor implements MessageProcessor<Frame> {
    static final Logger log = LoggerFactory.getLogger(AioMessageProcessor.class);

    Processor processor;

    @Override
    public void process(AioSession aioSession, Frame frame) {
        Session session = AioAttachment.getSession(aioSession);
        try {
            processor.onReceive(null, frame);
        } catch (Throwable e) {
            if (session == null) {
                log.warn(e.getMessage(), e);
            } else {
                processor.onError(session, e);
            }
        }
    }

    @Override
    public void stateEvent(AioSession aioSession, StateMachineEnum state, Throwable e) {
        switch (state) {
            case NEW_SESSION:
                processor.onOpen(AioAttachment.getSession(aioSession));
                break;

            case SESSION_CLOSED:
                processor.onClose(AioAttachment.getSession(aioSession));
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                processor.onError(AioAttachment.getSession(aioSession), e);
                break;
        }
    }
}
