
from socketd.transport.core.Message import MessageInternal
from socketd.transport.core.FragmentAggregator import FragmentAggregator

from .FragmentAggregatorDefault import FragmentAggregatorDefault
from .FragmentHandlerBase import FragmentHandlerBase


class FragmentHandlerDefault(FragmentHandlerBase):

    def create_fragment_aggregator(self, message: MessageInternal) -> FragmentAggregator:
        return FragmentAggregatorDefault(message)

    def aggr_enable(self) -> bool:
        return True
