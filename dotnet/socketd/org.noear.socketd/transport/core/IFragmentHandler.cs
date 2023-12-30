namespace org.noear.socketd.transport.core;

public interface IFragmentHandler {
 /**
  * 获取下个分片
  *
  * @param channel       通道
  * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
  * @param message       总包消息
  */
 IEntity nextFragment(IChannel channel, int fragmentIndex, IMessageInternal message);

 /**
  * 聚合所有分片
  *
  * @param channel       通道
  * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
  * @param message       分片消息
  */
 Frame aggrFragment(IChannel channel, int fragmentIndex, IMessageInternal message);

 /**
  * 聚合启用
  */
 bool aggrEnable();
}