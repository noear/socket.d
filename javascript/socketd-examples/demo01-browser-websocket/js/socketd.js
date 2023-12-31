var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
define("socketd/utils/StrUtils", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.StrUtils = void 0;
    class StrUtils {
        static guid() {
            let guid = "";
            for (let i = 1; i <= 32; i++) {
                const n = Math.floor(Math.random() * 16.0).toString(16);
                guid += n;
            }
            return guid;
        }
        static strToBuf(str, charet) {
            if (!charet) {
                charet = 'utf-8';
            }
            const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
            return encoder.encode(str).buffer; // 将字符串编码成 ArrayBuffer,
        }
        static bufToStr(buf, start, length, charet) {
            if (buf.byteLength != length) {
                //取出子集
                const bufView = new DataView(buf);
                const tmp = new ArrayBuffer(length);
                const tmpView = new DataView(tmp);
                for (let i = 0; i < length; i++) {
                    tmpView.setInt8(i, bufView.getInt8(start + i));
                }
                buf = tmp;
            }
            return StrUtils.bufToStrDo(buf, charet);
        }
        static bufToStrDo(buf, charet) {
            if (!charet) {
                charet = 'utf-8';
            }
            const decoder = new TextDecoder(charet);
            return decoder.decode(buf);
        }
    }
    exports.StrUtils = StrUtils;
});
define("socketd/transport/core/Typealias", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/transport/core/Buffer", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.BlobBuffer = exports.ByteBuffer = void 0;
    class ByteBuffer {
        constructor(buf) {
            this._bufIdx = 0;
            this._buf = buf;
        }
        remaining() {
            return this.size() - this.position();
        }
        position() {
            return this._bufIdx;
        }
        size() {
            return this._buf.byteLength;
        }
        reset() {
            this._bufIdx = 0;
        }
        getBytes(length, callback) {
            let tmpSize = this.remaining();
            if (tmpSize > length) {
                tmpSize = length;
            }
            if (tmpSize <= 0) {
                return false;
            }
            let tmpEnd = this._bufIdx + tmpSize;
            let tmp = this._buf.slice(this._bufIdx, tmpEnd);
            this._bufIdx = tmpEnd;
            callback(tmp);
            return true;
        }
        getBlob() {
            return null;
        }
        getArray() {
            return this._buf;
        }
    }
    exports.ByteBuffer = ByteBuffer;
    class BlobBuffer {
        constructor(buf) {
            this._bufIdx = 0;
            this._buf = buf;
        }
        remaining() {
            return this._buf.size - this._bufIdx;
        }
        position() {
            return this._bufIdx;
        }
        size() {
            return this._buf.size;
        }
        reset() {
            this._bufIdx = 0;
        }
        getBytes(length, callback) {
            let tmpSize = this.remaining();
            if (tmpSize > length) {
                tmpSize = length;
            }
            if (tmpSize <= 0) {
                return false;
            }
            let tmpEnd = this._bufIdx + tmpSize;
            let tmp = this._buf.slice(this._bufIdx, tmpEnd);
            let tmpReader = new FileReader();
            tmpReader.onload = (event) => {
                if (event.target) {
                    //成功读取
                    callback(event.target.result);
                }
            };
            tmpReader.readAsArrayBuffer(tmp);
            this._bufIdx = tmpEnd;
            return true;
        }
        getBlob() {
            return this._buf;
        }
        getArray() {
            return null;
        }
    }
    exports.BlobBuffer = BlobBuffer;
});
define("socketd/transport/core/Constants", ["require", "exports", "socketd/transport/core/Buffer"], function (require, exports, Buffer_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.EntityMetas = exports.Flags = exports.Constants = void 0;
    /**
     * 常量
     *
     * @author noear
     * @since 2.0
     */
    exports.Constants = {
        /**
         * 默认流id（占位）
         */
        DEF_SID: "",
        /**
         * 默认事件（占位）
         */
        DEF_EVENT: "",
        /**
         * 默认元信息字符串（占位）
         */
        DEF_META_STRING: "",
        /**
         * 默认数据
         * */
        DEF_DATA: new Buffer_1.ByteBuffer(new ArrayBuffer(0)),
        /**
         * 因协议指令关闭
         */
        CLOSE1_PROTOCOL: 1,
        /**
         * 因协议非法关闭
         */
        CLOSE2_PROTOCOL_ILLEGAL: 2,
        /**
         * 因异常关闭
         */
        CLOSE3_ERROR: 3,
        /**
         * 因用户主动关闭
         */
        CLOSE4_USER: 4,
        /**
         * 流ID长度最大限制
         */
        MAX_SIZE_SID: 64,
        /**
         * 事件长度最大限制
         */
        MAX_SIZE_EVENT: 512,
        /**
         * 元信息串长度最大限制
         */
        MAX_SIZE_META_STRING: 4096,
        /**
         * 数据长度最大限制（也是分片长度最大限制）
         */
        MAX_SIZE_DATA: 1024 * 1024 * 16,
        /**
         * 分片长度最小限制
         */
        MIN_FRAGMENT_SIZE: 1024
    };
    /**
     * 标志
     *
     * @author noear
     * @since 2.0
     */
    exports.Flags = {
        /**
         * 未知
         */
        Unknown: 0,
        /**
         * 连接
         */
        Connect: 10,
        /**
         * 连接确认
         */
        Connack: 11,
        /**
         * Ping
         */
        Ping: 20,
        /**
         * Pong
         */
        Pong: 21,
        /**
         * 关闭（Udp 没有断链的概念，需要发消息）
         */
        Close: 30,
        /**
         * 告警
         */
        Alarm: 31,
        /**
         * 消息
         */
        Message: 40,
        /**
         * 请求
         */
        Request: 41,
        /**
         * 订阅
         */
        Subscribe: 42,
        /**
         * 答复
         */
        Reply: 48,
        /**
         * 答复结束（结束订阅接收）
         */
        ReplyEnd: 49,
        of: function (code) {
            switch (code) {
                case 10:
                    return this.Connect;
                case 11:
                    return this.Connack;
                case 20:
                    return this.Ping;
                case 21:
                    return this.Pong;
                case 30:
                    return this.Close;
                case 31:
                    return this.Alarm;
                case 40:
                    return this.Message;
                case 41:
                    return this.Request;
                case 42:
                    return this.Subscribe;
                case 48:
                    return this.Reply;
                case 49:
                    return this.ReplyEnd;
                default:
                    return this.Unknown;
            }
        },
        name: function (code) {
            switch (code) {
                case this.Connect:
                    return "Connect";
                case this.Connack:
                    return "Connack";
                case this.Ping:
                    return "Ping";
                case this.Pong:
                    return "Pong";
                case this.Close:
                    return "Close";
                case this.Alarm:
                    return "Alarm";
                case this.Message:
                    return "Message";
                case this.Request:
                    return "Request";
                case this.Subscribe:
                    return "Subscribe";
                case this.Reply:
                    return "Reply";
                case this.ReplyEnd:
                    return "ReplyEnd";
                default:
                    return "Unknown";
            }
        }
    };
    /**
     * 实体元信息常用名
     *
     * @author noear
     * @since 2.0
     */
    exports.EntityMetas = {
        /**
         * 框架版本号
         */
        META_SOCKETD_VERSION: "SocketD",
        /**
         * 数据长度
         */
        META_DATA_LENGTH: "Data-Length",
        /**
         * 数据类型
         */
        META_DATA_TYPE: "Data-Type",
        /**
         * 数据分片索引
         */
        META_DATA_FRAGMENT_IDX: "Data-Fragment-Idx",
        /**
         * 数据描述之文件名
         */
        META_DATA_DISPOSITION_FILENAME: "Data-Disposition-Filename",
        /**
         * 数据范围开始（相当于分页）
         */
        META_RANGE_START: "Data-Range-Start",
        /**
         * 数据范围长度
         */
        META_RANGE_SIZE: "Data-Range-Size",
    };
});
define("socketd/transport/core/Message", ["require", "exports", "socketd/transport/core/Constants"], function (require, exports, Constants_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.MessageDefault = exports.MessageBuilder = void 0;
    /**
     * 消息默认实现（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    class MessageBuilder {
        constructor() {
            this._flag = Constants_1.Flags.Unknown;
            this._sid = Constants_1.Constants.DEF_SID;
            this._event = Constants_1.Constants.DEF_EVENT;
            this._entity = null;
        }
        /**
         * 设置标记
         */
        flag(flag) {
            this._flag = flag;
            return this;
        }
        /**
         * 设置流id
         */
        sid(sid) {
            this._sid = sid;
            return this;
        }
        /**
         * 设置事件
         */
        event(event) {
            this._event = event;
            return this;
        }
        /**
         * 设置实体
         */
        entity(entity) {
            this._entity = entity;
            return this;
        }
        /**
         * 构建
         */
        build() {
            return new MessageDefault(this._flag, this._sid, this._event, this._entity);
        }
    }
    exports.MessageBuilder = MessageBuilder;
    /**
     * 消息默认实现（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    class MessageDefault {
        constructor(flag, sid, event, entity) {
            this._flag = flag;
            this._sid = sid;
            this._event = event;
            this._entity = entity;
        }
        at() {
            return this._entity.at();
        }
        /**
         * 获取标记
         */
        flag() {
            return this._flag;
        }
        /**
         * 是否为请求
         */
        isRequest() {
            return this._flag == Constants_1.Flags.Request;
        }
        /**
         * 是否为订阅
         */
        isSubscribe() {
            return this._flag == Constants_1.Flags.Subscribe;
        }
        /**
         * 是否答复结束
         * */
        isEnd() {
            return this._flag == Constants_1.Flags.ReplyEnd;
        }
        /**
         * 获取消息流Id（用于消息交互、分片）
         */
        sid() {
            return this._sid;
        }
        /**
         * 获取消息事件
         */
        event() {
            return this._event;
        }
        /**
         * 获取消息实体
         */
        entity() {
            return this._entity;
        }
        toString() {
            return "Message{" +
                "sid='" + this._sid + '\'' +
                ", event='" + this._event + '\'' +
                ", entity=" + this._entity +
                '}';
        }
        metaString() {
            return this._entity.metaString();
        }
        metaMap() {
            return this._entity.metaMap();
        }
        meta(name) {
            return this._entity.meta(name);
        }
        metaOrDefault(name, def) {
            return this._entity.metaOrDefault(name, def);
        }
        metaAsInt(name) {
            return this._entity.metaAsInt(name);
        }
        metaAsFloat(name) {
            return this._entity.metaAsFloat(name);
        }
        putMeta(name, val) {
            this._entity.putMeta(name, val);
        }
        data() {
            return this._entity.data();
        }
        dataAsReader() {
            return this._entity.dataAsReader();
        }
        dataAsString() {
            return this._entity.dataAsString();
        }
        dataSize() {
            return this._entity.dataSize();
        }
        release() {
            if (this._entity) {
                this._entity.release();
            }
        }
    }
    exports.MessageDefault = MessageDefault;
});
define("socketd/transport/core/Frame", ["require", "exports", "socketd/transport/core/Entity", "socketd/transport/core/Constants", "socketd/transport/core/Message", "socketd/SocketD"], function (require, exports, Entity_1, Constants_2, Message_1, SocketD_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Frames = exports.Frame = void 0;
    /**
     * 帧（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    class Frame {
        constructor(flag, message) {
            this._flag = flag;
            this._message = message;
        }
        /**
         * 标志（保持与 Message 的获取风格）
         * */
        flag() {
            return this._flag;
        }
        /**
         * 消息
         * */
        message() {
            return this._message;
        }
        toString() {
            return "Frame{" +
                "flag=" + Constants_2.Flags.name(this._flag) +
                ", message=" + this._message +
                '}';
        }
    }
    exports.Frame = Frame;
    /**
     * 帧工厂
     *
     * @author noear
     * @since 2.0
     * */
    class Frames {
        /**
         * 构建连接帧
         *
         * @param sid 流Id
         * @param url 连接地址
         */
        static connectFrame(sid, url) {
            const entity = new Entity_1.EntityDefault();
            //添加框架版本号
            entity.metaPut(Constants_2.EntityMetas.META_SOCKETD_VERSION, (0, SocketD_1.protocolVersion)());
            return new Frame(Constants_2.Flags.Connect, new Message_1.MessageBuilder().sid(sid).event(url).entity(entity).build());
        }
        /**
         * 构建连接确认帧
         *
         * @param connectMessage 连接消息
         */
        static connackFrame(connectMessage) {
            const entity = new Entity_1.EntityDefault();
            //添加框架版本号
            entity.metaPut(Constants_2.EntityMetas.META_SOCKETD_VERSION, (0, SocketD_1.protocolVersion)());
            return new Frame(Constants_2.Flags.Connack, new Message_1.MessageBuilder().sid(connectMessage.sid()).event(connectMessage.event()).entity(entity).build());
        }
        /**
         * 构建 ping 帧
         */
        static pingFrame() {
            return new Frame(Constants_2.Flags.Ping, null);
        }
        /**
         * 构建 pong 帧
         */
        static pongFrame() {
            return new Frame(Constants_2.Flags.Pong, null);
        }
        /**
         * 构建关闭帧（一般用不到）
         */
        static closeFrame() {
            return new Frame(Constants_2.Flags.Close, null);
        }
        /**
         * 构建告警帧（一般用不到）
         */
        static alarmFrame(from, alarm) {
            const message = new Message_1.MessageBuilder();
            if (from != null) {
                //如果有来源消息，则回传元信息
                message.sid(from.sid());
                message.event(from.event());
                message.entity(new Entity_1.StringEntity(alarm).metaStringSet(from.metaString()));
            }
            else {
                message.entity(new Entity_1.StringEntity(alarm));
            }
            return new Frame(Constants_2.Flags.Alarm, message.build());
        }
    }
    exports.Frames = Frames;
});
define("socketd/transport/core/Codec", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ArrayBufferCodecWriter = exports.ArrayBufferCodecReader = void 0;
    class ArrayBufferCodecReader {
        constructor(buf) {
            this._buf = buf;
            this._bufView = new DataView(buf);
            this._bufViewIdx = 0;
        }
        getByte() {
            if (this._bufViewIdx >= this._buf.byteLength) {
                return -1;
            }
            const tmp = this._bufView.getInt8(this._bufViewIdx);
            this._bufViewIdx += 1;
            return tmp;
        }
        getBytes(dst, offset, length) {
            const tmp = new DataView(dst);
            const tmpEndIdx = offset + length;
            for (let i = offset; i < tmpEndIdx; i++) {
                if (this._bufViewIdx >= this._buf.byteLength) {
                    //读完了
                    break;
                }
                tmp.setInt8(i, this._bufView.getInt8(this._bufViewIdx));
                this._bufViewIdx++;
            }
        }
        getInt() {
            if (this._bufViewIdx >= this._buf.byteLength) {
                return -1;
            }
            const tmp = this._bufView.getInt32(this._bufViewIdx);
            this._bufViewIdx += 4;
            return tmp;
        }
        remaining() {
            return this._buf.byteLength - this._bufViewIdx;
        }
        position() {
            return this._bufViewIdx;
        }
        size() {
            return this._buf.byteLength;
        }
        reset() {
            this._bufViewIdx = 0;
        }
    }
    exports.ArrayBufferCodecReader = ArrayBufferCodecReader;
    class ArrayBufferCodecWriter {
        constructor(n) {
            this._buf = new ArrayBuffer(n);
            this._bufView = new DataView(this._buf);
            this._bufViewIdx = 0;
        }
        putBytes(src) {
            const tmp = new DataView(src);
            const len = tmp.byteLength;
            for (let i = 0; i < len; i++) {
                this._bufView.setInt8(this._bufViewIdx, tmp.getInt8(i));
                this._bufViewIdx += 1;
            }
        }
        putInt(val) {
            this._bufView.setInt32(this._bufViewIdx, val);
            this._bufViewIdx += 4;
        }
        putChar(val) {
            this._bufView.setInt16(this._bufViewIdx, val);
            this._bufViewIdx += 2;
        }
        flush() {
        }
        getBuffer() {
            return this._buf;
        }
    }
    exports.ArrayBufferCodecWriter = ArrayBufferCodecWriter;
});
define("socketd/exception/SocketdException", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SocketdTimeoutException = exports.SocketdSizeLimitException = exports.SocketdConnectionException = exports.SocketdCodecException = exports.SocketdChannelException = exports.SocketdAlarmException = exports.SocketdException = void 0;
    /**
     * 异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdException extends Error {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdException = SocketdException;
    /**
     * 告警异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdAlarmException extends SocketdException {
        constructor(from) {
            super(from.entity().dataAsString());
            this._from = from;
        }
        getFrom() {
            return this._from;
        }
    }
    exports.SocketdAlarmException = SocketdAlarmException;
    /**
     * 通道异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdChannelException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdChannelException = SocketdChannelException;
    /**
     * 编码异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdCodecException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdCodecException = SocketdCodecException;
    /**
     * 连接异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdConnectionException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdConnectionException = SocketdConnectionException;
    /**
     * 大小限制异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdSizeLimitException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdSizeLimitException = SocketdSizeLimitException;
    /**
     * 超时异常
     *
     * @author noear
     * @since 2.0
     */
    class SocketdTimeoutException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdTimeoutException = SocketdTimeoutException;
});
define("socketd/transport/core/Entity", ["require", "exports", "socketd/utils/StrUtils", "socketd/transport/core/Codec", "socketd/transport/core/Constants", "socketd/transport/core/Buffer", "socketd/exception/SocketdException"], function (require, exports, StrUtils_1, Codec_1, Constants_3, Buffer_2, SocketdException_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FileEntity = exports.StringEntity = exports.EntityDefault = void 0;
    /**
     * 实体默认实现
     *
     * @author noear
     * @since 2.0
     */
    class EntityDefault {
        constructor() {
            this._metaMap = null;
            this._data = Constants_3.Constants.DEF_DATA;
            this._dataAsReader = null;
        }
        /**
         * At
         * */
        at() {
            return this.meta("@");
        }
        /**
         * 设置元信息字符串
         * */
        metaStringSet(metaString) {
            this._metaMap = new URLSearchParams(metaString);
            return this;
        }
        /**
         * 放置元信息字典
         *
         * @param map 元信息字典
         */
        metaMapPut(map) {
            if (map instanceof URLSearchParams) {
                const tmp = map;
                tmp.forEach((val, key, p) => {
                    this.metaMap().set(key, val);
                });
            }
            else {
                for (const name of map.prototype) {
                    this.metaMap().set(name, map[name]);
                }
            }
            return this;
        }
        /**
         * 放置元信息
         *
         * @param name 名字
         * @param val  值
         */
        metaPut(name, val) {
            this.metaMap().set(name, val);
            return this;
        }
        /**
         * 获取元信息字符串（queryString style）
         */
        metaString() {
            return this.metaMap().toString();
        }
        /**
         * 获取元信息字典
         */
        metaMap() {
            if (this._metaMap == null) {
                this._metaMap = new URLSearchParams();
            }
            return this._metaMap;
        }
        /**
         * 获取元信息
         *
         * @param name 名字
         */
        meta(name) {
            return this.metaMap().get(name);
        }
        /**
         * 获取元信息或默认值
         *
         * @param name 名字
         * @param def  默认值
         */
        metaOrDefault(name, def) {
            const val = this.meta(name);
            if (val) {
                return val;
            }
            else {
                return def;
            }
        }
        /**
         * 获取元信息并转为 int
         */
        metaAsInt(name) {
            return parseInt(this.metaOrDefault(name, '0'));
        }
        /**
         * 获取元信息并转为 float
         */
        metaAsFloat(name) {
            return parseFloat(this.metaOrDefault(name, '0'));
        }
        /**
         * 放置元信息
         *
         * @param name 名字
         * @param val  值
         */
        putMeta(name, val) {
            this.metaPut(name, val);
        }
        /**
         * 设置数据
         *
         * @param data 数据
         */
        dataSet(data) {
            if (data instanceof Blob) {
                this._data = new Buffer_2.BlobBuffer(data);
            }
            else {
                this._data = new Buffer_2.ByteBuffer(data);
            }
            return this;
        }
        /**
         * 获取数据（若多次复用，需要reset）
         */
        data() {
            return this._data;
        }
        dataAsReader() {
            if (this._data.getArray() == null) {
                throw new SocketdException_1.SocketdException("Blob does not support dataAsReader");
            }
            if (!this._dataAsReader) {
                this._dataAsReader = new Codec_1.ArrayBufferCodecReader(this._data.getArray());
            }
            return this._dataAsReader;
        }
        /**
         * 获取数据并转成字符串
         */
        dataAsString() {
            if (this._data.getArray() == null) {
                throw new SocketdException_1.SocketdException("Blob does not support dataAsString");
            }
            return StrUtils_1.StrUtils.bufToStrDo(this._data.getArray(), '');
        }
        /**
         * 获取数据长度
         */
        dataSize() {
            return this._data.size();
        }
        /**
         * 释放资源
         */
        release() {
        }
        toString() {
            return "Entity{" +
                "meta='" + this.metaString() + '\'' +
                ", data=byte[" + this.dataSize() + ']' + //避免内容太大，影响打印
                '}';
        }
    }
    exports.EntityDefault = EntityDefault;
    /**
     * 字符串实体
     *
     * @author noear
     * @since 2.0
     */
    class StringEntity extends EntityDefault {
        constructor(data) {
            super();
            const dataBuf = StrUtils_1.StrUtils.strToBuf(data);
            this.dataSet(dataBuf);
        }
    }
    exports.StringEntity = StringEntity;
    class FileEntity extends EntityDefault {
        constructor(file) {
            super();
            this.dataSet(file);
            this.metaPut(Constants_3.EntityMetas.META_DATA_DISPOSITION_FILENAME, file.name);
        }
    }
    exports.FileEntity = FileEntity;
});
define("socketd/transport/core/Asserts", ["require", "exports", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, Constants_4, SocketdException_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Asserts = void 0;
    /**
     * 断言
     *
     * @author noear
     * @since 2.0
     */
    class Asserts {
        /**
         * 断言关闭
         */
        static assertClosed(channel) {
            if (channel != null && channel.isClosed() > 0) {
                throw new SocketdException_2.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
            }
        }
        /**
         * 断言关闭
         */
        static assertClosedByUser(channel) {
            if (channel != null && channel.isClosed() == Constants_4.Constants.CLOSE4_USER) {
                throw new SocketdException_2.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
            }
        }
        /**
         * 断言 null
         */
        static assertNull(name, val) {
            if (val == null) {
                throw new Error("The argument cannot be null: " + name);
            }
        }
        /**
         * 断言 empty
         */
        static assertEmpty(name, val) {
            if (!val) {
                throw new Error("The argument cannot be empty: " + name);
            }
        }
        /**
         * 断言 size
         */
        static assertSize(name, size, limitSize) {
            if (size > limitSize) {
                const message = `This message ${name} size is out of limit ${limitSize} (${size})`;
                throw new SocketdException_2.SocketdSizeLimitException(message);
            }
        }
    }
    exports.Asserts = Asserts;
});
define("socketd/transport/core/Stream", ["require", "exports", "socketd/exception/SocketdException", "socketd/transport/core/Asserts"], function (require, exports, SocketdException_3, Asserts_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.StreamMangerDefault = exports.StreamSubscribe = exports.StreamRequest = exports.StreamBase = void 0;
    /**
     * 流基类
     *
     * @author noear
     * @since 2.0
     */
    class StreamBase {
        constructor(sid, isSingle, timeout) {
            this._sid = sid;
            this._isSingle = isSingle;
            this._timeout = timeout;
        }
        sid() {
            return this._sid;
        }
        isSingle() {
            return this._isSingle;
        }
        timeout() {
            return this._timeout;
        }
        /**
         * 保险开始（避免永久没有回调，造成内存不能释放）
         *
         * @param streamManger  流管理器
         * @param streamTimeout 流超时
         */
        insuranceStart(streamManger, streamTimeout) {
            if (this._insuranceFuture) {
                return;
            }
            this._insuranceFuture = setTimeout(() => {
                streamManger.removeStream(this.sid());
                this.onError(new SocketdException_3.SocketdTimeoutException("The stream response timeout, sid=" + this.sid()));
            }, streamTimeout);
        }
        /**
         * 保险取消息
         */
        insuranceCancel() {
            if (this._insuranceFuture) {
                clearTimeout(this._insuranceFuture);
            }
        }
        /**
         * 异常时
         *
         * @param error 异常
         */
        onError(error) {
            if (this._doOnError != null) {
                this._doOnError(error);
            }
        }
        thenError(onError) {
            this._doOnError = onError;
            return this;
        }
    }
    exports.StreamBase = StreamBase;
    /**
     * 请求流
     *
     * @author noear
     * @since 2.0
     */
    class StreamRequest extends StreamBase {
        constructor(sid, timeout, future) {
            super(sid, false, timeout);
            this._future = future;
            this._isDone = false;
        }
        isDone() {
            return this._isDone;
        }
        onAccept(reply, channel) {
            this._isDone = true;
            try {
                this._future(reply);
            }
            catch (e) {
                channel.onError(e);
            }
        }
    }
    exports.StreamRequest = StreamRequest;
    /**
     * 订阅流
     *
     * @author noear
     * @since 2.0
     */
    class StreamSubscribe extends StreamBase {
        constructor(sid, timeout, future) {
            super(sid, false, timeout);
            this._future = future;
        }
        isDone() {
            return false;
        }
        onAccept(reply, channel) {
            try {
                this._future(reply);
            }
            catch (e) {
                channel.onError(e);
            }
        }
    }
    exports.StreamSubscribe = StreamSubscribe;
    class StreamMangerDefault {
        constructor(config) {
            this._config = config;
            this._streamMap = new Map();
        }
        /**
         * 获取流接收器
         *
         * @param sid 流Id
         */
        getStream(sid) {
            return this._streamMap.get(sid);
        }
        /**
         * 添加流接收器
         *
         * @param sid    流Id
         * @param stream 流
         */
        addStream(sid, stream) {
            Asserts_1.Asserts.assertNull("stream", stream);
            this._streamMap.set(sid, stream);
            //增加流超时处理（做为后备保险）
            const streamTimeout = stream.timeout() > 0 ? stream.timeout() : this._config.getStreamTimeout();
            if (streamTimeout > 0) {
                stream.insuranceStart(this, streamTimeout);
            }
        }
        /**
         * 移除流接收器
         *
         * @param sid 流Id
         */
        removeStream(sid) {
            const stream = this.getStream(sid);
            if (stream) {
                this._streamMap.delete(sid);
                stream.insuranceCancel();
                console.debug(`${this._config.getRoleName()} stream removed, sid=${sid}`);
            }
        }
    }
    exports.StreamMangerDefault = StreamMangerDefault;
});
define("socketd/transport/core/IdGenerator", ["require", "exports", "socketd/utils/StrUtils"], function (require, exports, StrUtils_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.GuidGenerator = void 0;
    class GuidGenerator {
        generate() {
            return StrUtils_2.StrUtils.guid();
        }
    }
    exports.GuidGenerator = GuidGenerator;
});
define("socketd/transport/core/FragmentHolder", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FragmentHolder = void 0;
    class FragmentHolder {
        constructor(index, message) {
            this._index = index;
            this._message = message;
        }
        /**
         * 获取顺序位
         */
        getIndex() {
            return this._index;
        }
        /**
         * 获取分片帧
         */
        getMessage() {
            return this._message;
        }
    }
    exports.FragmentHolder = FragmentHolder;
});
define("socketd/transport/core/FragmentAggregator", ["require", "exports", "socketd/transport/core/Message", "socketd/transport/core/Entity", "socketd/transport/core/Frame", "socketd/transport/core/FragmentHolder", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, Message_2, Entity_2, Frame_1, FragmentHolder_1, Constants_5, SocketdException_4) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FragmentAggregatorDefault = void 0;
    /**
     * 分片聚合器
     *
     * @author noear
     * @since 2.0
     */
    class FragmentAggregatorDefault {
        constructor(main) {
            //分片列表
            this._fragmentHolders = new Array();
            this._main = main;
            const dataLengthStr = main.meta(Constants_5.EntityMetas.META_DATA_LENGTH);
            if (!dataLengthStr) {
                throw new SocketdException_4.SocketdCodecException("Missing '" + Constants_5.EntityMetas.META_DATA_LENGTH + "' meta, event=" + main.event());
            }
            this._dataLength = parseInt(dataLengthStr);
        }
        /**
         * 获取消息流Id（用于消息交互、分片）
         */
        getSid() {
            return this._main.sid();
        }
        /**
         * 数据流大小
         */
        getDataStreamSize() {
            return this._dataStreamSize;
        }
        /**
         * 数据总长度
         */
        getDataLength() {
            return this._dataLength;
        }
        /**
         * 添加帧
         */
        add(index, message) {
            //添加分片
            this._fragmentHolders.push(new FragmentHolder_1.FragmentHolder(index, message));
            //添加计数
            this._dataStreamSize = this._dataStreamSize + message.dataSize();
        }
        /**
         * 获取聚合后的帧
         */
        get() {
            //排序
            this._fragmentHolders.sort((f1, f2) => {
                if (f1.getIndex() == f2.getIndex()) {
                    return 0;
                }
                else if (f1.getIndex() > f2.getIndex()) {
                    return 1;
                }
                else {
                    return -1;
                }
            });
            //创建聚合流
            const dataBuffer = new ArrayBuffer(this._dataLength);
            const dataBufferView = new DataView(dataBuffer);
            let dataBufferViewIdx = 0;
            //添加分片数据
            for (const fh of this._fragmentHolders) {
                const tmp = new DataView(fh.getMessage().data().getArray());
                for (let i = 0; i < fh.getMessage().data().size(); i++) {
                    dataBufferView.setInt8(dataBufferViewIdx, tmp.getInt8(i));
                    dataBufferViewIdx++;
                }
            }
            //返回
            return new Frame_1.Frame(this._main.flag(), new Message_2.MessageBuilder()
                .flag(this._main.flag())
                .sid(this._main.sid())
                .event(this._main.event())
                .entity(new Entity_2.EntityDefault().metaMapPut(this._main.metaMap()).dataSet(dataBuffer))
                .build());
        }
    }
    exports.FragmentAggregatorDefault = FragmentAggregatorDefault;
});
define("socketd/transport/core/FragmentHandler", ["require", "exports", "socketd/transport/core/Entity", "socketd/transport/core/Constants", "socketd/transport/core/FragmentAggregator"], function (require, exports, Entity_3, Constants_6, FragmentAggregator_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FragmentHandlerDefault = void 0;
    /**
     * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
     *
     * @author noear
     * @since 2.0
     */
    class FragmentHandlerDefault {
        /**
         * 拆割分片
         *
         * @param channel       通道
         * @param message       总包消息
         * @param consumer 分片消费
         */
        spliFragment(channel, message, consumer) {
            if (message.dataSize() > channel.getConfig().getFragmentSize()) {
                let fragmentIndex = 0;
                this.spliFragmentDo(fragmentIndex, channel, message, consumer);
            }
            else {
                if (message.data().getBlob() == null) {
                    consumer(message);
                }
                else {
                    message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
                        consumer(new Entity_3.EntityDefault().dataSet(dataBuffer).metaMapPut(message.metaMap()));
                    });
                }
            }
        }
        spliFragmentDo(fragmentIndex, channel, message, consumer) {
            //获取分片
            fragmentIndex++;
            message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
                const fragmentEntity = new Entity_3.EntityDefault().dataSet(dataBuffer);
                if (fragmentIndex == 1) {
                    fragmentEntity.metaMapPut(message.metaMap());
                }
                fragmentEntity.metaPut(Constants_6.EntityMetas.META_DATA_FRAGMENT_IDX, fragmentIndex.toString());
                consumer(fragmentEntity);
                this.spliFragmentDo(fragmentIndex, channel, message, consumer);
            });
        }
        /**
         * 聚合分片
         *
         * @param channel       通道
         * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
         * @param message       分片消息
         */
        aggrFragment(channel, fragmentIndex, message) {
            let aggregator = channel.getAttachment(message.sid());
            if (!aggregator) {
                aggregator = new FragmentAggregator_1.FragmentAggregatorDefault(message);
                channel.putAttachment(aggregator.getSid(), aggregator);
            }
            aggregator.add(fragmentIndex, message);
            if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
                //长度不够，等下一个分片包
                return null;
            }
            else {
                //重置为聚合帖
                channel.putAttachment(message.sid(), null);
                return aggregator.get();
            }
        }
        aggrEnable() {
            return true;
        }
    }
    exports.FragmentHandlerDefault = FragmentHandlerDefault;
});
define("socketd/transport/core/CodecByteBuffer", ["require", "exports", "socketd/transport/core/Frame", "socketd/utils/StrUtils", "socketd/transport/core/Asserts", "socketd/transport/core/Constants", "socketd/transport/core/Message", "socketd/transport/core/Entity"], function (require, exports, Frame_2, StrUtils_3, Asserts_2, Constants_7, Message_3, Entity_4) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.CodecByteBuffer = void 0;
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
                const sidB = StrUtils_3.StrUtils.strToBuf(frame.message().sid(), this._config.getCharset());
                //event
                const eventB = StrUtils_3.StrUtils.strToBuf(frame.message().event(), this._config.getCharset());
                //metaString
                const metaStringB = StrUtils_3.StrUtils.strToBuf(frame.message().metaString(), this._config.getCharset());
                //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
                const frameSize = 4 + 4 + sidB.byteLength + eventB.byteLength + metaStringB.byteLength + frame.message().dataSize() + 2 * 3;
                Asserts_2.Asserts.assertSize("sid", sidB.byteLength, Constants_7.Constants.MAX_SIZE_SID);
                Asserts_2.Asserts.assertSize("event", eventB.byteLength, Constants_7.Constants.MAX_SIZE_EVENT);
                Asserts_2.Asserts.assertSize("metaString", metaStringB.byteLength, Constants_7.Constants.MAX_SIZE_META_STRING);
                Asserts_2.Asserts.assertSize("data", frame.message().dataSize(), Constants_7.Constants.MAX_SIZE_DATA);
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
                return new Frame_2.Frame(Constants_7.Flags.of(flag), null);
            }
            else {
                const metaBufSize = Math.min(Constants_7.Constants.MAX_SIZE_META_STRING, buffer.remaining());
                //1.解码 sid and event
                const buf = new ArrayBuffer(metaBufSize);
                //sid
                const sid = this.decodeString(buffer, buf, Constants_7.Constants.MAX_SIZE_SID);
                //event
                const event = this.decodeString(buffer, buf, Constants_7.Constants.MAX_SIZE_EVENT);
                //metaString
                const metaString = this.decodeString(buffer, buf, Constants_7.Constants.MAX_SIZE_META_STRING);
                //2.解码 body
                const dataRealSize = frameSize - buffer.position();
                let data;
                if (dataRealSize > Constants_7.Constants.MAX_SIZE_DATA) {
                    //超界了，空读。必须读，不然协议流会坏掉
                    data = new ArrayBuffer(Constants_7.Constants.MAX_SIZE_DATA);
                    buffer.getBytes(data, 0, Constants_7.Constants.MAX_SIZE_DATA);
                    for (let i = dataRealSize - Constants_7.Constants.MAX_SIZE_DATA; i > 0; i--) {
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
                const message = new Message_3.MessageBuilder()
                    .flag(Constants_7.Flags.of(flag))
                    .sid(sid)
                    .event(event)
                    .entity(new Entity_4.EntityDefault().dataSet(data).metaStringSet(metaString))
                    .build();
                return new Frame_2.Frame(message.flag(), message);
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
            return StrUtils_3.StrUtils.bufToStr(buf, 0, bufViewIdx, this._config.getCharset());
        }
    }
    exports.CodecByteBuffer = CodecByteBuffer;
});
define("socketd/transport/core/Config", ["require", "exports", "socketd/transport/core/Stream", "socketd/transport/core/IdGenerator", "socketd/transport/core/FragmentHandler", "socketd/transport/core/Constants", "socketd/transport/core/Asserts", "socketd/transport/core/CodecByteBuffer"], function (require, exports, Stream_1, IdGenerator_1, FragmentHandler_1, Constants_8, Asserts_3, CodecByteBuffer_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ConfigBase = void 0;
    class ConfigBase {
        constructor(clientMode) {
            this._clientMode = clientMode;
            this._streamManger = new Stream_1.StreamMangerDefault(this);
            this._codec = new CodecByteBuffer_1.CodecByteBuffer(this);
            this._charset = "utf-8";
            this._idGenerator = new IdGenerator_1.GuidGenerator();
            this._fragmentHandler = new FragmentHandler_1.FragmentHandlerDefault();
            this._fragmentSize = Constants_8.Constants.MAX_SIZE_DATA;
            this._coreThreads = 2;
            this._maxThreads = this._coreThreads * 4;
            this._readBufferSize = 512;
            this._writeBufferSize = 512;
            this._idleTimeout = 0; //默认不关（提供用户特殊场景选择）
            this._requestTimeout = 10000; //10秒（默认与连接超时同）
            this._streamTimeout = 1000 * 60 * 60 * 2; //2小时 //避免永不回调时，不能释放
            this._maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
        }
        /**
         * 是否客户端模式
         */
        clientMode() {
            return this._clientMode;
        }
        /**
         * 获取流管理器
         */
        getStreamManger() {
            return this._streamManger;
        }
        /**
         * 获取角色名
         * */
        getRoleName() {
            return this.clientMode() ? "Client" : "Server";
        }
        /**
         * 获取字符集
         */
        getCharset() {
            return this._charset;
        }
        /**
         * 配置字符集
         */
        charset(charset) {
            this._charset = charset;
            return this;
        }
        /**
         * 获取编解码器
         */
        getCodec() {
            return this._codec;
        }
        /**
         * 获取标识生成器
         */
        getIdGenerator() {
            return this._idGenerator;
        }
        /**
         * 配置标识生成器
         */
        idGenerator(idGenerator) {
            Asserts_3.Asserts.assertNull("idGenerator", idGenerator);
            this._idGenerator = idGenerator;
            return this;
        }
        /**
         * 获取分片处理
         */
        getFragmentHandler() {
            return this._fragmentHandler;
        }
        /**
         * 配置分片处理
         */
        fragmentHandler(fragmentHandler) {
            Asserts_3.Asserts.assertNull("fragmentHandler", fragmentHandler);
            this._fragmentHandler = fragmentHandler;
            return this;
        }
        /**
         * 获取分片大小
         */
        getFragmentSize() {
            return this._fragmentSize;
        }
        /**
         * 配置分片大小
         */
        fragmentSize(fragmentSize) {
            if (fragmentSize > Constants_8.Constants.MAX_SIZE_DATA) {
                throw new Error("The parameter fragmentSize cannot > 16m");
            }
            if (fragmentSize < Constants_8.Constants.MIN_FRAGMENT_SIZE) {
                throw new Error("The parameter fragmentSize cannot < 1k");
            }
            this._fragmentSize = fragmentSize;
            return this;
        }
        /**
         * 获取核心线程数
         */
        getCoreThreads() {
            return this._coreThreads;
        }
        /**
         * 配置核心线程数
         */
        coreThreads(coreThreads) {
            this._coreThreads = coreThreads;
            this._maxThreads = coreThreads * 4;
            return this;
        }
        /**
         * 获取最大线程数
         */
        getMaxThreads() {
            return this._maxThreads;
        }
        /**
         * 配置最大线程数
         */
        maxThreads(maxThreads) {
            this._maxThreads = maxThreads;
            return this;
        }
        /**
         * 获取读缓冲大小
         */
        getReadBufferSize() {
            return this._readBufferSize;
        }
        /**
         * 配置读缓冲大小
         */
        readBufferSize(readBufferSize) {
            this._readBufferSize = readBufferSize;
            return this;
        }
        /**
         * 获取写缓冲大小
         */
        getWriteBufferSize() {
            return this._writeBufferSize;
        }
        /**
         * 配置写缓冲大小
         */
        writeBufferSize(writeBufferSize) {
            this._writeBufferSize = writeBufferSize;
            return this;
        }
        /**
         * 配置连接空闲超时
         */
        getIdleTimeout() {
            return this._idleTimeout;
        }
        /**
         * 配置连接空闲超时
         */
        idleTimeout(idleTimeout) {
            this._idleTimeout = idleTimeout;
            return this;
        }
        /**
         * 配置请求默认超时
         */
        getRequestTimeout() {
            return this._requestTimeout;
        }
        /**
         * 配置请求默认超时
         */
        requestTimeout(requestTimeout) {
            this._requestTimeout = requestTimeout;
            return this;
        }
        /**
         * 获取消息流超时（单位：毫秒）
         * */
        getStreamTimeout() {
            return this._streamTimeout;
        }
        /**
         * 配置消息流超时（单位：毫秒）
         * */
        streamTimeout(streamTimeout) {
            this._streamTimeout = streamTimeout;
            return this;
        }
        /**
         * 获取允许最大UDP包大小
         */
        getMaxUdpSize() {
            return this._maxUdpSize;
        }
        /**
         * 配置允许最大UDP包大小
         */
        maxUdpSize(maxUdpSize) {
            this._maxUdpSize = maxUdpSize;
            return this;
        }
        /**
         * 生成 id
         * */
        generateId() {
            return this._idGenerator.generate();
        }
    }
    exports.ConfigBase = ConfigBase;
});
define("socketd/transport/core/Handshake", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/transport/core/Channel", ["require", "exports", "socketd/transport/core/Frame"], function (require, exports, Frame_3) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ChannelBase = void 0;
    class ChannelBase {
        constructor(config) {
            this._config = config;
            this._attachments = new Map();
            this._isClosed = 0;
        }
        getAttachment(name) {
            return this._attachments.get(name);
        }
        putAttachment(name, val) {
            if (val == null) {
                this._attachments.delete(name);
            }
            else {
                this._attachments.set(name, val);
            }
        }
        isClosed() {
            return this._isClosed;
        }
        close(code) {
            this._isClosed = code;
            this._attachments.clear();
        }
        getConfig() {
            return this._config;
        }
        setHandshake(handshake) {
            this._handshake = handshake;
        }
        getHandshake() {
            return this._handshake;
        }
        sendConnect(url) {
            this.send(Frame_3.Frames.connectFrame(this.getConfig().getIdGenerator().generate(), url), null);
        }
        sendConnack(connectMessage) {
            this.send(Frame_3.Frames.connackFrame(connectMessage), null);
        }
        sendPing() {
            this.send(Frame_3.Frames.pingFrame(), null);
        }
        sendPong() {
            this.send(Frame_3.Frames.pongFrame(), null);
        }
        sendClose() {
            this.send(Frame_3.Frames.closeFrame(), null);
        }
        sendAlarm(from, alarm) {
            this.send(Frame_3.Frames.alarmFrame(from, alarm), null);
        }
    }
    exports.ChannelBase = ChannelBase;
});
define("socketd/transport/client/ClientSession", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/transport/core/Session", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SessionBase = void 0;
    /**
     * 会话基类
     *
     * @author noear
     */
    class SessionBase {
        constructor(channel) {
            this._channel = channel;
            this._sessionId = this.generateId();
        }
        sessionId() {
            return this._sessionId;
        }
        name() {
            return this.param("@");
        }
        attrMap() {
            if (this._attrMap == null) {
                this._attrMap = new Map();
            }
            return this._attrMap;
        }
        attrHas(name) {
            if (this._attrMap == null) {
                return false;
            }
            return this._attrMap.has(name);
        }
        attr(name) {
            if (this._attrMap == null) {
                return null;
            }
            return this._attrMap.get(name);
        }
        attrOrDefault(name, def) {
            const tmp = this.attr(name);
            return tmp ? tmp : def;
        }
        attrPut(name, val) {
            this.attrMap().set(name, val);
        }
        generateId() {
            return this._channel.getConfig().getIdGenerator().generate();
        }
    }
    exports.SessionBase = SessionBase;
});
define("socketd/transport/core/RouteSelector", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.RouteSelectorDefault = void 0;
    /**
     * 路径映射器默认实现（哈希）
     *
     * @author noear
     * @since 2.0
     */
    class RouteSelectorDefault {
        constructor() {
            this._inner = new Map();
        }
        /**
         * 选择
         *
         * @param route 路由
         */
        select(route) {
            return this._inner.get(route);
        }
        /**
         * 放置
         *
         * @param route  路由
         * @param target 目标
         */
        put(route, target) {
            this._inner.set(route, target);
        }
        /**
         * 移除
         *
         * @param route 路由
         */
        remove(route) {
            this._inner.delete(route);
        }
        /**
         * 数量
         */
        size() {
            return this._inner.size;
        }
    }
    exports.RouteSelectorDefault = RouteSelectorDefault;
});
define("socketd/transport/core/Listener", ["require", "exports", "socketd/transport/core/RouteSelector"], function (require, exports, RouteSelector_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.PipelineListener = exports.PathListener = exports.EventListener = exports.SimpleListener = void 0;
    /**
     * 简单监听器（一般用于占位）
     *
     * @author noear
     * @since 2.0
     */
    class SimpleListener {
        onOpen(session) {
        }
        onMessage(session, message) {
        }
        onClose(session) {
        }
        onError(session, error) {
        }
    }
    exports.SimpleListener = SimpleListener;
    /**
     * 事件监听器（根据消息事件路由）
     *
     * @author noear
     * @since 2.0
     */
    class EventListener {
        constructor(routeSelector) {
            if (routeSelector) {
                this._eventRouteSelector = routeSelector;
            }
            else {
                this._eventRouteSelector = new RouteSelector_1.RouteSelectorDefault();
            }
        }
        doOn(event, consumer) {
            this._eventRouteSelector.put(event, consumer);
            return this;
        }
        doOnOpen(consumer) {
            this._doOnOpen = consumer;
            return this;
        }
        doOnMessage(consumer) {
            this._doOnMessage = consumer;
            return this;
        }
        doOnClose(consumer) {
            this._doOnClose = consumer;
            return this;
        }
        doOnError(consumer) {
            this._doOnError = consumer;
            return this;
        }
        onOpen(session) {
            if (this._doOnOpen) {
                this._doOnOpen(session);
            }
        }
        onMessage(session, message) {
            if (this._doOnMessage) {
                this._doOnMessage(session, message);
            }
            const consumer = this._eventRouteSelector.select(message.event());
            if (consumer) {
                consumer(session, message);
            }
        }
        onClose(session) {
            if (this._doOnClose) {
                this._doOnClose(session);
            }
        }
        onError(session, error) {
            if (this._doOnError) {
                this._doOnError(session, error);
            }
        }
    }
    exports.EventListener = EventListener;
    /**
     * 路径监听器（根据握手地址路由，一般用于服务端）
     *
     * @author noear
     * @since 2.0
     */
    class PathListener {
        constructor(routeSelector) {
            if (routeSelector) {
                this._pathRouteSelector = routeSelector;
            }
            else {
                this._pathRouteSelector = new RouteSelector_1.RouteSelectorDefault();
            }
        }
        /**
         * 路由
         */
        of(path, listener) {
            this._pathRouteSelector.put(path, listener);
            return this;
        }
        /**
         * 数量（二级监听器的数据）
         */
        size() {
            return this._pathRouteSelector.size();
        }
        onOpen(session) {
            const l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onOpen(session);
            }
        }
        onMessage(session, message) {
            const l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onMessage(session, message);
            }
        }
        onClose(session) {
            const l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onClose(session);
            }
        }
        onError(session, error) {
            const l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onError(session, error);
            }
        }
    }
    exports.PathListener = PathListener;
    /**
     * 管道监听器
     *
     * @author noear
     * @since 2.0
     */
    class PipelineListener {
        constructor() {
            this._deque = new Array();
        }
        /**
         * 前一个
         */
        prev(listener) {
            this._deque.unshift(listener);
            return this;
        }
        /**
         * 后一个
         */
        next(listener) {
            this._deque.push(listener);
            return this;
        }
        /**
         * 数量（二级监听器的数据）
         * */
        size() {
            return this._deque.length;
        }
        /**
         * 打开时
         *
         * @param session 会话
         */
        onOpen(session) {
            for (const listener of this._deque) {
                listener.onOpen(session);
            }
        }
        /**
         * 收到消息时
         *
         * @param session 会话
         * @param message 消息
         */
        onMessage(session, message) {
            for (const listener of this._deque) {
                listener.onMessage(session, message);
            }
        }
        /**
         * 关闭时
         *
         * @param session 会话
         */
        onClose(session) {
            for (const listener of this._deque) {
                listener.onClose(session);
            }
        }
        /**
         * 出错时
         *
         * @param session 会话
         * @param error   错误信息
         */
        onError(session, error) {
            for (const listener of this._deque) {
                listener.onError(session, error);
            }
        }
    }
    exports.PipelineListener = PipelineListener;
});
define("socketd/transport/client/ClientConfig", ["require", "exports", "socketd/transport/core/Config"], function (require, exports, Config_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientConfig = void 0;
    class ClientConfig extends Config_1.ConfigBase {
        constructor(url) {
            super(true);
            //支持 sd: 开头的架构
            if (url.startsWith("sd:")) {
                url = url.substring(3);
            }
            this._url = url;
            this._uri = new URL(url);
            this._port = parseInt(this._uri.port);
            this._schema = this._uri.protocol;
            this._linkUrl = "sd:" + url;
            if (this._port < 0) {
                this._port = 8602;
            }
            this._connectTimeout = 10000;
            this._heartbeatInterval = 20000;
            this._autoReconnect = true;
        }
        /**
         * 获取通讯架构（tcp, ws, udp）
         */
        getSchema() {
            return this._schema;
        }
        /**
         * 获取连接地址
         */
        getUrl() {
            return this._url;
        }
        /**
         * 获取连接地址
         */
        getUri() {
            return this._uri;
        }
        /**
         * 获取链接地址
         */
        getLinkUrl() {
            return this._linkUrl;
        }
        /**
         * 获取连接主机
         */
        getHost() {
            return this._uri.host;
        }
        /**
         * 获取连接端口
         */
        getPort() {
            return this._port;
        }
        /**
         * 获取心跳间隔（单位毫秒）
         */
        getHeartbeatInterval() {
            return this._heartbeatInterval;
        }
        /**
         * 配置心跳间隔（单位毫秒）
         */
        heartbeatInterval(heartbeatInterval) {
            this._heartbeatInterval = heartbeatInterval;
            return this;
        }
        /**
         * 获取连接超时（单位毫秒）
         */
        getConnectTimeout() {
            return this._connectTimeout;
        }
        /**
         * 配置连接超时（单位毫秒）
         */
        connectTimeout(connectTimeout) {
            this._connectTimeout = connectTimeout;
            return this;
        }
        /**
         * 获取是否自动重链
         */
        isAutoReconnect() {
            return this._autoReconnect;
        }
        /**
         * 配置是否自动重链
         */
        autoReconnect(autoReconnect) {
            this._autoReconnect = autoReconnect;
            return this;
        }
        idleTimeout(idleTimeout) {
            if (this._autoReconnect == false) {
                //自动重链下，禁用 idleTimeout
                this._idleTimeout = (idleTimeout);
                return this;
            }
            else {
                this._idleTimeout = (0);
                return this;
            }
        }
        toString() {
            return "ClientConfig{" +
                "schema='" + this._schema + '\'' +
                ", charset=" + this._charset +
                ", url='" + this._url + '\'' +
                ", heartbeatInterval=" + this._heartbeatInterval +
                ", connectTimeout=" + this._connectTimeout +
                ", idleTimeout=" + this._idleTimeout +
                ", requestTimeout=" + this._requestTimeout +
                ", readBufferSize=" + this._readBufferSize +
                ", writeBufferSize=" + this._writeBufferSize +
                ", autoReconnect=" + this._autoReconnect +
                ", maxUdpSize=" + this._maxUdpSize +
                '}';
        }
    }
    exports.ClientConfig = ClientConfig;
});
define("socketd/transport/core/HandshakeDefault", ["require", "exports", "socketd/transport/core/Constants"], function (require, exports, Constants_9) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.HandshakeDefault = void 0;
    class HandshakeDefault {
        constructor(source) {
            this._source = source;
            this._url = new URL(source.event());
            this._version = source.meta(Constants_9.EntityMetas.META_SOCKETD_VERSION);
            this._paramMap = new Map();
            for (const [k, v] of this._url.searchParams) {
                this._paramMap.set(k, v);
            }
        }
        getSource() {
            return this._source;
        }
        param(name) {
            return this._paramMap.get(name);
        }
        paramMap() {
            return this._paramMap;
        }
        paramOrDefault(name, def) {
            const tmp = this.param(name);
            return tmp ? tmp : def;
        }
        paramPut(name, value) {
            this._paramMap.set(name, value);
        }
        uri() {
            return this._url;
        }
        version() {
            return this._version;
        }
    }
    exports.HandshakeDefault = HandshakeDefault;
});
define("socketd/transport/core/Processor", ["require", "exports", "socketd/transport/core/Listener", "socketd/transport/core/Constants", "socketd/exception/SocketdException", "socketd/transport/core/HandshakeDefault"], function (require, exports, Listener_1, Constants_10, SocketdException_5, HandshakeDefault_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ProcessorDefault = void 0;
    class ProcessorDefault {
        constructor() {
            this._listener = new Listener_1.SimpleListener();
        }
        setListener(listener) {
            if (listener != null) {
                this._listener = listener;
            }
        }
        onReceive(channel, frame) {
            if (channel.getConfig().clientMode()) {
                console.debug("C-REV:" + frame);
            }
            else {
                console.debug("S-REV:" + frame);
            }
            if (frame.flag() == Constants_10.Flags.Connect) {
                channel.setHandshake(new HandshakeDefault_1.HandshakeDefault(frame.message()));
                channel.onOpenFuture((r, err) => {
                    if (r && channel.isValid()) {
                        //如果还有效，则发送链接确认
                        try {
                            channel.sendConnack(frame.message()); //->Connack
                        }
                        catch (err) {
                            this.onError(channel, err);
                        }
                    }
                });
                this.onOpen(channel);
            }
            else if (frame.flag() == Constants_10.Flags.Connack) {
                //if client
                channel.setHandshake(new HandshakeDefault_1.HandshakeDefault(frame.message()));
                this.onOpen(channel);
            }
            else {
                if (channel.getHandshake() == null) {
                    channel.close(Constants_10.Constants.CLOSE1_PROTOCOL);
                    if (frame.flag() == Constants_10.Flags.Close) {
                        //说明握手失败了
                        throw new SocketdException_5.SocketdConnectionException("Connection request was rejected");
                    }
                    console.warn(`${channel.getConfig().getRoleName()} channel handshake is null, sessionId=${channel.getSession().sessionId()}`);
                    return;
                }
                try {
                    switch (frame.flag()) {
                        case Constants_10.Flags.Ping: {
                            channel.sendPong();
                            break;
                        }
                        case Constants_10.Flags.Pong: {
                            break;
                        }
                        case Constants_10.Flags.Close: {
                            //关闭通道
                            channel.close(Constants_10.Constants.CLOSE1_PROTOCOL);
                            this.onCloseInternal(channel);
                            break;
                        }
                        case Constants_10.Flags.Alarm: {
                            //结束流，并异常通知
                            const exception = new SocketdException_5.SocketdAlarmException(frame.message());
                            const stream = channel.getConfig().getStreamManger().getStream(frame.message().sid());
                            if (stream == null) {
                                this.onError(channel, exception);
                            }
                            else {
                                channel.getConfig().getStreamManger().removeStream(frame.message().sid());
                                stream.onError(exception);
                            }
                            break;
                        }
                        case Constants_10.Flags.Message:
                        case Constants_10.Flags.Request:
                        case Constants_10.Flags.Subscribe: {
                            this.onReceiveDo(channel, frame, false);
                            break;
                        }
                        case Constants_10.Flags.Reply:
                        case Constants_10.Flags.ReplyEnd: {
                            this.onReceiveDo(channel, frame, true);
                            break;
                        }
                        default: {
                            channel.close(Constants_10.Constants.CLOSE2_PROTOCOL_ILLEGAL);
                            this.onCloseInternal(channel);
                        }
                    }
                }
                catch (e) {
                    this.onError(channel, e);
                }
            }
        }
        onReceiveDo(channel, frame, isReply) {
            //如果启用了聚合!
            if (channel.getConfig().getFragmentHandler().aggrEnable()) {
                //尝试聚合分片处理
                const fragmentIdxStr = frame.message().meta(Constants_10.EntityMetas.META_DATA_FRAGMENT_IDX);
                if (fragmentIdxStr != null) {
                    //解析分片索引
                    const index = parseInt(fragmentIdxStr);
                    const frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, index, frame.message());
                    if (frameNew == null) {
                        return;
                    }
                    else {
                        frame = frameNew;
                    }
                }
            }
            //执行接收处理
            if (isReply) {
                channel.retrieve(frame);
            }
            else {
                this.onMessage(channel, frame.message());
            }
        }
        onOpen(channel) {
            try {
                this._listener.onOpen(channel.getSession());
                channel.doOpenFuture(true, null);
            }
            catch (e) {
                console.warn(`${channel.getConfig().getRoleName()} channel listener onOpen error`, e);
                channel.doOpenFuture(false, e);
            }
        }
        onMessage(channel, message) {
            try {
                this._listener.onMessage(channel.getSession(), message);
            }
            catch (e) {
                console.warn(`${channel.getConfig().getRoleName()} channel listener onMessage error`, e);
                this.onError(channel, e);
            }
        }
        onClose(channel) {
            if (channel.isClosed() == 0) {
                this.onCloseInternal(channel);
            }
        }
        onCloseInternal(channel) {
            this._listener.onClose(channel.getSession());
        }
        onError(channel, error) {
            this._listener.onError(channel.getSession(), error);
        }
    }
    exports.ProcessorDefault = ProcessorDefault;
});
define("socketd/transport/core/ChannelAssistant", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/transport/client/ClientConnector", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientConnectorBase = void 0;
    /**
     * 客户端连接器基类
     *
     * @author noear
     * @since 2.0
     */
    class ClientConnectorBase {
        constructor(client) {
            this._client = client;
        }
        heartbeatHandler() {
            return this._client.getHeartbeatHandler();
        }
        heartbeatInterval() {
            return this._client.getHeartbeatInterval();
        }
        autoReconnect() {
            return this._client.getConfig().isAutoReconnect();
        }
    }
    exports.ClientConnectorBase = ClientConnectorBase;
});
define("socketd/transport/core/HeartbeatHandler", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.HeartbeatHandlerDefault = void 0;
    class HeartbeatHandlerDefault {
        constructor(heartbeatHandler) {
            this._heartbeatHandler = heartbeatHandler;
        }
        heartbeat(session) {
            if (this._heartbeatHandler == null) {
                session.sendPing();
            }
            else {
                this._heartbeatHandler(session);
            }
        }
    }
    exports.HeartbeatHandlerDefault = HeartbeatHandlerDefault;
});
define("socketd/utils/RunUtils", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.RunUtils = void 0;
    class RunUtils {
        static runAndTry(fun) {
            try {
                fun();
            }
            catch (e) {
            }
        }
    }
    exports.RunUtils = RunUtils;
});
define("socketd/transport/client/ClientChannel", ["require", "exports", "socketd/transport/core/Channel", "socketd/transport/core/HeartbeatHandler", "socketd/transport/core/Constants", "socketd/transport/core/Asserts", "socketd/exception/SocketdException", "socketd/utils/RunUtils"], function (require, exports, Channel_1, HeartbeatHandler_1, Constants_11, Asserts_4, SocketdException_6, RunUtils_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientChannel = void 0;
    /**
     * 客户端通道
     *
     * @author noear
     * @since 2.0
     */
    class ClientChannel extends Channel_1.ChannelBase {
        constructor(real, connector) {
            super(real.getConfig());
            this._connector = connector;
            this._real = real;
            if (connector.heartbeatHandler() == null) {
                this._heartbeatHandler = new HeartbeatHandler_1.HeartbeatHandlerDefault(null);
            }
            else {
                this._heartbeatHandler = new HeartbeatHandler_1.HeartbeatHandlerDefault(connector.heartbeatHandler());
            }
            this.initHeartbeat();
        }
        /**
         * 初始化心跳（关闭后，手动重链时也会用到）
         */
        initHeartbeat() {
            if (this._heartbeatScheduledFuture) {
                clearInterval(this._heartbeatScheduledFuture);
            }
            if (this._connector.autoReconnect()) {
                this._heartbeatScheduledFuture = setInterval(() => {
                    try {
                        this.heartbeatHandle();
                    }
                    catch (e) {
                        console.warn("Client channel heartbeat error", e);
                    }
                }, this._connector.heartbeatInterval());
            }
        }
        /**
         * 心跳处理
         */
        heartbeatHandle() {
            return __awaiter(this, void 0, void 0, function* () {
                if (this._real != null) {
                    //说明握手未成
                    if (this._real.getHandshake() == null) {
                        return;
                    }
                    //手动关闭
                    if (this._real.isClosed() == Constants_11.Constants.CLOSE4_USER) {
                        console.debug(`Client channel is closed (pause heartbeat), sessionId=${this.getSession().sessionId()}`);
                        return;
                    }
                }
                try {
                    yield this.prepareCheck();
                    this._heartbeatHandler.heartbeat(this.getSession());
                }
                catch (e) {
                    if (e instanceof SocketdException_6.SocketdException) {
                        throw e;
                    }
                    if (this._connector.autoReconnect()) {
                        this._real.close(Constants_11.Constants.CLOSE3_ERROR);
                        this._real = null;
                    }
                    throw new SocketdException_6.SocketdChannelException(e);
                }
            });
        }
        /**
         * 预备检测
         *
         * @return 是否为新链接
         */
        prepareCheck() {
            return __awaiter(this, void 0, void 0, function* () {
                if (this._real == null || this._real.isValid() == false) {
                    this._real = yield this._connector.connect();
                    return true;
                }
                else {
                    return false;
                }
            });
        }
        /**
         * 是否有效
         */
        isValid() {
            if (this._real == null) {
                return false;
            }
            else {
                return this._real.isValid();
            }
        }
        /**
         * 是否已关闭
         */
        isClosed() {
            if (this._real == null) {
                return 0;
            }
            else {
                return this._real.isClosed();
            }
        }
        /**
         * 发送
         *
         * @param frame  帧
         * @param stream 流（没有则为 null）
         */
        send(frame, stream) {
            return __awaiter(this, void 0, void 0, function* () {
                Asserts_4.Asserts.assertClosedByUser(this._real);
                try {
                    yield this.prepareCheck();
                    this._real.send(frame, stream);
                }
                catch (e) {
                    if (this._connector.autoReconnect()) {
                        this._real.close(Constants_11.Constants.CLOSE3_ERROR);
                        this._real = null;
                    }
                    throw e;
                }
            });
        }
        retrieve(frame) {
            this._real.retrieve(frame);
        }
        reconnect() {
            this.initHeartbeat();
            this.prepareCheck();
        }
        onError(error) {
            this._real.onError(error);
        }
        close(code) {
            RunUtils_1.RunUtils.runAndTry(() => clearInterval(this._heartbeatScheduledFuture));
            RunUtils_1.RunUtils.runAndTry(() => this._connector.close());
            if (this._real) {
                RunUtils_1.RunUtils.runAndTry(() => this._real.close(code));
            }
            super.close(code);
        }
        getSession() {
            return this._real.getSession();
        }
    }
    exports.ClientChannel = ClientChannel;
});
define("socketd/transport/core/SessionDefault", ["require", "exports", "socketd/transport/core/Session", "socketd/transport/core/Message", "socketd/transport/core/Frame", "socketd/transport/core/Constants", "socketd/transport/core/Stream"], function (require, exports, Session_1, Message_4, Frame_4, Constants_12, Stream_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SessionDefault = void 0;
    /**
     * 会话默认实现
     *
     * @author noear
     * @since 2.0
     */
    class SessionDefault extends Session_1.SessionBase {
        constructor(channel) {
            super(channel);
        }
        isValid() {
            return this._channel.isValid();
        }
        handshake() {
            return this._channel.getHandshake();
        }
        /**
         * 获取握手参数
         *
         * @param name 名字
         */
        param(name) {
            return this.handshake().param(name);
        }
        /**
         * 获取握手参数或默认值
         *
         * @param name 名字
         * @param def  默认值
         */
        paramOrDefault(name, def) {
            return this.handshake().paramOrDefault(name, def);
        }
        /**
         * 获取路径
         */
        path() {
            if (this._pathNew == null) {
                return this.handshake().uri().pathname;
            }
            else {
                return this._pathNew;
            }
        }
        /**
         * 设置新路径
         */
        pathNew(pathNew) {
            this._pathNew = pathNew;
        }
        /**
         * 手动重连（一般是自动）
         */
        reconnect() {
            this._channel.reconnect();
        }
        /**
         * 手动发送 Ping（一般是自动）
         */
        sendPing() {
            this._channel.sendPing();
        }
        sendAlarm(from, alarm) {
            this._channel.sendAlarm(from, alarm);
        }
        /**
         * 发送
         */
        send(event, content) {
            const message = new Message_4.MessageBuilder()
                .sid(this.generateId())
                .event(event)
                .entity(content)
                .build();
            this._channel.send(new Frame_4.Frame(Constants_12.Flags.Message, message), null);
        }
        /**
         * 发送并请求（限为一次答复；指定超时）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout 超时
         */
        sendAndRequest(event, content, consumer, timeout) {
            //异步，用 streamTimeout
            const message = new Message_4.MessageBuilder()
                .sid(this.generateId())
                .event(event)
                .entity(content)
                .build();
            const stream = new Stream_2.StreamRequest(message.sid(), timeout, consumer);
            this._channel.send(new Frame_4.Frame(Constants_12.Flags.Request, message), stream);
            return stream;
        }
        /**
         * 发送并订阅（答复结束之前，不限答复次数）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout 超时
         */
        sendAndSubscribe(event, content, consumer, timeout) {
            const message = new Message_4.MessageBuilder()
                .sid(this.generateId())
                .event(event)
                .entity(content)
                .build();
            const stream = new Stream_2.StreamSubscribe(message.sid(), timeout, consumer);
            this._channel.send(new Frame_4.Frame(Constants_12.Flags.Subscribe, message), stream);
            return stream;
        }
        /**
         * 答复
         *
         * @param from    来源消息
         * @param content 内容
         */
        reply(from, content) {
            const message = new Message_4.MessageBuilder()
                .sid(from.sid())
                .event(from.event())
                .entity(content)
                .build();
            this._channel.send(new Frame_4.Frame(Constants_12.Flags.Reply, message), null);
        }
        /**
         * 答复并结束（即最后一次答复）
         *
         * @param from    来源消息
         * @param content 内容
         */
        replyEnd(from, content) {
            const message = new Message_4.MessageBuilder()
                .sid(from.sid())
                .event(from.event())
                .entity(content)
                .build();
            this._channel.send(new Frame_4.Frame(Constants_12.Flags.ReplyEnd, message), null);
        }
        /**
         * 关闭
         */
        close() {
            console.debug(`${this._channel.getConfig().getRoleName()} session will be closed, sessionId=${this.sessionId()}`);
            if (this._channel.isValid()) {
                try {
                    this._channel.sendClose();
                }
                catch (e) {
                    console.warn(`${this._channel.getConfig().getRoleName()} channel sendClose error`, e);
                }
            }
            this._channel.close(Constants_12.Constants.CLOSE4_USER);
        }
    }
    exports.SessionDefault = SessionDefault;
});
define("socketd/transport/client/Client", ["require", "exports", "socketd/transport/core/Processor", "socketd/transport/client/ClientChannel", "socketd/transport/core/SessionDefault"], function (require, exports, Processor_1, ClientChannel_1, SessionDefault_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientBase = void 0;
    /**
     * 客户端基类
     *
     * @author noear
     * @since 2.0
     */
    class ClientBase {
        constructor(clientConfig, assistant) {
            this._config = clientConfig;
            this._assistant = assistant;
            this._processor = new Processor_1.ProcessorDefault();
        }
        /**
         * 获取通道助理
         */
        getAssistant() {
            return this._assistant;
        }
        /**
         * 获取心跳处理
         */
        getHeartbeatHandler() {
            return this._heartbeatHandler;
        }
        /**
         * 获取心跳间隔（毫秒）
         */
        getHeartbeatInterval() {
            return this.getConfig().getHeartbeatInterval();
        }
        /**
         * 获取配置
         */
        getConfig() {
            return this._config;
        }
        /**
         * 获取处理器
         */
        getProcessor() {
            return this._processor;
        }
        /**
         * 设置心跳
         */
        heartbeatHandler(handler) {
            if (handler != null) {
                this._heartbeatHandler = handler;
            }
            return this;
        }
        /**
         * 配置
         */
        config(configHandler) {
            if (configHandler != null) {
                configHandler(this._config);
            }
            return this;
        }
        /**
         * 设置监听器
         */
        listen(listener) {
            if (listener != null) {
                this._processor.setListener(listener);
            }
            return this;
        }
        /**
         * 打开会话
         */
        open() {
            return __awaiter(this, void 0, void 0, function* () {
                const connector = this.createConnector();
                //连接
                const channel0 = yield connector.connect();
                //新建客户端通道
                const clientChannel = new ClientChannel_1.ClientChannel(channel0, connector);
                //同步握手信息
                clientChannel.setHandshake(channel0.getHandshake());
                const session = new SessionDefault_1.SessionDefault(clientChannel);
                //原始通道切换为带壳的 session
                channel0.setSession(session);
                console.info(`Socket.D client successfully connected: {link=${this.getConfig().getLinkUrl()}`);
                return session;
            });
        }
    }
    exports.ClientBase = ClientBase;
});
define("socketd/transport/client/ClientProvider", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/cluster/ClusterClientSession", ["require", "exports", "socketd/utils/StrUtils", "socketd/exception/SocketdException", "socketd/transport/client/ClientChannel", "socketd/utils/RunUtils"], function (require, exports, StrUtils_4, SocketdException_7, ClientChannel_2, RunUtils_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClusterClientSession = void 0;
    /**
     * 集群客户端会话
     *
     * @author noear
     * @since 2.1
     */
    class ClusterClientSession {
        constructor(sessions) {
            this._sessionSet = sessions;
            this._sessionId = StrUtils_4.StrUtils.guid();
            this._sessionRoundCounter = 0;
        }
        /**
         * 获取所有会话
         */
        getSessionAll() {
            return this._sessionSet;
        }
        /**
         * 获取一个会话（轮询负栽均衡）
         */
        getSessionOne() {
            if (this._sessionSet.length == 0) {
                //没有会话
                throw new SocketdException_7.SocketdException("No session!");
            }
            else if (this._sessionSet.length == 1) {
                //只有一个就不管了
                return this._sessionSet[0];
            }
            else {
                //查找可用的会话
                const sessions = new ClientChannel_2.ClientChannel[this._sessionSet.length];
                let sessionsSize = 0;
                for (const s of this._sessionSet) {
                    if (s.isValid()) {
                        sessions[sessionsSize] = s;
                        sessionsSize++;
                    }
                }
                if (sessionsSize == 0) {
                    //没有可用的会话
                    throw new SocketdException_7.SocketdException("No session is available!");
                }
                if (sessionsSize == 1) {
                    return sessions[0];
                }
                //论询处理
                const counter = this._sessionRoundCounter++;
                const idx = counter % sessionsSize;
                if (counter > 999999999) {
                    this._sessionRoundCounter = 0;
                }
                return sessions[idx];
            }
        }
        isValid() {
            for (const session of this._sessionSet) {
                if (session.isValid()) {
                    return true;
                }
            }
            return false;
        }
        sessionId() {
            return this._sessionId;
        }
        reconnect() {
            for (const session of this._sessionSet) {
                if (session.isValid() == false) {
                    session.reconnect();
                }
            }
        }
        /**
         * 发送
         *
         * @param event   事件
         * @param content 内容
         */
        send(event, content) {
            const sender = this.getSessionOne();
            sender.send(event, content);
        }
        /**
         * 发送并请求（限为一次答复；指定回调）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout  超时
         */
        sendAndRequest(event, content, consumer, timeout) {
            const sender = this.getSessionOne();
            return sender.sendAndRequest(event, content, consumer, timeout);
        }
        /**
         * 发送并订阅（答复结束之前，不限答复次数）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout  超时
         */
        sendAndSubscribe(event, content, consumer, timeout) {
            const sender = this.getSessionOne();
            return sender.sendAndSubscribe(event, content, consumer, timeout);
        }
        /**
         * 关闭
         */
        close() {
            for (const session of this._sessionSet) {
                //某个关闭出错，不影响别的关闭
                RunUtils_2.RunUtils.runAndTry(session.close);
            }
        }
    }
    exports.ClusterClientSession = ClusterClientSession;
});
define("socketd/cluster/ClusterClient", ["require", "exports", "socketd/cluster/ClusterClientSession", "socketd/SocketD"], function (require, exports, ClusterClientSession_1, SocketD_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClusterClient = void 0;
    /**
     * 集群客户端
     *
     * @author noear
     */
    class ClusterClient {
        constructor(serverUrls) {
            this._serverUrls = serverUrls;
        }
        heartbeatHandler(heartbeatHandler) {
            this._heartbeatHandler = heartbeatHandler;
            return this;
        }
        /**
         * 配置
         */
        config(configHandler) {
            this._configHandler = configHandler;
            return this;
        }
        /**
         * 监听
         */
        listen(listener) {
            this._listener = listener;
            return this;
        }
        /**
         * 打开
         */
        open() {
            return __awaiter(this, void 0, void 0, function* () {
                const sessionList = new ClusterClient[this._serverUrls.length];
                for (const urls of this._serverUrls) {
                    for (let url of urls.split(",")) {
                        url = url.trim();
                        if (!url) {
                            continue;
                        }
                        const client = (0, SocketD_2.createClient)(url);
                        if (this._listener != null) {
                            client.listen(this._listener);
                        }
                        if (this._configHandler != null) {
                            client.config(this._configHandler);
                        }
                        if (this._heartbeatHandler != null) {
                            client.heartbeatHandler(this._heartbeatHandler);
                        }
                        const session = yield client.open();
                        sessionList.add(session);
                    }
                }
                return new ClusterClientSession_1.ClusterClientSession(sessionList);
            });
        }
    }
    exports.ClusterClient = ClusterClient;
});
define("socketd_websocket/WsChannelAssistant", ["require", "exports", "socketd/transport/core/Codec"], function (require, exports, Codec_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.WsChannelAssistant = void 0;
    class WsChannelAssistant {
        constructor(config) {
            this._config = config;
        }
        read(buffer) {
            return this._config.getCodec().read(new Codec_2.ArrayBufferCodecReader(buffer));
        }
        write(target, frame) {
            let tmp = this._config.getCodec()
                .write(frame, n => new Codec_2.ArrayBufferCodecWriter(n));
            target.send(tmp.getBuffer());
        }
        isValid(target) {
            return target.readyState === WebSocket.OPEN;
        }
        close(target) {
            target.close();
        }
        getRemoteAddress(target) {
            throw new Error("Method not implemented.");
        }
        getLocalAddress(target) {
            throw new Error("Method not implemented.");
        }
    }
    exports.WsChannelAssistant = WsChannelAssistant;
});
define("socketd/transport/client/ClientHandshakeResult", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientHandshakeResult = void 0;
    /**
     * 客户端握手结果
     *
     * @author noear
     * @since 2.0
     */
    class ClientHandshakeResult {
        constructor(channel, throwable) {
            this._channel = channel;
            this._throwable = throwable;
        }
        getChannel() {
            return this._channel;
        }
        getThrowable() {
            return this._throwable;
        }
    }
    exports.ClientHandshakeResult = ClientHandshakeResult;
});
define("socketd/transport/core/ChannelSupporter", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/transport/core/ChannelDefault", ["require", "exports", "socketd/transport/core/Frame", "socketd/transport/core/Message", "socketd/transport/core/Constants", "socketd/transport/core/Channel", "socketd/transport/core/SessionDefault"], function (require, exports, Frame_5, Message_5, Constants_13, Channel_2, SessionDefault_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ChannelDefault = void 0;
    class ChannelDefault extends Channel_2.ChannelBase {
        constructor(source, supporter) {
            super(supporter.getConfig());
            this._source = source;
            this._processor = supporter.getProcessor();
            this._assistant = supporter.getAssistant();
            this._streamManger = supporter.getConfig().getStreamManger();
        }
        onOpenFuture(future) {
            this._onOpenFuture = future;
        }
        doOpenFuture(r, e) {
            if (this._onOpenFuture) {
                this._onOpenFuture(r, e);
            }
        }
        isValid() {
            return this.isClosed() == 0 && this._assistant.isValid(this._source);
        }
        config() {
            return this._config;
        }
        sendPing() {
            this.send(Frame_5.Frames.pingFrame(), null);
        }
        sendPong() {
            this.send(Frame_5.Frames.pongFrame(), null);
        }
        send(frame, stream) {
            if (this.getConfig().clientMode()) {
                console.debug("C-SEN:" + frame);
            }
            else {
                console.debug("S-SEN:" + frame);
            }
            if (frame.message()) {
                const message = frame.message();
                //注册流接收器
                if (stream != null) {
                    this._streamManger.addStream(message.sid(), stream);
                }
                //如果有实体（尝试分片）
                if (message.entity() != null) {
                    //确保用完自动关闭
                    if (message.dataSize() > this.getConfig().getFragmentSize()) {
                        message.putMeta(Constants_13.EntityMetas.META_DATA_LENGTH, message.dataSize().toString());
                    }
                    this.getConfig().getFragmentHandler().spliFragment(this, message, fragmentEntity => {
                        //主要是 sid 和 entity
                        const fragmentFrame = new Frame_5.Frame(frame.flag(), new Message_5.MessageBuilder()
                            .flag(frame.flag())
                            .sid(message.sid())
                            .event(message.event())
                            .entity(fragmentEntity)
                            .build());
                        this._assistant.write(this._source, fragmentFrame);
                    });
                    return;
                }
            }
            //不满足分片条件，直接发
            this._assistant.write(this._source, frame);
        }
        retrieve(frame) {
            const stream = this._streamManger.getStream(frame.message().sid());
            if (stream != null) {
                if (stream.isSingle() || frame.flag() == Constants_13.Flags.ReplyEnd) {
                    //如果是单收或者答复结束，则移除流接收器
                    this._streamManger.removeStream(frame.message().sid());
                }
                if (stream.isSingle()) {
                    //单收时，内部已经是异步机制
                    stream.onAccept(frame.message(), this);
                }
                else {
                    //改为异步处理，避免卡死Io线程
                    stream.onAccept(frame.message(), this);
                }
            }
            else {
                console.debug(`${this.getConfig().getRoleName()} stream not found, sid=${frame.message().sid()}, sessionId=${this.getSession().sessionId()}`);
            }
        }
        reconnect() {
            //由 ClientChannel 实现
        }
        onError(error) {
            this._processor.onError(this, error);
        }
        getSession() {
            if (this._session == null) {
                this._session = new SessionDefault_2.SessionDefault(this);
            }
            return this._session;
        }
        setSession(session) {
            this._session = session;
        }
        close(code) {
            console.debug(`${this.getConfig().getRoleName()} channel will be closed, sessionId=${this.getSession().sessionId()}`);
            try {
                super.close(code);
                this._assistant.close(this._source);
            }
            catch (e) {
                console.warn(`${this.getConfig().getRoleName()} channel close error, sessionId=${this.getSession().sessionId()}`, e);
            }
        }
    }
    exports.ChannelDefault = ChannelDefault;
});
define("socketd_websocket/impl/WebSocketClientImpl", ["require", "exports", "socketd/transport/client/ClientHandshakeResult", "socketd/transport/core/ChannelDefault", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, ClientHandshakeResult_1, ChannelDefault_1, Constants_14, SocketdException_8) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.WebSocketClientImpl = void 0;
    class WebSocketClientImpl {
        constructor(url, client, handshakeFuture) {
            this._real = new WebSocket(url);
            this._client = client;
            this._channel = new ChannelDefault_1.ChannelDefault(this._real, client);
            this._handshakeFuture = handshakeFuture;
            this._real.binaryType = "arraybuffer";
            this._real.onopen = this.onOpen.bind(this);
            this._real.onmessage = this.onMessage.bind(this);
            this._real.onclose = this.onClose.bind(this);
            this._real.onerror = this.onError.bind(this);
        }
        onOpen(e) {
            try {
                this._channel.sendConnect(this._client.getConfig().getUrl());
            }
            catch (err) {
                console.warn("Client channel sendConnect error", err);
            }
        }
        onMessage(e) {
            if (e.data instanceof String) {
                console.warn("Client channel unsupported onMessage(String test)");
            }
            else {
                try {
                    let frame = this._client.getAssistant().read(e.data);
                    if (frame != null) {
                        if (frame.flag() == Constants_14.Flags.Connack) {
                            this._channel.onOpenFuture((r, err) => {
                                if (err == null) {
                                    this._handshakeFuture(new ClientHandshakeResult_1.ClientHandshakeResult(this._channel, null));
                                }
                                else {
                                    this._handshakeFuture(new ClientHandshakeResult_1.ClientHandshakeResult(this._channel, err));
                                }
                            });
                        }
                        this._client.getProcessor().onReceive(this._channel, frame);
                    }
                }
                catch (e) {
                    if (e instanceof SocketdException_8.SocketdConnectionException) {
                        this._handshakeFuture(new ClientHandshakeResult_1.ClientHandshakeResult(this._channel, e));
                    }
                    console.warn("WebSocket client onMessage error", e);
                }
            }
        }
        onClose(e) {
            this._client.getProcessor().onClose(this._channel);
        }
        onError(e) {
            this._client.getProcessor().onError(this._channel, e);
        }
        close() {
            this._real.close();
        }
    }
    exports.WebSocketClientImpl = WebSocketClientImpl;
});
define("socketd_websocket/WsClientConnector", ["require", "exports", "socketd/transport/client/ClientConnector", "socketd_websocket/impl/WebSocketClientImpl"], function (require, exports, ClientConnector_1, WebSocketClientImpl_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.WsClientConnector = void 0;
    class WsClientConnector extends ClientConnector_1.ClientConnectorBase {
        constructor(client) {
            super(client);
        }
        connect() {
            //关闭之前的资源
            this.close();
            //处理自定义架构的影响（重连时，新建实例比原生重链接口靠谱）
            let url = this._client.getConfig().getUrl();
            return new Promise((resolve, reject) => {
                this._real = new WebSocketClientImpl_1.WebSocketClientImpl(url, this._client, (r) => {
                    if (r.getThrowable()) {
                        reject(r.getThrowable());
                    }
                    else {
                        resolve(r.getChannel());
                    }
                });
            });
        }
        close() {
            if (this._real) {
                this._real.close();
            }
        }
    }
    exports.WsClientConnector = WsClientConnector;
});
define("socketd_websocket/WsClient", ["require", "exports", "socketd/transport/client/Client", "socketd_websocket/WsChannelAssistant", "socketd_websocket/WsClientConnector"], function (require, exports, Client_1, WsChannelAssistant_1, WsClientConnector_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.WsClient = void 0;
    class WsClient extends Client_1.ClientBase {
        constructor(clientConfig) {
            super(clientConfig, new WsChannelAssistant_1.WsChannelAssistant(clientConfig));
        }
        createConnector() {
            return new WsClientConnector_1.WsClientConnector(this);
        }
    }
    exports.WsClient = WsClient;
});
define("socketd_websocket/WsClientProvider", ["require", "exports", "socketd_websocket/WsClient"], function (require, exports, WsClient_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.WsClientProvider = void 0;
    class WsClientProvider {
        schemas() {
            return ["ws", "wss", "sd:ws", "sd:wss"];
        }
        createClient(clientConfig) {
            return new WsClient_1.WsClient(clientConfig);
        }
    }
    exports.WsClientProvider = WsClientProvider;
});
define("socketd/SocketD", ["require", "exports", "socketd/transport/core/Asserts", "socketd/transport/client/ClientConfig", "socketd/cluster/ClusterClient", "socketd_websocket/WsClientProvider", "socketd/transport/core/Entity", "socketd/transport/core/Listener", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, Asserts_5, ClientConfig_1, ClusterClient_1, WsClientProvider_1, Entity_5, Listener_2, Constants_15, SocketdException_9) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Metas = exports.newPipelineListener = exports.newPathListener = exports.newEventListener = exports.newSimpleListener = exports.newEntity = exports.createClusterClient = exports.createClientOrNull = exports.createClient = exports.protocolVersion = exports.version = void 0;
    const clientProviderMap = new Map();
    //init
    (function () {
        const provider = new WsClientProvider_1.WsClientProvider();
        for (const s of provider.schemas()) {
            clientProviderMap.set(s, provider);
        }
    })();
    /**
     * 框架版本号
     */
    function version() {
        return "2.2.1";
    }
    exports.version = version;
    /**
     * 协议版本号
     */
    function protocolVersion() {
        return "1.0";
    }
    exports.protocolVersion = protocolVersion;
    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    function createClient(serverUrl) {
        const client = createClientOrNull(serverUrl);
        if (client == null) {
            throw new Error("No socketd client providers were found.");
        }
        else {
            return client;
        }
    }
    exports.createClient = createClient;
    /**
     * 创建客户端（支持 url 自动识别），如果没有则为 null
     *
     * @param serverUrl 服务器地址
     */
    function createClientOrNull(serverUrl) {
        Asserts_5.Asserts.assertNull("serverUrl", serverUrl);
        const idx = serverUrl.indexOf("://");
        if (idx < 2) {
            throw new Error("The serverUrl invalid: " + serverUrl);
        }
        const schema = serverUrl.substring(0, idx);
        const factory = clientProviderMap.get(schema);
        if (factory == null) {
            return null;
        }
        else {
            const clientConfig = new ClientConfig_1.ClientConfig(serverUrl);
            return factory.createClient(clientConfig);
        }
    }
    exports.createClientOrNull = createClientOrNull;
    /**
     * 创建集群客户端
     *
     * @param serverUrls 服务端地址
     */
    function createClusterClient(serverUrls) {
        return new ClusterClient_1.ClusterClient(serverUrls);
    }
    exports.createClusterClient = createClusterClient;
    /**
     * 创建实体
     * */
    function newEntity(data) {
        if (!data) {
            return new Entity_5.EntityDefault();
        }
        else if (data instanceof File) {
            return new Entity_5.FileEntity(data);
        }
        else if (data instanceof ArrayBuffer) {
            return new Entity_5.EntityDefault().dataSet(data);
        }
        else if (data instanceof String) {
            return new Entity_5.StringEntity(data.toString());
        }
        else if (typeof (data) === 'string') {
            return new Entity_5.StringEntity(data);
        }
        else {
            throw new SocketdException_9.SocketdException("The type is not supported: " + typeof (data));
        }
    }
    exports.newEntity = newEntity;
    /**
     * 创建简单临听器
     * */
    function newSimpleListener() {
        return new Listener_2.SimpleListener();
    }
    exports.newSimpleListener = newSimpleListener;
    /**
     * 创建事件监听器
     * */
    function newEventListener(routeSelector) {
        return new Listener_2.EventListener(routeSelector);
    }
    exports.newEventListener = newEventListener;
    /**
     * 创建路径监听器（一般用于服务端）
     * */
    function newPathListener(routeSelector) {
        return new Listener_2.PathListener(routeSelector);
    }
    exports.newPathListener = newPathListener;
    /**
     * 创建管道监听器
     * */
    function newPipelineListener() {
        return new Listener_2.PipelineListener();
    }
    exports.newPipelineListener = newPipelineListener;
    /**
     * 元信息字典
     * */
    exports.Metas = Constants_15.EntityMetas;
});
