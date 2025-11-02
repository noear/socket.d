package features.cases;

/**
 * @author noear
 * @since 2.0
 */
public abstract class BaseTestCase {
    public static final String file_sml_receive = "/Users/noear/Downloads/socketd-upload.mov";
    public static final String file_sml_send = "/Users/noear/Movies/snack3-rce-poc.mov";

    public static final String file_big_1g_receive = "/Users/noear/Downloads/socketd-big-upload.mov";
    public static final String file_big_1g_send = "/Users/noear/Movies/[Socket.D 实战] 直播手写 FolkMQ (4).mov";

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

