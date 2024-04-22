import asyncio

from socketd import SocketD
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.impl.LogConfig import log
from socketd.transport.core.listener.EventListener import EventListener


def do_on_open(s:Session):
    s.handshake().out_meta("test", "1")
    log.info("onOpen: " + s.session_id())

def do_on_message(s:Session, m:Message):
    log.info("onMessage: " + m)

def doOn_demo(s:Session, m:Message):
    if m.is_request():
        s.reply(m, StringEntity("me to! ref:" + m.data_as_string()))

    if m.is_subscribe():
        size = m.range_size()
        for i in range(size):
            s.reply(m, StringEntity("me to-" + i))
        s.reply_end(m, StringEntity("welcome to my home!"))

def doOn_upload(s:Session, m:Message):
    if m.is_request():
        fileName = m.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME)
        if fileName is None:
            s.reply(m, StringEntity("file received: " + fileName + ", size: " + m.data_size()))
        else:
            s.reply(m, StringEntity("no file! size: " + m.data_size()))

def doOn_download(s:Session, m:Message):
    if m.is_request():
        fileEntity = StringEntity("...")
        s.reply(m, fileEntity)

def doOn_push(s:Session, m:Message):
    if s.attr_has("push"):
        return

    s.attr_put("push","1")

    for i in range(100):
        if s.attr_has("push") is False:
            break
        s.send("/push", "push test")

def doOn_unpush(s:Session, m:Message):
    s.attr_map().pop("push")

def do_on_close(s:Session):
    log.info("onClose: " + s.session_id())
def do_on_error(s:Session, err:Exception):
    log.warning("onError: " + s.session_id())

def buildListener():
    return (EventListener().do_on_open(do_on_open)
     .do_on_message(do_on_message)
     .do_on_close(do_on_close)
     .do_on_error(do_on_error)
     .do_on("/demo", doOn_demo)
     .do_on("/upload", doOn_upload)
     .do_on("/download", doOn_download)
     .do_on("/push", doOn_push)
     .do_on("/unpush", doOn_push)
     )

async def main():
    await (SocketD.create_server("sd:ws")
            .config(lambda c: c.port(8602).fragment_size(1024 * 1024))
            .listen(buildListener())
            .start())
    await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())