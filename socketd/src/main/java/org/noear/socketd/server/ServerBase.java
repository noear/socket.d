package org.noear.socketd.server;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Processor;
import org.noear.socketd.protocol.impl.ProcessorDefault;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ServerBase implements Server {
    protected Processor processor = new ProcessorDefault();

    /**
     * 处理
     */
    @Override
    public void process(Processor processor) {
        if (processor != null) {
            this.processor = processor;
        }
    }

    /**
     * 监听
     */
    @Override
    public void listen(Listener listener) {
        processor.setListener(listener);
    }
}
