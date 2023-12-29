namespace org.noear.socketd.transport.core;

public interface IFragmentAggregator {
 /**
  * 获取流Id
  */
 string getSid();

 /**
  * 数据流大小
  */
 int getDataStreamSize();

 /**
  * 数据总长度
  */
 int getDataLength();

 /**
  * 添加分片
  */
 void add(int index, IMessageInternal message);

 /**
  * 获取聚合帧
  */
 Frame get();
}