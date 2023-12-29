namespace org.noear.socketd.transport.core;

public interface IEntity
{
    /**
    * at
    *
    * @since 2.1
    */
    String at();

    /**
     * 获取元信息字符串（queryString style）
     */
    String metaString();

    /**
     * 获取元信息字典
     */
    Dictionary<String, String> metaMap();

    /**
     * 获取元信息
     */
    String meta(String name);

    /**
     * 获取元信息或默认
     */
    String metaOrDefault(String name, String def);

    /**
     * 放置元信息
     * */
    void putMeta(String name, String val);

    /**
     * 获取数据
     */
    byte[] data();

    /**
     * 获取数据并转为字符串
     */
    String dataAsString();

    /**
     * 获取数据并转为字节数组
     */
    byte[] dataAsBytes();

    /**
     * 获取数据长度
     */
    int dataSize();

    /**
     * 释放资源
     */
    void release() ;
}