class MapUtils(object):
    @staticmethod
    def remove(map:dict, name:str) -> object:
        _tmp = map.get(name)
        if _tmp is not None:
            map.pop(name)
        return _tmp