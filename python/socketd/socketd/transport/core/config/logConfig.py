
import logging
# socket.d 默认
logger = logging.getLogger("socketD")
# 异步默认日志
logging.getLogger("asyncio").setLevel(logging.WARNING)
# 少用默认logging ，因为他会阻塞你的线程
