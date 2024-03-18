from threading import RLock
from typing import List

from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.utils.StrUtil import StrUtil


class LoadBalancer:
    __roundCounter: int = 0  # 轮环计数器
    __lock: RLock = RLock()  # 计数锁

    @staticmethod
    def round_counter_get() -> int:
        LoadBalancer.__lock.acquire()

        try:
            LoadBalancer.__roundCounter += 1
            if LoadBalancer.__roundCounter > 999_999:
                LoadBalancer.__roundCounter = 0
            return LoadBalancer.__roundCounter
        finally:
            LoadBalancer.__lock.release()

    # 根据 poll 获取任意一个
    @staticmethod
    def get_any_by_poll(coll: List[ClientSession]) -> ClientSession:
        return LoadBalancer.get_any(coll, LoadBalancer.round_counter_get())

    # 根据 hash 获取任意一个
    @staticmethod
    def get_any_by_hash(coll: List[ClientSession], diversion: str) -> ClientSession:
        return LoadBalancer.get_any(coll, StrUtil.hash_code(diversion))

    # 获取任意一个
    @staticmethod
    def get_any(coll: list[ClientSession], random: int) -> ClientSession | None:
        if coll is None or coll.__len__() == 0:
            return None
        else:
            sessions: List[ClientSession] = []
            for s in coll:
                if s.is_valid() and not s.is_closing():
                    sessions.append(s)

            if sessions.__len__() == 0:
                return None

            if sessions.__len__() == 1:
                return sessions[0]

            random = abs(random)
            idx = random % sessions.__len__()
            return sessions[idx]

    # 获取第一个
    @staticmethod
    def get_first(coll: List[ClientSession]):
        if coll is None or coll.__len__() == 0:
            return None
        else:
            for s in coll:
                if s.is_valid() and not s.is_closing():
                    return s

            return None
