import type {Config} from "../Config";
import {Frame} from "../Frame";
import type {IoFunction} from "../Typealias";
import {StrUtils} from "../../../utils/StrUtils";
import {Asserts} from "../Asserts";
import {Constants} from "../Constants";
import {Flags} from "../Flags";
import {MessageBuilder} from "../Message"
import type {Codec, CodecReader, CodecWriter} from "../Codec";
import {EntityDefault} from "../entity/EntityDefault";

/**
 * 编解码器（基于 CodecReader,CodecWriter 接口编解）
 *
 * @author noear
 * @since 2.0
 */
export class CodecDefault implements Codec {
    private _config: Config;

    constructor(config: Config) {
        this._config = config;
    }

    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    write<T extends CodecWriter>(frame: Frame, targetFactory: IoFunction<number, T>): T {
        if (frame.message()) {
            //sid
            const sidB = StrUtils.strToBuf(frame.message()!.sid(), this._config.getCharset());
            //event
            const eventB = StrUtils.strToBuf(frame.message()!.event(), this._config.getCharset());
            //metaString
            const metaStringB = StrUtils.strToBuf(frame.message()!.metaString(), this._config.getCharset());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            const frameSize = 4 + 4 + sidB.byteLength + eventB.byteLength + metaStringB.byteLength + frame.message()!.dataSize() + 2 * 3;

            Asserts.assertSize("sid", sidB.byteLength, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.byteLength, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.byteLength, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.message()!.dataSize(), Constants.MAX_SIZE_DATA);

            const target = targetFactory(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.flag());

            //sid
            target.putBytes(sidB);
            target.putChar('\n'.charCodeAt(0));

            //event
            target.putBytes(eventB);
            target.putChar('\n'.charCodeAt(0));

            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n'.charCodeAt(0));

            //data
            target.putBytes(frame.message()!.data().getArray()!);

            target.flush();

            return target;
        } else {
            //length (len[int] + flag[int])
            const frameSize = 4 + 4;
            const target = targetFactory(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.flag());
            target.flush();

            return target;
        }
    }

    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    read(buffer: CodecReader): Frame | null { //=>Frame
        const frameSize = buffer.getInt();

        if (frameSize > (buffer.remaining() + 4)) {
            return null;
        }

        const flag = buffer.getInt();

        if (frameSize == 8) {
            //len[int] + flag[int]
            return new Frame(Flags.of(flag), null);
        } else {

            const metaBufSize = Math.min(Constants.MAX_SIZE_META_STRING, buffer.remaining());

            //1.解码 sid and event
            const buf = new ArrayBuffer(metaBufSize);

            //sid
            const sid = this.decodeString(buffer, buf, Constants.MAX_SIZE_SID);

            //event
            const event = this.decodeString(buffer, buf, Constants.MAX_SIZE_EVENT);

            //metaString
            const metaString = this.decodeString(buffer, buf, Constants.MAX_SIZE_META_STRING);

            //2.解码 body
            const dataRealSize = frameSize - buffer.position();
            let data: ArrayBuffer;
            if (dataRealSize > Constants.MAX_SIZE_DATA) {
                //超界了，空读。必须读，不然协议流会坏掉
                data = new ArrayBuffer(Constants.MAX_SIZE_DATA);
                buffer.getBytes(data, 0, Constants.MAX_SIZE_DATA);
                for (let i = dataRealSize - Constants.MAX_SIZE_DATA; i > 0; i--) {
                    buffer.getByte();
                }
            } else {
                data = new ArrayBuffer(dataRealSize);
                if (dataRealSize > 0) {
                    buffer.getBytes(data, 0, dataRealSize);
                }
            }

            //先 data , 后 metaString (避免 data 时修改元信息)
            const message = new MessageBuilder()
                .flag(Flags.of(flag))
                .sid(sid)
                .event(event)
                .entity(new EntityDefault().dataSet(data).metaStringSet(metaString))
                .build();

            return new Frame(message.flag(), message);
        }
    }

    protected decodeString(reader: CodecReader, buf: ArrayBuffer, maxLen: number): string {
        const bufView = new DataView(buf);
        let bufViewIdx = 0;

        while (true) {
            const c = reader.getByte();

            if (c == 0 && reader.peekByte() == 10) { //x0a:'\n'
                reader.skipBytes(1);
                break;
            }

            if (maxLen > 0 && maxLen <= bufViewIdx) {
                //超界了，空读。必须读，不然协议流会坏掉
            } else {
                bufView.setInt8(bufViewIdx, c);
                bufViewIdx++;
            }
        }


        if (bufViewIdx < 1) {
            return "";
        }

        //这里要加个长度控制
        return StrUtils.bufToStr(buf, 0, bufViewIdx, this._config.getCharset());
    }
}