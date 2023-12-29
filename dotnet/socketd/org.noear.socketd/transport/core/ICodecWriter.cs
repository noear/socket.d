namespace org.noear.socketd.transport.core;

public interface ICodecWriter
{
 /**
  * 推入一组 byte
  */
 void putBytes(byte[] bytes);

 /**
  * 推入 int
  */
 void putInt(int val);

 /**
  * 推入 char
  */
 void putChar(int val);

 /**
  * 冲刷
  */
 void flush();
}