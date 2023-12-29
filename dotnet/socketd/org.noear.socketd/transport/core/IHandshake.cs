namespace org.noear.socketd.transport.core;

public interface IHandshake {
 /**
  * 协议版本
  */
 String version();

 /**
  * 获请传输地址
  *
  * @return tcp://192.168.0.1/path?user=1&path=2
  */
 Uri uri();

 /**
  * 获取参数集合
  */
 Dictionary<String, String> paramMap();

 /**
  * 获取参数
  *
  * @param name 参数名
  */
 String param(String name);

 /**
  * 获取参数或默认值
  *
  * @param name 参数名
  * @param def  默认值
  */
 String paramOrDefault(String name, String def);

 /**
  * 放置参数
  */
 IHandshake paramPut(String name, String value);
}