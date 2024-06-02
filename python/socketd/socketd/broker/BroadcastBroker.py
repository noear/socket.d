import abc

from socketd.transport.core import Entity

# 广播经纪人
class BroadcastBroker:
    # 广播
    # @param event  事件
    # @param entity 实体（转发方式 https://socketd.noear.org/article/737 ）
    @abc.abstractmethod
    def broadcast(self, event:str, entity:Entity):
        ...