from abc import ABC
from typing import Dict, List
from socketd.cluster.LoadBalancer import LoadBalancer
from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Listener import Listener
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session
from socketd.transport.utils.StrUtil import StrUtil


# 经纪人监听器基类（实现玩家封闭管理）
class BrokerListenerBase(Listener, ABC):
    def __init__(self):
        self.__sessionAll: Dict[str, Session] = {}
        self.__playerSessions: Dict[str, List[Session]] = {}

    # 获取所有会话（包括没有名字的）
    def get_session_all(self) -> List[Session]:
        return list(self.__sessionAll.values())

    # 获取任意会话（包括没有名字的）
    def get_session_any(self) -> ClientSession:
        return LoadBalancer.get_any(self.get_session_all(), LoadBalancer.round_counter_get())

    # 获取会话数量
    def get_session_count(self) -> int:
        return len(self.__sessionAll)

    # 获取所有玩家的名字
    def get_name_all(self) -> List[str]:
        return list(self.__sessionAll.keys())

    # 获取所有玩家数量
    def get_player_count(self, name: str) -> int:
        return len(self.get_player_all(name))

    # 获取所有玩家会话
    def get_player_all(self, name: str) -> List[Session]:
        return self.__playerSessions[name]

    # 获取任意一个玩家会话
    def get_player_any(self, atName: str, requester: Session | None, message: Message | None) -> ClientSession | None:
        if StrUtil.is_empty(atName):
            return None

        if atName.endswith("!"):
            atName = atName[:-1]
            x_hash = None

            if message is not None:
                x_hash = message.meta(EntityMetas.META_X_Hash)

            if StrUtil.is_empty(x_hash):
                if requester is None:
                    return LoadBalancer.get_any_by_poll(self.get_player_all(atName))
                else: # 使用请求者 ip 分流
                    return LoadBalancer.get_any_by_hash(self.get_player_all(atName), requester.remote_address())
            else:
                return LoadBalancer.get_any_by_hash(self.get_player_all(atName), x_hash)
        else:
            return LoadBalancer.get_any_by_poll(self.get_player_all(atName))

    # 添加玩家会话
    def add_player(self, name: str, session: Session):
        if StrUtil.is_not_empty(name):
            tmp: List[Session] = self.__playerSessions.get(name)
            if tmp is None:
                tmp = [session]
                self.__playerSessions[name] = tmp
            tmp.append(session)

        self.__sessionAll[session.session_id()] = session

    # 移除玩家会话
    def remove_player(self, name: str, session: Session):
        if StrUtil.is_not_empty(name):
            tmp: list[Session] = self.get_player_all(name)
            if tmp is not None:
                tmp.remove(session)

        self.__sessionAll.pop(session.session_id())
