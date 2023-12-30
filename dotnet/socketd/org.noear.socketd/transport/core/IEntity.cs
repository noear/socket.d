namespace org.noear.socketd.transport.core;

public interface IEntity {
 /**
 * at
 *
 * @since 2.1
 */
 string at();

 /**
  * 获取元信息字符串（queryString style）
  */
 string metaString();

 /**
  * 获取元信息字典
  */
 Dictionary<string, string> metaMap();

 /**
  * 获取元信息
  */
 string meta(string name);

 /**
  * 获取元信息或默认
  */
 string metaOrDefault(string name, string def);

 /**
  * 放置元信息
  * */
 void putMeta(string name, string val);

 /**
  * 获取数据
  */
 ICodecReader data();

 /**
  * 获取数据并转为字符串
  */
 string dataAsString();

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
 void release();
}