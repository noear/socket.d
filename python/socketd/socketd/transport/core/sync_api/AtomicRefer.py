from threading import Lock
from typing import Generic, TypeVar

T = TypeVar('T')


class AtomicRefer(Generic[T]):

    def __init__(self, item: T):
        self.item = item
        self._lock = Lock()

    def get(self) -> T:
        if self._lock.locked():
            return self.item
        else:
            with self._lock:
                return self.item

    def set(self, item: T):
        if self._lock.locked():
            self.item = item
        else:
            with self._lock:
                self.item = item

    def __enter__(self):
        """获取锁"""
        self._lock.acquire()
        return self.item

    def __exit__(self, exc_type, exc_val, exc_tb):
        """释放锁"""
        return self._lock.release()


