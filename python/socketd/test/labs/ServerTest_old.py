import asyncio
import logging

from socketd import SocketD
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.utils.LogConfig import log
from socketd.transport.core.listener.EventListener import EventListener


async def do_on_open(s:Session):
    s.handshake().out_meta("test", "1")
    log.info("onOpen: " + s.session_id())

async def doOn_demo(s:Session, m:Message):
    if m.is_request():
        await s.reply(m, StringEntity("me to! ref:" + m.data_as_string()))

    if m.is_subscribe():
        size = m.range_size()
        for i in range(size):
            await s.reply(m, StringEntity("me to-" + str(i)))
        await s.reply_end(m, StringEntity("welcome to my home!"))

async def doOn_upload(s:Session, m:Message):
    if m.is_request():
        fileName = m.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME)
        if fileName:
            await s.reply(m, StringEntity("file received: " + fileName + ", size: " + str(m.data_size())))
        else:
            await s.reply(m, StringEntity("no file! size: " + str(m.data_size())))

async def doOn_download(s:Session, m:Message):
    if m.is_request():
        fileEntity = StringEntity("...")
        await s.reply(m, fileEntity)

async def doOn_push(s:Session, m:Message):
    if s.attr_has("push"):
        return

    s.attr_put("push","1")

    for i in range(100):
        if s.attr_has("push") is False:
            break
        await asyncio.sleep(0.1)
        s.send("/push", StringEntity("push test"))

async def doOn_error(s:Session, err:Exception):
    logging.exception(f"---onError: \n{err}")

def buildListener():
    return (EventListener()
             .do_on_open(do_on_open)
             .do_on_message(lambda s,m: log.info("onMessage: " + str(m)))
             .do_on_close(lambda s:log.info("onClose: " + s.session_id()))
             .do_on_error(doOn_error)
             .do_on("/demo", doOn_demo)
             .do_on("/upload", doOn_upload)
             .do_on("/download", doOn_download)
             .do_on("/push", doOn_push)
             .do_on("/unpush", lambda s,m: s.attr_del("push"))
            )

async def main():
    await (SocketD.create_server("sd:ws")
            .config(lambda c: c.port(8602).fragment_size(1024 * 1024).use_subprotocols(True))
            .listen(buildListener())
            .start())
    await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())