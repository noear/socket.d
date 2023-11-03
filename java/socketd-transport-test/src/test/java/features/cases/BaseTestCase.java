package features.cases;

/**
 * @author noear
 * @since 2.0
 */
public abstract class BaseTestCase {
    private final String schema;
    private final int port;

    public String getSchema() {
        return schema;
    }

    public int getPort() {
        return port;
    }

    public BaseTestCase(String schema, int port) {
        this.schema = schema;
        this.port = port;
    }

    /**
     * 开始测试
     */
    public void start() throws Exception {
        System.out.println("------------------ (test start: " + getSchema() + ", port=" + getPort() + ")------------------");
    }

    /**
     * 停止测试
     */
    public void stop() throws Exception {

        System.out.println("------------------ (test stop:  " + getSchema() + ", port=" + getPort() + ")------------------");
    }

    public void onError() {
        System.out.println("------------------ (test error: " + getSchema() + ", port=" + getPort() + ")------------------");
    }
}

