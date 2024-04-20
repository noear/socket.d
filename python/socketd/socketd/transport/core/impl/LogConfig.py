# 异步日志框架
import sys
import logging

from loguru import logger as _logger

# socket.d 默认
logger = logging.getLogger("socketD")
# 异步默认日志
logging.getLogger("asyncio").setLevel(logging.WARNING)
# 删除原本的处理器 # 少用默认logging ，因为他会阻塞你的线程
_logger.remove()
# 日志用这个
log = _logger.opt(colors=True)
log.add(sys.stdin, enqueue=True)
