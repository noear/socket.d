"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.CodecByteBuffer = void 0;
const Frame_1 = require("./Frame");
const StrUtils_1 = require("../../utils/StrUtils");
const Asserts_1 = require("./Asserts");
const Constants_1 = require("./Constants");
const Message_1 = require("./Message");
const Entity_1 = require("./Entity");
/**
 * 编解码器（基于 CodecReader,CodecWriter 接口编解）
 *
 * @author noear
 * @since 2.0
 */
class CodecByteBuffer {
    constructor(config) {
        this._config = config;
    }
    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    write(frame, targetFactory) {
        if (frame.message()) {
            //sid
            const sidB = StrUtils_1.StrUtils.strToBuf(frame.message().sid(), this._config.getCharset());
            //event
            const eventB = StrUtils_1.StrUtils.strToBuf(frame.message().event(), this._config.getCharset());
            //metaString
            const metaStringB = StrUtils_1.StrUtils.strToBuf(frame.message().metaString(), this._config.getCharset());
            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            const frameSize = 4 + 4 + sidB.byteLength + eventB.byteLength + metaStringB.byteLength + frame.message().dataSize() + 2 * 3;
            Asserts_1.Asserts.assertSize("sid", sidB.byteLength, Constants_1.Constants.MAX_SIZE_SID);
            Asserts_1.Asserts.assertSize("event", eventB.byteLength, Constants_1.Constants.MAX_SIZE_EVENT);
            Asserts_1.Asserts.assertSize("metaString", metaStringB.byteLength, Constants_1.Constants.MAX_SIZE_META_STRING);
            Asserts_1.Asserts.assertSize("data", frame.message().dataSize(), Constants_1.Constants.MAX_SIZE_DATA);
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
            target.putBytes(frame.message().data().getArray());
            target.flush();
            return target;
        }
        else {
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
    read(buffer) {
        const frameSize = buffer.getInt();
        if (frameSize > (buffer.remaining() + 4)) {
            return null;
        }
        const flag = buffer.getInt();
        if (frameSize == 8) {
            //len[int] + flag[int]
            return new Frame_1.Frame(Constants_1.Flags.of(flag), null);
        }
        else {
            const metaBufSize = Math.min(Constants_1.Constants.MAX_SIZE_META_STRING, buffer.remaining());
            //1.解码 sid and event
            const buf = new ArrayBuffer(metaBufSize);
            //sid
            const sid = this.decodeString(buffer, buf, Constants_1.Constants.MAX_SIZE_SID);
            //event
            const event = this.decodeString(buffer, buf, Constants_1.Constants.MAX_SIZE_EVENT);
            //metaString
            const metaString = this.decodeString(buffer, buf, Constants_1.Constants.MAX_SIZE_META_STRING);
            //2.解码 body
            const dataRealSize = frameSize - buffer.position();
            let data;
            if (dataRealSize > Constants_1.Constants.MAX_SIZE_DATA) {
                //超界了，空读。必须读，不然协议流会坏掉
                data = new ArrayBuffer(Constants_1.Constants.MAX_SIZE_DATA);
                buffer.getBytes(data, 0, Constants_1.Constants.MAX_SIZE_DATA);
                for (let i = dataRealSize - Constants_1.Constants.MAX_SIZE_DATA; i > 0; i--) {
                    buffer.getByte();
                }
            }
            else {
                data = new ArrayBuffer(dataRealSize);
                if (dataRealSize > 0) {
                    buffer.getBytes(data, 0, dataRealSize);
                }
            }
            //先 data , 后 metaString (避免 data 时修改元信息)
            const message = new Message_1.MessageBuilder()
                .flag(Constants_1.Flags.of(flag))
                .sid(sid)
                .event(event)
                .entity(new Entity_1.EntityDefault().dataSet(data).metaStringSet(metaString))
                .build();
            return new Frame_1.Frame(message.flag(), message);
        }
    }
    decodeString(reader, buf, maxLen) {
        const bufView = new DataView(buf);
        let bufViewIdx = 0;
        while (true) {
            const c = reader.getByte();
            if (c == 10) { //10:'\n'
                break;
            }
            if (maxLen > 0 && maxLen <= bufViewIdx) {
                //超界了，空读。必须读，不然协议流会坏掉
            }
            else {
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
        return StrUtils_1.StrUtils.bufToStr(buf, 0, bufViewIdx, this._config.getCharset());
    }
}
exports.CodecByteBuffer = CodecByteBuffer;
