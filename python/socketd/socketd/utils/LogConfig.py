# 异步日志框架
import sys
import logging

from loguru import logger as _logger

# socket.d 默认
logger = logging.getLogger("socketD")
# 异步默认日志
logging.getLogger("asyncio").setLevel(logging.WARNING)
# 日志用这个（为能用 True，否则格式化异常时会出错）
log = _logger.opt(colors=False)
