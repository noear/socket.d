namespace org.noear.socketd.transport.core;

public interface ICodec
{
 /**
  * 编码读取
  *
  * @param buffer 缓冲
  */
 Frame read(ICodecReader buffer);

 /**
  * 解码写入
  *
  * @param frame         帧
  * @param targetFactory 目标工厂
  */
 T write<T>(Frame frame, Func<int, T> targetFactory) where T : ICodecWriter;
}