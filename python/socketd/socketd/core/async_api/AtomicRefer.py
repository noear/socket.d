import asyncio
from typing import Generic, TypeVar

T = TypeVar('T')


class AtomicRefer(Generic[T]):

    def __init__(self, item: T):
        self.item = item
        self._lock = asyncio.Lock()

    async def get(self) -> T:
        if self._lock.locked():
            return self.item
        else:
            async with self._lock:
                return self.item

    async def set(self, item: T):
        if self._lock.locked():
            self.item = item
        else:
            async with self._lock:
                self.item = item

    async def __aenter__(self):
        """获取锁"""
        await self._lock.acquire()
        return self.item

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """释放锁"""
        return self._lock.release()


