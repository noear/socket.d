import abc

from socketd.transport.core import Listener

# 路由选择器
class RouteSelector:
    # 选择
    @abc.abstractmethod
    def select(self, route:str)->Listener:...

    # 放置
    @abc.abstractmethod
    def put(self, route:str, target:Listener):...

    # 移除
    @abc.abstractmethod
    def remove(self, route:str):...

    # 数量
    @abc.abstractmethod
    def size(self)->int:...