from typing import List

from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.utils.StrUtil import StrUtil


class LoadBalancer:
    _roundCounter:int = 0

    @staticmethod
    def roundCounterGet(self)->int:
        self._roundCounter += 1
        if self._roundCounter > 999_999:
            self._roundCounter = 0
        return self._roundCounter


    @staticmethod
    def getAnyByPoll(self, coll:List[ClientSession]) -> ClientSession:
        return self.getAny(coll, self.roundCounterGet())

    @staticmethod
    def getAnyByHash(self, coll:List[ClientSession], diversion:str) -> ClientSession:
        return self.getAny(coll, StrUtil.hash_code(diversion))

    @staticmethod
    def getAny(self, coll:list[ClientSession], random:int) -> ClientSession:
        if coll == None or coll.count() == 0:
            return None
        else:
            sessions:List[ClientSession] = []
            for s in coll:
                if s.is_valid() and not s.is_closing():
                    sessions.extend(s)

            if sessions.count() == 0:
                return None

            if sessions.count() == 1:
                return sessions[0]

            random = abs(random)
            idx = random % sessions.count()
            return sessions[idx]


    @staticmethod
    def getFirst(self, coll:List[ClientSession]):
        if coll == None or coll.count() == 0:
            return None
        else:
            for s in coll:
                if s.is_valid() and not s.is_closing():
                    return s

            return None