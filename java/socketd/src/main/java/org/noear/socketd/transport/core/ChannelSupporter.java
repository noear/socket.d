package org.noear.socketd.transport.core;

/**
 * @author noear
 * @since 2.1
 */
public interface ChannelSupporter<S> {
    Processor processor();
    Config config();

    ChannelAssistant<S> assistant();
}
