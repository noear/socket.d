from abc import ABC

from socketd.cluster.LoadBalancer import LoadBalancer
from socketd.transport.core.Listener import Listener
from socketd.transport.core.Session import Session
from socketd.transport.utils.StrUtil import StrUtil

# 经纪人监听器基类（实现玩家封闭管理）
class BrokerListenerBase(Listener,ABC) :
    def __init__(self):
        self.__sessionAll:dict[str,Session] = {}
        self.__playerSessions:dict[str,set[Session]] = {}

    # 获取所有会话（包括没有名字的）
    def get_session_all(self) -> set[Session]:
        return self.__sessionAll.values()

    # 获取任意会话（包括没有名字的）
    def get_session_any(self) -> Session:
        return LoadBalancer.get_any(self.get_session_all())

    # 获取会话数量
    def get_session_count(self) -> int:
        return len(self.__sessionAll)

    # 获取所有玩家的名字
    def get_name_all(self) -> set[str]:
        return self.__sessionAll.keys()

    # 获取所有玩家数量
    def get_player_count(self, name:str) -> int:
        return len(self.get_player_all(name))

    #获取所有玩家会话
    def get_player_all(self, name:str) -> set[Session]:
        return self.__playerSessions[name]

    # 获取任意一个玩家会话
    def get_player_any(self, atName:str, requester: Session | None) -> Session:
        if StrUtil.is_empty(atName):
            return None

        if atName.endswith("!"):
            atName = atName[:-1]

            if requester is None:
                return LoadBalancer.get_any_by_poll(self.get_player_all(atName))
            else:
                return LoadBalancer.get_any_by_hash(self.get_player_all(atName), requester.remote_address())# 使用请求者 ip 分流
        else:
            return LoadBalancer.get_any_by_poll(self.get_player_all(atName))

    # 添加玩家会话
    def add_player(self, name:str, session:Session):
        if StrUtil.is_not_empty(name):
            tmp:set[Session] = self.__playerSessions.get(name)
            if tmp is None:
                tmp = set[Session]
                self.__playerSessions[name] = tmp
            tmp.add(session)

        self.__sessionAll[session.session_id()] = session

    # 移除玩家会话
    def remove_player(self, name:str, session:Session):
        if StrUtil.is_not_empty(name):
            tmp:set[Session] = self.get_player_all(name)
            if tmp is None:
                tmp.pop(session)

        self.__sessionAll.pop(session.session_id())