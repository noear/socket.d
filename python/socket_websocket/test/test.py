from socketd.core.Buffer import Buffer
from socketd.core.Costants import Flag
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.Frame import Frame
from socketd.core.module.MessageDefault import MessageDefault
from socketd.core.module.StringEntity import StringEntity
from socketd.transport.CodecByteBuffer import CodecByteBuffer


def main():
    b = Buffer()
    b.put_int(Flag.Message.value)
    print(b.getvalue())
    b.flip()
    print(b.getvalue())
    print(b.size())

    code = CodecByteBuffer(ServerConfig("ws"))
    b1 = code.write(Frame(Flag.Message,
                          MessageDefault().set_sid("1700534070000000001")
                          .set_flag(Flag.Subscribe)
                          .set_topic("tcp-java://127.0.0.1:9386/path?u=a&p=2")
                          .set_entity(StringEntity("test"))
                          ),
                    lambda l: Buffer())
    print(b1.getvalue())
    b1.seek(0)
    b2 = code.read(b1)
    print(b2)
    b1.close()


if __name__ == "__main__":
    # main()
    a = """nXtQUotVF0tP-809pRJbcYg25qj8gCO
    b6tBUxtzYhQt6-0boy45wiBV30pAp3uv
    qYtrU8tD9tWt0-V2jVonvtkG8VxY0Dcgj
    XrtpUgtYarfBtg-866KDoRT0MmeW250tqb
    OQtxU2tqdPUMtJ-NmYjX3zIn3daN6gaFpy
    nXtQUotN80c0tP-wrep4WvsQMYd9JoYhd4
    98t7Udtn72FQt6-00QeMy3UBN5Zzao8Uxd
    43tyUBtQo4HNtg-6JJom4jTyOnp8R92U9
    2OtOUatY50iDtD-qZzKnQBhn3gDaa4vhoW
    2OtOUatY50iDtD-qZzKnQBhn3gDaa4vhoW"""
    print(",".join(a.split("\n")))

