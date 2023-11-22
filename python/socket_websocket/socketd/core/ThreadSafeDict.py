import threading


class ThreadSafeDict:
    def __init__(self):
        self._dict = {}
        self._lock = threading.Lock()

    def get(self, key, default=None):
        with self._lock:
            return self._dict.get(key, default)

    def set(self, key, value):
        with self._lock:
            self._dict[key] = value

    def delete(self, key):
        with self._lock:
            del self._dict[key]

    def keys(self):
        with self._lock:
            return list(self._dict.keys())

    def values(self):
        with self._lock:
            return list(self._dict.values())

    def items(self):
        with self._lock:
            return self._dict.items()

    def clear(self):
        with self._lock:
            self._dict.clear()

    def size(self):
        with self._lock:
            return self._dict.__len__()

    @property
    def dict(self):
        return self._dict
