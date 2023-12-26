
import {CodecUtils} from "./CodecUtils";
import {Asserts} from "./Asserts";
import {Constants, Flags} from "./Constants";
import {Frame} from "./Message";
import {Config} from "./Config";
import {BufferReader, BufferWriter} from "./Buffer";
import {IoFunction} from "./Types";

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
    read(buffer: BufferReader): Frame;

    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    write<T extends BufferWriter>(frame: Frame, factory: IoFunction<number, T>): T;
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
    write<T extends BufferWriter>(frame: Frame, factory: IoFunction<number, T>): T {
        if (frame.getMessage()) {
            //sid
            let sidB = CodecUtils.strToBuf(frame.getMessage().sid());
            //event
            let eventB = CodecUtils.strToBuf(frame.getMessage().event());
            //metaString
            let metaStringB = CodecUtils.strToBuf(frame.getMessage().metaString());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            let frameSize = 4 + 4 + sidB.length + eventB.length + metaStringB.length + frame.getMessage().dataSize() + 2 * 3;

            Asserts.assertSize("sid", sidB.length, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.length, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.length, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.getMessage().dataSize(), Constants.MAX_SIZE_DATA);

            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());

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
            target.putBytes(frame.getMessage().data());

            target.flush();

            return target;
        } else {
            //length (len[int] + flag[int])
            let frameSize = 4 + 4;
            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());
            target.flush();

            return target;
        }
    }

    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    read(buffer: BufferReader): Frame { //=>Frame
        let frameSize = buffer.getInt();

        if (frameSize > (buffer.remaining() + 4)) {
            return null;
        }

        let flag = buffer.getInt();

        if (frameSize == 8) {
            //len[int] + flag[int]
            return new Frame(Flags.of(flag), null);
        } else {

            return null;
        }
    }

    protected decodeString(reader: BufferReader, buf: object, maxLen: number): string {
        return null;
    }
}