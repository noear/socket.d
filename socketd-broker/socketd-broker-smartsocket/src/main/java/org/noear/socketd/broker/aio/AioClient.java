package org.noear.socketd.broker.aio;

import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientBase;
import org.noear.socketd.protocol.Session;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author noear 2023/10/17 created
 */
public class AioClient extends ClientBase implements Client {

    @Override
    public Session open() throws IOException, TimeoutException {
        return null;
    }
}
