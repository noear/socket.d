package org.noear.socketd.broker;

import java.util.*;

/**
 * @author noear
 * @since 2.0
 */
public class BrokerManager {
    static Map<String, Broker> brokers;

    static {
        brokers = new HashMap<>();
        ServiceLoader.load(Broker.class).iterator().forEachRemaining(broker -> {
            brokers.put(broker.schema(), broker);
        });
    }

    public static Broker getBroker(String schema) {
        if (brokers.size() == 0) {
            throw new IllegalStateException("No Broker providers were found.");
        }

        return brokers.get(schema);
    }
}
