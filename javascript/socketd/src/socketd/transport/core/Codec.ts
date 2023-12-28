
import {Asserts} from "./Asserts";
import {Constants, Flags} from "./Constants";
import {EntityDefault} from "./Entity";
import {MessageBuilder} from "./Message";
import {Config} from "./Config";
import {IoFunction} from "./Types";
import {StrUtils} from "../../utils/StrUtils";
import {Frame} from "./Frame";



/**
 * 编解码缓冲读
 *
 * @author noear
 * @since 2.0
 */
export interface CodecReader {

    /**
     * 获取 byte
     */
    getByte(): number;

    /**
     * 获取一组 byte
     */
    getBytes(dst: ArrayBuffer, offset: number, length: number);

    /**
     * 获取 int
     */
    getInt(): number;

    /**
     * 剩余长度
     */
    remaining(): number;

    /**
     * 当前位置
     */
    position(): number;
}

/**
 * 编解码缓冲写
 *
 * @author noear
 * @since 2.0
 */
export interface CodecWriter {
    /**
     * 推入一组 byte
     */
    putBytes(src: ArrayBuffer);

    /**
     * 推入 int
     */
    putInt(val: number);

    /**
     * 推入 char
     */
    putChar(val: number);

    /**
     * 冲刷
     */
    flush();
}

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 */
export interface Codec {
    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    read(buffer: CodecReader): Frame;

    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    write<T extends CodecWriter>(frame: Frame, targetFactory: IoFunction<number, T>): T;
}

/**
 * 编解码器（基于 BufferWriter,BufferReader 接口编解）
 *
 * @author noear
 * @since 2.0
 */
export class CodecByteBuffer implements Codec {
    _config: Config;

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
            let sidB = StrUtils.strToBuf(frame.message().sid(), this._config.getCharset());
            //event
            let eventB = StrUtils.strToBuf(frame.message().event(), this._config.getCharset());
            //metaString
            let metaStringB = StrUtils.strToBuf(frame.message().metaString(), this._config.getCharset());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            let frameSize = 4 + 4 + sidB.byteLength + eventB.byteLength + metaStringB.byteLength + frame.message().dataSize() + 2 * 3;

            Asserts.assertSize("sid", sidB.byteLength, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.byteLength, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.byteLength, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.message().dataSize(), Constants.MAX_SIZE_DATA);

            let target = targetFactory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.flag());

            //sid
            target.putBytes(sidB);
            target.putChar('\n');

            //event
            target.putBytes(eventB);
            target.putChar('\n');

            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n');

            //data
            target.putBytes(frame.message().data());

            target.flush();

            return target;
        } else {
            //length (len[int] + flag[int])
            let frameSize = 4 + 4;
            let target = targetFactory.apply(frameSize);

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
    read(buffer: CodecReader): Frame { //=>Frame
        let frameSize = buffer.getInt();

        if (frameSize > (buffer.remaining() + 4)) {
            return null;
        }

        let flag = buffer.getInt();

        if (frameSize == 8) {
            //len[int] + flag[int]
            return new Frame(Flags.of(flag), null);
        } else {

            let metaBufSize = Math.min(Constants.MAX_SIZE_META_STRING, buffer.remaining());

            //1.解码 sid and event
            let buf = new ArrayBuffer(metaBufSize);

            //sid
            let sid = this.decodeString(buffer, buf, Constants.MAX_SIZE_SID);

            //event
            let event = this.decodeString(buffer, buf, Constants.MAX_SIZE_EVENT);

            //metaString
            let metaString = this.decodeString(buffer, buf, Constants.MAX_SIZE_META_STRING);

            //2.解码 body
            let dataRealSize = frameSize - buffer.position();
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
            let message = new MessageBuilder()
                .flag(Flags.of(flag))
                .sid(sid)
                .event(event)
                .entity(new EntityDefault().dataSet(data).metaStringSet(metaString))
                .build();

            return new Frame(message.flag(), message);
        }
    }

    protected decodeString(reader: CodecReader, buf: ArrayBuffer, maxLen: number): string {
        let bufView = new DataView(buf);
        let bufViewIdx = 0;

        while (true) {
            let c = reader.getByte();

            if (c == 10) { //10:'\n'
                break;
            }

            if (maxLen > 0 && maxLen <= bufViewIdx) {
                //超界了，空读。必须读，不然协议流会坏掉
            } else {
                if (c != 0) { //32:' '
                    bufView.setInt8(bufViewIdx, c);
                    bufViewIdx++;
                }
            }
        }


        if (bufViewIdx < 1) {
            return "";
        }

        //这里要加个长度控制
        return StrUtils.bufToStr(buf, 0, bufViewIdx, this._config.getCharset());
    }
}