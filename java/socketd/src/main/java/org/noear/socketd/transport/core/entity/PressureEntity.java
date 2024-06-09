package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;

/**
 * 压力实体
 *
 * @author noear
 * @since 2.5
 */
public class PressureEntity extends StringEntity {
    private static final PressureEntity instance = new PressureEntity();

    public static PressureEntity getInstance() {
        return instance;
    }

    public PressureEntity() {
        this("Too much pressure");
    }

    public PressureEntity(String description) {
        super(description);
        metaPut("code", String.valueOf(Constants.ALARM3001_PRESSURE));
    }
}
