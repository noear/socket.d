package labs.transport;

import org.noear.socketd.utils.MemoryUtils;

/**
 * @author noear 2024/5/16 created
 */
public class Test {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        for (int i = 0; i < 100_000; i++) {
            MemoryUtils.getUseMemoryRatio();
        }

        System.out.println(System.currentTimeMillis() - time);
    }
}
