
from socketd.transport.core.Message import MessageInternal
from socketd.transport.core.FragmentAggregator import FragmentAggregator

from .FragmentAggregatorDefault import FragmentAggregatorDefault
from .FragmentHandlerBase import FragmentHandlerBase


class FragmentHandlerDefault(FragmentHandlerBase):

    def createFragmentAggregator(self, message: MessageInternal) -> FragmentAggregator:
        return FragmentAggregatorDefault(message)

    def aggrEnable(self) -> bool:
        return True
