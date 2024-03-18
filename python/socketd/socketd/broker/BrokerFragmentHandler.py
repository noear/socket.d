from socketd.transport.core.fragment.FragmentHandlerDefault import FragmentHandlerDefault


# 经纪人分片处理（关掉聚合）
class BrokerFragmentHandler(FragmentHandlerDefault):
    def aggr_enable(self) -> bool:
        return False
