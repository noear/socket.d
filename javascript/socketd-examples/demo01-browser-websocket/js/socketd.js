/*!
 * Socket.D v2.2.0
 * (c) 2023 noear.org and other contributors
 * Released under the Apache-2.0 License.
 */
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
                let n = Math.floor(Math.random() * 16.0).toString(16);
                guid += n;
            }
            return guid;
        }
        static strToBuf(str, charet) {
            if (!charet) {
                charet = 'utf-8';
            }
            const encoder = new TextEncoder();
            return encoder.encode(str).buffer;
        }
        static bufToStr(buf, start, length, charet) {
            if (buf.byteLength != length) {
                let bufView = new DataView(buf);
                let tmp = new ArrayBuffer(length);
                let tmpView = new DataView(tmp);
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
define("socketd/transport/core/Constants", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.EntityMetas = exports.Flags = exports.Constants = void 0;
    exports.Constants = {
        DEF_SID: "",
        DEF_EVENT: "",
        DEF_META_STRING: "",
        CLOSE1_PROTOCOL: 1,
        CLOSE2_PROTOCOL_ILLEGAL: 2,
        CLOSE3_ERROR: 3,
        CLOSE4_USER: 4,
        MAX_SIZE_SID: 64,
        MAX_SIZE_EVENT: 512,
        MAX_SIZE_META_STRING: 4096,
        MAX_SIZE_DATA: 1024 * 1024 * 16,
        MIN_FRAGMENT_SIZE: 1024
    };
    exports.Flags = {
        Unknown: 0,
        Connect: 10,
        Connack: 11,
        Ping: 20,
        Pong: 21,
        Close: 30,
        Alarm: 31,
        Message: 40,
        Request: 41,
        Subscribe: 42,
        Reply: 48,
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
    exports.EntityMetas = {
        META_SOCKETD_VERSION: "SocketD",
        META_DATA_LENGTH: "Data-Length",
        META_DATA_TYPE: "Data-Type",
        META_DATA_FRAGMENT_IDX: "Data-Fragment-Idx",
        META_DATA_DISPOSITION_FILENAME: "Data-Disposition-Filename",
        META_RANGE_START: "Data-Range-Start",
        META_RANGE_SIZE: "Data-Range-Size",
    };
});
define("socketd/transport/core/Message", ["require", "exports", "socketd/transport/core/Constants"], function (require, exports, Constants_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.MessageDefault = exports.MessageBuilder = void 0;
    class MessageBuilder {
        constructor() {
            this._flag = Constants_1.Flags.Unknown;
            this._sid = Constants_1.Constants.DEF_SID;
            this._event = Constants_1.Constants.DEF_EVENT;
            this._entity = null;
        }
        flag(flag) {
            this._flag = flag;
            return this;
        }
        sid(sid) {
            this._sid = sid;
            return this;
        }
        event(event) {
            this._event = event;
            return this;
        }
        entity(entity) {
            this._entity = entity;
            return this;
        }
        build() {
            return new MessageDefault(this._flag, this._sid, this._event, this._entity);
        }
    }
    exports.MessageBuilder = MessageBuilder;
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
        flag() {
            return this._flag;
        }
        isRequest() {
            return this._flag == Constants_1.Flags.Request;
        }
        isSubscribe() {
            return this._flag == Constants_1.Flags.Subscribe;
        }
        isEnd() {
            return this._flag == Constants_1.Flags.ReplyEnd;
        }
        sid() {
            return this._sid;
        }
        event() {
            return this._event;
        }
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
define("socketd/transport/core/Typealias", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/exception/SocketdException", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SocketdTimeoutException = exports.SocketdSizeLimitException = exports.SocketdConnectionException = exports.SocketdCodecException = exports.SocketdChannelException = exports.SocketdAlarmException = exports.SocketdException = void 0;
    class SocketdException extends Error {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdException = SocketdException;
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
    class SocketdChannelException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdChannelException = SocketdChannelException;
    class SocketdCodecException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdCodecException = SocketdCodecException;
    class SocketdConnectionException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdConnectionException = SocketdConnectionException;
    class SocketdSizeLimitException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdSizeLimitException = SocketdSizeLimitException;
    class SocketdTimeoutException extends SocketdException {
        constructor(message) {
            super(message);
        }
    }
    exports.SocketdTimeoutException = SocketdTimeoutException;
});
define("socketd/transport/core/Stream", ["require", "exports", "socketd/exception/SocketdException", "socketd/transport/core/Asserts"], function (require, exports, SocketdException_1, Asserts_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.StreamMangerDefault = exports.StreamSubscribe = exports.StreamRequest = exports.StreamBase = void 0;
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
        insuranceStart(streamManger, streamTimeout) {
            if (this._insuranceFuture > 0) {
                return;
            }
            this._insuranceFuture = window.setTimeout(() => {
                streamManger.removeStream(this.sid());
                this.onError(new SocketdException_1.SocketdTimeoutException("The stream response timeout, sid=" + this.sid()));
            }, streamTimeout);
        }
        insuranceCancel() {
            if (this._insuranceFuture > 0) {
                window.clearTimeout(this._insuranceFuture);
            }
        }
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
        getStream(sid) {
            return this._streamMap.get(sid);
        }
        addStream(sid, stream) {
            Asserts_1.Asserts.assertNull("stream", stream);
            this._streamMap.set(sid, stream);
            let streamTimeout = stream.timeout() > 0 ? stream.timeout() : this._config.getStreamTimeout();
            if (streamTimeout > 0) {
                stream.insuranceStart(this, streamTimeout);
            }
        }
        removeStream(sid) {
            let stream = this.getStream(sid);
            if (stream) {
                this._streamMap.delete(sid);
                stream.insuranceCancel();
                console.debug(`${this._config.getRoleName()} stream removed, sid=${sid}`);
            }
        }
    }
    exports.StreamMangerDefault = StreamMangerDefault;
});
define("socketd/transport/core/IdGenerator", ["require", "exports", "socketd/utils/StrUtils"], function (require, exports, StrUtils_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.GuidGenerator = void 0;
    class GuidGenerator {
        generate() {
            return StrUtils_1.StrUtils.guid();
        }
    }
    exports.GuidGenerator = GuidGenerator;
});
define("socketd/transport/core/Frame", ["require", "exports", "socketd/transport/core/Entity", "socketd/transport/core/Constants", "socketd/SocketD", "socketd/transport/core/Message"], function (require, exports, Entity_1, Constants_2, SocketD_1, Message_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Frames = exports.Frame = void 0;
    class Frame {
        constructor(flag, message) {
            this._flag = flag;
            this._message = message;
        }
        flag() {
            return this._flag;
        }
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
    class Frames {
        static connectFrame(sid, url) {
            let entity = new Entity_1.EntityDefault();
            entity.metaPut(Constants_2.EntityMetas.META_SOCKETD_VERSION, SocketD_1.SocketD.protocolVersion());
            return new Frame(Constants_2.Flags.Connect, new Message_1.MessageBuilder().sid(sid).event(url).entity(entity).build());
        }
        static connackFrame(connectMessage) {
            let entity = new Entity_1.EntityDefault();
            entity.metaPut(Constants_2.EntityMetas.META_SOCKETD_VERSION, SocketD_1.SocketD.protocolVersion());
            return new Frame(Constants_2.Flags.Connack, new Message_1.MessageBuilder().sid(connectMessage.sid()).event(connectMessage.event()).entity(entity).build());
        }
        static pingFrame() {
            return new Frame(Constants_2.Flags.Ping, null);
        }
        static pongFrame() {
            return new Frame(Constants_2.Flags.Pong, null);
        }
        static closeFrame() {
            return new Frame(Constants_2.Flags.Close, null);
        }
        static alarmFrame(from, alarm) {
            let message = new Message_1.MessageBuilder();
            if (from != null) {
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
define("socketd/transport/core/FragmentHolder", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FragmentHolder = void 0;
    class FragmentHolder {
        constructor(index, message) {
            this._index = index;
            this._message = message;
        }
        getIndex() {
            return this._index;
        }
        getMessage() {
            return this._message;
        }
    }
    exports.FragmentHolder = FragmentHolder;
});
define("socketd/transport/core/FragmentAggregator", ["require", "exports", "socketd/transport/core/Message", "socketd/transport/core/Entity", "socketd/transport/core/Frame", "socketd/transport/core/FragmentHolder", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, Message_2, Entity_2, Frame_1, FragmentHolder_1, Constants_3, SocketdException_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FragmentAggregatorDefault = void 0;
    class FragmentAggregatorDefault {
        constructor(main) {
            this._fragmentHolders = new Array();
            this._main = main;
            let dataLengthStr = main.meta(Constants_3.EntityMetas.META_DATA_LENGTH);
            if (!dataLengthStr) {
                throw new SocketdException_2.SocketdCodecException("Missing '" + Constants_3.EntityMetas.META_DATA_LENGTH + "' meta, event=" + main.event());
            }
            this._dataLength = parseInt(dataLengthStr);
        }
        getSid() {
            return this._main.sid();
        }
        getDataStreamSize() {
            return this._dataStreamSize;
        }
        getDataLength() {
            return this._dataLength;
        }
        add(index, message) {
            this._fragmentHolders.push(new FragmentHolder_1.FragmentHolder(index, message));
            this._dataStreamSize = this._dataStreamSize + message.dataSize();
        }
        get() {
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
            let dataBuffer = new ArrayBuffer(this._dataLength);
            let dataBufferView = new DataView(dataBuffer);
            let dataBufferViewIdx = 0;
            for (let fh of this._fragmentHolders) {
                let tmp = new DataView(fh.getMessage().data());
                for (let i = 0; i < fh.getMessage().data().byteLength; i++) {
                    dataBufferView.setInt8(dataBufferViewIdx, tmp.getInt8(i));
                    dataBufferViewIdx++;
                }
            }
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
define("socketd/transport/core/FragmentHandler", ["require", "exports", "socketd/transport/core/Entity", "socketd/transport/core/Constants", "socketd/transport/core/FragmentAggregator"], function (require, exports, Entity_3, Constants_4, FragmentAggregator_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.FragmentHandlerDefault = void 0;
    class FragmentHandlerDefault {
        nextFragment(channel, fragmentIndex, message) {
            let dataBuffer = this.readFragmentData(message.dataAsReader(), channel.getConfig().getFragmentSize());
            if (dataBuffer == null || dataBuffer.byteLength == 0) {
                return null;
            }
            let fragmentEntity = new Entity_3.EntityDefault().dataSet(dataBuffer);
            if (fragmentIndex == 1) {
                fragmentEntity.metaMapPut(message.metaMap());
            }
            fragmentEntity.metaPut(Constants_4.EntityMetas.META_DATA_FRAGMENT_IDX, fragmentIndex.toString());
            return fragmentEntity;
        }
        aggrFragment(channel, fragmentIndex, message) {
            let aggregator = channel.getAttachment(message.sid());
            if (aggregator == null) {
                aggregator = new FragmentAggregator_1.FragmentAggregatorDefault(message);
                channel.putAttachment(aggregator.getSid(), aggregator);
            }
            aggregator.add(fragmentIndex, message);
            if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
                return null;
            }
            else {
                channel.putAttachment(message.sid(), null);
                return aggregator.get();
            }
        }
        aggrEnable() {
            return true;
        }
        readFragmentData(ins, maxSize) {
            let size = 0;
            if (ins.remaining() > maxSize) {
                size = maxSize;
            }
            else {
                size = ins.remaining();
            }
            let buf = new ArrayBuffer(size);
            ins.getBytes(buf, 0, size);
            return buf;
        }
    }
    exports.FragmentHandlerDefault = FragmentHandlerDefault;
});
define("socketd/transport/core/Config", ["require", "exports", "socketd/transport/core/Codec", "socketd/transport/core/Stream", "socketd/transport/core/IdGenerator", "socketd/transport/core/FragmentHandler", "socketd/transport/core/Constants", "socketd/transport/core/Asserts"], function (require, exports, Codec_1, Stream_1, IdGenerator_1, FragmentHandler_1, Constants_5, Asserts_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ConfigBase = void 0;
    class ConfigBase {
        constructor(clientMode) {
            this._clientMode = clientMode;
            this._streamManger = new Stream_1.StreamMangerDefault(this);
            this._codec = new Codec_1.CodecByteBuffer(this);
            this._charset = "utf-8";
            this._idGenerator = new IdGenerator_1.GuidGenerator();
            this._fragmentHandler = new FragmentHandler_1.FragmentHandlerDefault();
            this._fragmentSize = Constants_5.Constants.MAX_SIZE_DATA;
            this._coreThreads = 2;
            this._maxThreads = this._coreThreads * 4;
            this._readBufferSize = 512;
            this._writeBufferSize = 512;
            this._idleTimeout = 0;
            this._requestTimeout = 10000;
            this._streamTimeout = 1000 * 60 * 60 * 2;
            this._maxUdpSize = 2048;
        }
        clientMode() {
            return this._clientMode;
        }
        getStreamManger() {
            return this._streamManger;
        }
        getRoleName() {
            return this.clientMode() ? "Client" : "Server";
        }
        getCharset() {
            return this._charset;
        }
        charset(charset) {
            this._charset = charset;
            return this;
        }
        getCodec() {
            return this._codec;
        }
        getIdGenerator() {
            return this._idGenerator;
        }
        idGenerator(idGenerator) {
            Asserts_2.Asserts.assertNull("idGenerator", idGenerator);
            this._idGenerator = idGenerator;
            return this;
        }
        getFragmentHandler() {
            return this._fragmentHandler;
        }
        fragmentHandler(fragmentHandler) {
            Asserts_2.Asserts.assertNull("fragmentHandler", fragmentHandler);
            this._fragmentHandler = fragmentHandler;
            return this;
        }
        getFragmentSize() {
            return this._fragmentSize;
        }
        fragmentSize(fragmentSize) {
            if (fragmentSize > Constants_5.Constants.MAX_SIZE_DATA) {
                throw new Error("The parameter fragmentSize cannot > 16m");
            }
            if (fragmentSize < Constants_5.Constants.MIN_FRAGMENT_SIZE) {
                throw new Error("The parameter fragmentSize cannot < 1k");
            }
            this._fragmentSize = fragmentSize;
            return this;
        }
        getCoreThreads() {
            return this._coreThreads;
        }
        coreThreads(coreThreads) {
            this._coreThreads = coreThreads;
            this._maxThreads = coreThreads * 4;
            return this;
        }
        getMaxThreads() {
            return this._maxThreads;
        }
        maxThreads(maxThreads) {
            this._maxThreads = maxThreads;
            return this;
        }
        getReadBufferSize() {
            return this._readBufferSize;
        }
        readBufferSize(readBufferSize) {
            this._readBufferSize = readBufferSize;
            return this;
        }
        getWriteBufferSize() {
            return this._writeBufferSize;
        }
        writeBufferSize(writeBufferSize) {
            this._writeBufferSize = writeBufferSize;
            return this;
        }
        getIdleTimeout() {
            return this._idleTimeout;
        }
        idleTimeout(idleTimeout) {
            this._idleTimeout = idleTimeout;
            return this;
        }
        getRequestTimeout() {
            return this._requestTimeout;
        }
        requestTimeout(requestTimeout) {
            this._requestTimeout = requestTimeout;
            return this;
        }
        getStreamTimeout() {
            return this._streamTimeout;
        }
        streamTimeout(streamTimeout) {
            this._streamTimeout = streamTimeout;
            return this;
        }
        getMaxUdpSize() {
            return this._maxUdpSize;
        }
        maxUdpSize(maxUdpSize) {
            this._maxUdpSize = maxUdpSize;
            return this;
        }
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
define("socketd/transport/core/Channel", ["require", "exports", "socketd/transport/core/Frame"], function (require, exports, Frame_2) {
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
            this.send(Frame_2.Frames.connectFrame(this.getConfig().getIdGenerator().generate(), url), null);
        }
        sendConnack(connectMessage) {
            this.send(Frame_2.Frames.connackFrame(connectMessage), null);
        }
        sendPing() {
            this.send(Frame_2.Frames.pingFrame(), null);
        }
        sendPong() {
            this.send(Frame_2.Frames.pongFrame(), null);
        }
        sendClose() {
            this.send(Frame_2.Frames.closeFrame(), null);
        }
        sendAlarm(from, alarm) {
            this.send(Frame_2.Frames.alarmFrame(from, alarm), null);
        }
    }
    exports.ChannelBase = ChannelBase;
});
define("socketd/transport/core/Asserts", ["require", "exports", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, Constants_6, SocketdException_3) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Asserts = void 0;
    class Asserts {
        static assertClosed(channel) {
            if (channel != null && channel.isClosed() > 0) {
                throw new SocketdException_3.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
            }
        }
        static assertClosedByUser(channel) {
            if (channel != null && channel.isClosed() == Constants_6.Constants.CLOSE4_USER) {
                throw new SocketdException_3.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
            }
        }
        static assertNull(name, val) {
            if (val == null) {
                throw new Error("The argument cannot be null: " + name);
            }
        }
        static assertEmpty(name, val) {
            if (!val) {
                throw new Error("The argument cannot be empty: " + name);
            }
        }
        static assertSize(name, size, limitSize) {
            if (size > limitSize) {
                let message = `This message ${name} size is out of limit ${limitSize} (${size})`;
                throw new SocketdException_3.SocketdSizeLimitException(message);
            }
        }
    }
    exports.Asserts = Asserts;
});
define("socketd/transport/core/Codec", ["require", "exports", "socketd/transport/core/Asserts", "socketd/transport/core/Constants", "socketd/transport/core/Entity", "socketd/transport/core/Message", "socketd/utils/StrUtils", "socketd/transport/core/Frame"], function (require, exports, Asserts_3, Constants_7, Entity_4, Message_3, StrUtils_2, Frame_3) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ArrayBufferCodecWriter = exports.ArrayBufferCodecReader = exports.CodecByteBuffer = void 0;
    class CodecByteBuffer {
        constructor(config) {
            this._config = config;
        }
        write(frame, targetFactory) {
            if (frame.message()) {
                let sidB = StrUtils_2.StrUtils.strToBuf(frame.message().sid(), this._config.getCharset());
                let eventB = StrUtils_2.StrUtils.strToBuf(frame.message().event(), this._config.getCharset());
                let metaStringB = StrUtils_2.StrUtils.strToBuf(frame.message().metaString(), this._config.getCharset());
                let frameSize = 4 + 4 + sidB.byteLength + eventB.byteLength + metaStringB.byteLength + frame.message().dataSize() + 2 * 3;
                Asserts_3.Asserts.assertSize("sid", sidB.byteLength, Constants_7.Constants.MAX_SIZE_SID);
                Asserts_3.Asserts.assertSize("event", eventB.byteLength, Constants_7.Constants.MAX_SIZE_EVENT);
                Asserts_3.Asserts.assertSize("metaString", metaStringB.byteLength, Constants_7.Constants.MAX_SIZE_META_STRING);
                Asserts_3.Asserts.assertSize("data", frame.message().dataSize(), Constants_7.Constants.MAX_SIZE_DATA);
                let target = targetFactory(frameSize);
                target.putInt(frameSize);
                target.putInt(frame.flag());
                target.putBytes(sidB);
                target.putChar('\n'.charCodeAt(0));
                target.putBytes(eventB);
                target.putChar('\n'.charCodeAt(0));
                target.putBytes(metaStringB);
                target.putChar('\n'.charCodeAt(0));
                target.putBytes(frame.message().data());
                target.flush();
                return target;
            }
            else {
                let frameSize = 4 + 4;
                let target = targetFactory(frameSize);
                target.putInt(frameSize);
                target.putInt(frame.flag());
                target.flush();
                return target;
            }
        }
        read(buffer) {
            let frameSize = buffer.getInt();
            if (frameSize > (buffer.remaining() + 4)) {
                return null;
            }
            let flag = buffer.getInt();
            if (frameSize == 8) {
                return new Frame_3.Frame(Constants_7.Flags.of(flag), null);
            }
            else {
                let metaBufSize = Math.min(Constants_7.Constants.MAX_SIZE_META_STRING, buffer.remaining());
                let buf = new ArrayBuffer(metaBufSize);
                let sid = this.decodeString(buffer, buf, Constants_7.Constants.MAX_SIZE_SID);
                let event = this.decodeString(buffer, buf, Constants_7.Constants.MAX_SIZE_EVENT);
                let metaString = this.decodeString(buffer, buf, Constants_7.Constants.MAX_SIZE_META_STRING);
                let dataRealSize = frameSize - buffer.position();
                let data;
                if (dataRealSize > Constants_7.Constants.MAX_SIZE_DATA) {
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
                let message = new Message_3.MessageBuilder()
                    .flag(Constants_7.Flags.of(flag))
                    .sid(sid)
                    .event(event)
                    .entity(new Entity_4.EntityDefault().dataSet(data).metaStringSet(metaString))
                    .build();
                return new Frame_3.Frame(message.flag(), message);
            }
        }
        decodeString(reader, buf, maxLen) {
            let bufView = new DataView(buf);
            let bufViewIdx = 0;
            while (true) {
                let c = reader.getByte();
                if (c == 10) {
                    break;
                }
                if (maxLen > 0 && maxLen <= bufViewIdx) {
                }
                else {
                    if (c != 0) {
                        bufView.setInt8(bufViewIdx, c);
                        bufViewIdx++;
                    }
                }
            }
            if (bufViewIdx < 1) {
                return "";
            }
            return StrUtils_2.StrUtils.bufToStr(buf, 0, bufViewIdx, this._config.getCharset());
        }
    }
    exports.CodecByteBuffer = CodecByteBuffer;
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
            let tmp = this._bufView.getInt8(this._bufViewIdx);
            this._bufViewIdx += 1;
            return tmp;
        }
        getBytes(dst, offset, length) {
            let tmp = new DataView(dst);
            let tmpEndIdx = offset + length;
            for (let i = offset; i < tmpEndIdx; i++) {
                if (this._bufViewIdx >= this._buf.byteLength) {
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
            let tmp = this._bufView.getInt32(this._bufViewIdx);
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
            let tmp = new DataView(src);
            let len = tmp.byteLength;
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
define("socketd/transport/core/Entity", ["require", "exports", "socketd/utils/StrUtils", "socketd/transport/core/Codec"], function (require, exports, StrUtils_3, Codec_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.StringEntity = exports.EntityDefault = void 0;
    class EntityDefault {
        constructor() {
            this._metaMap = null;
            this._data = new ArrayBuffer(0);
            this._dataAsReader = null;
        }
        at() {
            return this.meta("@");
        }
        metaStringSet(metaString) {
            this._metaMap = new URLSearchParams(metaString);
            return this;
        }
        metaMapPut(map) {
            for (let name of map.prototype) {
                this.metaMap().set(name, map[name]);
            }
            return this;
        }
        metaPut(name, val) {
            this.metaMap().set(name, val);
            return this;
        }
        metaString() {
            return this.metaMap().toString();
        }
        metaMap() {
            if (this._metaMap == null) {
                this._metaMap = new URLSearchParams();
            }
            return this._metaMap;
        }
        meta(name) {
            return this.metaMap().get(name);
        }
        metaOrDefault(name, def) {
            let val = this.meta(name);
            if (val) {
                return val;
            }
            else {
                return def;
            }
        }
        metaAsInt(name) {
            return parseInt(this.metaOrDefault(name, '0'));
        }
        metaAsFloat(name) {
            return parseFloat(this.metaOrDefault(name, '0'));
        }
        putMeta(name, val) {
            this.metaPut(name, val);
        }
        dataSet(data) {
            this._data = data;
            return this;
        }
        data() {
            return this._data;
        }
        dataAsReader() {
            if (!this._dataAsReader) {
                this._dataAsReader = new Codec_2.ArrayBufferCodecReader(this._data);
            }
            return this._dataAsReader;
        }
        dataAsString() {
            return StrUtils_3.StrUtils.bufToStrDo(this._data, '');
        }
        dataSize() {
            return this._data.byteLength;
        }
        release() {
        }
        toString() {
            return "Entity{" +
                "meta='" + this.metaString() + '\'' +
                ", data=byte[" + this.dataSize() + ']' +
                '}';
        }
    }
    exports.EntityDefault = EntityDefault;
    class StringEntity extends EntityDefault {
        constructor(data) {
            super();
            const dataBuf = StrUtils_3.StrUtils.strToBuf(data);
            this.dataSet(dataBuf);
        }
    }
    exports.StringEntity = StringEntity;
});
define("socketd/transport/client/ClientSession", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
define("socketd/transport/core/Session", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SessionBase = void 0;
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
            let tmp = this.attr(name);
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
    class RouteSelectorDefault {
        constructor() {
            this._inner = new Map();
        }
        select(route) {
            return this._inner.get(route);
        }
        put(route, target) {
            this._inner.set(route, target);
        }
        remove(route) {
            this._inner.delete(route);
        }
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
    class EventListener {
        constructor(routeSelector) {
            if (routeSelector == null) {
                this._eventRouteSelector = new RouteSelector_1.RouteSelectorDefault();
            }
            else {
                this._eventRouteSelector = routeSelector;
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
    class PathListener {
        constructor(routeSelector) {
            if (routeSelector == null) {
                this._pathRouteSelector = new RouteSelector_1.RouteSelectorDefault();
            }
            else {
                this._pathRouteSelector = routeSelector;
            }
        }
        of(path, listener) {
            this._pathRouteSelector.put(path, listener);
            return this;
        }
        size() {
            return this._pathRouteSelector.size();
        }
        onOpen(session) {
            let l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onOpen(session);
            }
        }
        onMessage(session, message) {
            let l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onMessage(session, message);
            }
        }
        onClose(session) {
            let l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onClose(session);
            }
        }
        onError(session, error) {
            let l1 = this._pathRouteSelector.select(session.path());
            if (l1 != null) {
                l1.onError(session, error);
            }
        }
    }
    exports.PathListener = PathListener;
    class PipelineListener {
        constructor() {
            this._deque = new Array();
        }
        prev(listener) {
            this._deque.unshift(listener);
            return this;
        }
        next(listener) {
            this._deque.push(listener);
            return this;
        }
        size() {
            return this._deque.length;
        }
        onOpen(session) {
            for (let listener of this._deque) {
                listener.onOpen(session);
            }
        }
        onMessage(session, message) {
            for (let listener of this._deque) {
                listener.onMessage(session, message);
            }
        }
        onClose(session) {
            for (let listener of this._deque) {
                listener.onClose(session);
            }
        }
        onError(session, error) {
            for (let listener of this._deque) {
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
        getSchema() {
            return this._schema;
        }
        getUrl() {
            return this._url;
        }
        getUri() {
            return this._uri;
        }
        getLinkUrl() {
            return this._linkUrl;
        }
        getHost() {
            return this._uri.host;
        }
        getPort() {
            return this._port;
        }
        getHeartbeatInterval() {
            return this._heartbeatInterval;
        }
        heartbeatInterval(heartbeatInterval) {
            this._heartbeatInterval = heartbeatInterval;
            return this;
        }
        getConnectTimeout() {
            return this._connectTimeout;
        }
        connectTimeout(connectTimeout) {
            this._connectTimeout = connectTimeout;
            return this;
        }
        isAutoReconnect() {
            return this._autoReconnect;
        }
        autoReconnect(autoReconnect) {
            this._autoReconnect = autoReconnect;
            return this;
        }
        idleTimeout(idleTimeout) {
            if (this._autoReconnect == false) {
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
define("socketd/transport/core/HandshakeDefault", ["require", "exports", "socketd/transport/core/Constants"], function (require, exports, Constants_8) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.HandshakeDefault = void 0;
    class HandshakeDefault {
        constructor(source) {
            this._source = source;
            this._url = new URL(source.event());
            this._version = source.meta(Constants_8.EntityMetas.META_SOCKETD_VERSION);
            this._paramMap = new Map();
            for (let [k, v] of this._url.searchParams) {
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
            let tmp = this.param(name);
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
define("socketd/transport/core/Processor", ["require", "exports", "socketd/transport/core/Listener", "socketd/transport/core/Constants", "socketd/exception/SocketdException", "socketd/transport/core/HandshakeDefault"], function (require, exports, Listener_1, Constants_9, SocketdException_4, HandshakeDefault_1) {
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
            if (frame.flag() == Constants_9.Flags.Connect) {
                channel.setHandshake(new HandshakeDefault_1.HandshakeDefault(frame.message()));
                channel.onOpenFuture((r, err) => {
                    if (r && channel.isValid()) {
                        try {
                            channel.sendConnack(frame.getMessage());
                        }
                        catch (err) {
                            this.onError(channel, err);
                        }
                    }
                });
                this.onOpen(channel);
            }
            else if (frame.flag() == Constants_9.Flags.Connack) {
                channel.setHandshake(new HandshakeDefault_1.HandshakeDefault(frame.message()));
                this.onOpen(channel);
            }
            else {
                if (channel.getHandshake() == null) {
                    channel.close(Constants_9.Constants.CLOSE1_PROTOCOL);
                    if (frame.flag() == Constants_9.Flags.Close) {
                        throw new SocketdException_4.SocketdConnectionException("Connection request was rejected");
                    }
                    console.warn(`${channel.getConfig().getRoleName()} channel handshake is null, sessionId=${channel.getSession().sessionId()}`);
                    return;
                }
                try {
                    switch (frame.flag()) {
                        case Constants_9.Flags.Ping: {
                            channel.sendPong();
                            break;
                        }
                        case Constants_9.Flags.Pong: {
                            break;
                        }
                        case Constants_9.Flags.Close: {
                            channel.close(Constants_9.Constants.CLOSE1_PROTOCOL);
                            this.onCloseInternal(channel);
                            break;
                        }
                        case Constants_9.Flags.Alarm: {
                            let exception = new SocketdException_4.SocketdAlarmException(frame.getMessage());
                            let acceptor = channel.getConfig().getStreamManger().getStream(frame.getMessage().sid());
                            if (acceptor == null) {
                                this.onError(channel, exception);
                            }
                            else {
                                channel.getConfig().getStreamManger().removeStream(frame.getMessage().sid());
                                acceptor.onError(exception);
                            }
                            break;
                        }
                        case Constants_9.Flags.Message:
                        case Constants_9.Flags.Request:
                        case Constants_9.Flags.Subscribe: {
                            this.onReceiveDo(channel, frame, false);
                            break;
                        }
                        case Constants_9.Flags.Reply:
                        case Constants_9.Flags.ReplyEnd: {
                            this.onReceiveDo(channel, frame, true);
                            break;
                        }
                        default: {
                            channel.close(Constants_9.Constants.CLOSE2_PROTOCOL_ILLEGAL);
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
            if (channel.getConfig().getFragmentHandler().aggrEnable()) {
                let fragmentIdxStr = frame.message().meta(Constants_9.EntityMetas.META_DATA_FRAGMENT_IDX);
                if (fragmentIdxStr != null) {
                    let index = parseInt(fragmentIdxStr);
                    let frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, index, frame.getMessage());
                    if (frameNew == null) {
                        return;
                    }
                    else {
                        frame = frameNew;
                    }
                }
            }
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
define("socketd/transport/client/ClientChannel", ["require", "exports", "socketd/transport/core/Channel", "socketd/transport/core/HeartbeatHandler", "socketd/transport/core/Constants", "socketd/transport/core/Asserts", "socketd/exception/SocketdException", "socketd/utils/RunUtils"], function (require, exports, Channel_1, HeartbeatHandler_1, Constants_10, Asserts_4, SocketdException_5, RunUtils_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientChannel = void 0;
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
        initHeartbeat() {
            if (this._heartbeatScheduledFuture) {
                clearInterval(this._heartbeatScheduledFuture);
            }
            if (this._connector.autoReconnect()) {
                this._heartbeatScheduledFuture = window.setInterval(() => {
                    try {
                        this.heartbeatHandle();
                    }
                    catch (e) {
                        console.warn("Client channel heartbeat error", e);
                    }
                }, this._connector.heartbeatInterval());
            }
        }
        heartbeatHandle() {
            return __awaiter(this, void 0, void 0, function* () {
                if (this._real != null) {
                    if (this._real.getHandshake() == null) {
                        return;
                    }
                    if (this._real.isClosed() == Constants_10.Constants.CLOSE4_USER) {
                        console.debug(`Client channel is closed (pause heartbeat), sessionId=${this.getSession().sessionId()}`);
                        return;
                    }
                }
                try {
                    yield this.prepareCheck();
                    this._heartbeatHandler.heartbeat(this.getSession());
                }
                catch (e) {
                    if (e instanceof SocketdException_5.SocketdException) {
                        throw e;
                    }
                    if (this._connector.autoReconnect()) {
                        this._real.close(Constants_10.Constants.CLOSE3_ERROR);
                        this._real = null;
                    }
                    throw new SocketdException_5.SocketdChannelException(e);
                }
            });
        }
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
        isValid() {
            if (this._real == null) {
                return false;
            }
            else {
                return this._real.isValid();
            }
        }
        isClosed() {
            if (this._real == null) {
                return 0;
            }
            else {
                return this._real.isClosed();
            }
        }
        send(frame, stream) {
            return __awaiter(this, void 0, void 0, function* () {
                Asserts_4.Asserts.assertClosedByUser(this._real);
                try {
                    yield this.prepareCheck();
                    this._real.send(frame, stream);
                }
                catch (e) {
                    if (this._connector.autoReconnect()) {
                        this._real.close(Constants_10.Constants.CLOSE3_ERROR);
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
            RunUtils_1.RunUtils.runAndTry(() => window.clearInterval(this._heartbeatScheduledFuture));
            RunUtils_1.RunUtils.runAndTry(() => this._connector.close());
            RunUtils_1.RunUtils.runAndTry(() => this._real.close(code));
            super.close(code);
        }
        getSession() {
            return this._real.getSession();
        }
    }
    exports.ClientChannel = ClientChannel;
});
define("socketd/transport/core/SessionDefault", ["require", "exports", "socketd/transport/core/Session", "socketd/transport/core/Message", "socketd/transport/core/Frame", "socketd/transport/core/Constants", "socketd/transport/core/Stream"], function (require, exports, Session_1, Message_4, Frame_4, Constants_11, Stream_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SessionDefault = void 0;
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
        param(name) {
            return this.handshake().param(name);
        }
        paramOrDefault(name, def) {
            return this.handshake().paramOrDefault(name, def);
        }
        path() {
            if (this._pathNew == null) {
                return this.handshake().uri().pathname;
            }
            else {
                return this._pathNew;
            }
        }
        pathNew(pathNew) {
            this._pathNew = pathNew;
        }
        reconnect() {
            this._channel.reconnect();
        }
        sendPing() {
            this._channel.sendPing();
        }
        sendAlarm(from, alarm) {
            this._channel.sendAlarm(from, alarm);
        }
        send(event, content) {
            let message = new Message_4.MessageBuilder()
                .sid(this.generateId())
                .event(event)
                .entity(content)
                .build();
            this._channel.send(new Frame_4.Frame(Constants_11.Flags.Message, message), null);
        }
        sendAndRequest(event, content, consumer, timeout) {
            let message = new Message_4.MessageBuilder()
                .sid(this.generateId())
                .event(event)
                .entity(content)
                .build();
            let stream = new Stream_2.StreamRequest(message.sid(), timeout, consumer);
            this._channel.send(new Frame_4.Frame(Constants_11.Flags.Request, message), stream);
            return stream;
        }
        sendAndSubscribe(event, content, consumer, timeout) {
            let message = new Message_4.MessageBuilder()
                .sid(this.generateId())
                .event(event)
                .entity(content)
                .build();
            let stream = new Stream_2.StreamSubscribe(message.sid(), timeout, consumer);
            this._channel.send(new Frame_4.Frame(Constants_11.Flags.Subscribe, message), stream);
            return stream;
        }
        reply(from, content) {
            let message = new Message_4.MessageBuilder()
                .sid(from.sid())
                .event(from.event())
                .entity(content)
                .build();
            this._channel.send(new Frame_4.Frame(Constants_11.Flags.Reply, message), null);
        }
        replyEnd(from, content) {
            let message = new Message_4.MessageBuilder()
                .sid(from.sid())
                .event(from.event())
                .entity(content)
                .build();
            this._channel.send(new Frame_4.Frame(Constants_11.Flags.ReplyEnd, message), null);
        }
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
            this._channel.close(Constants_11.Constants.CLOSE4_USER);
        }
    }
    exports.SessionDefault = SessionDefault;
});
define("socketd/transport/client/Client", ["require", "exports", "socketd/transport/core/Processor", "socketd/transport/client/ClientChannel", "socketd/transport/core/SessionDefault"], function (require, exports, Processor_1, ClientChannel_1, SessionDefault_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClientBase = void 0;
    class ClientBase {
        constructor(clientConfig, assistant) {
            this._config = clientConfig;
            this._assistant = assistant;
            this._processor = new Processor_1.ProcessorDefault();
        }
        getAssistant() {
            return this._assistant;
        }
        getHeartbeatHandler() {
            return this._heartbeatHandler;
        }
        getHeartbeatInterval() {
            return this.getConfig().getHeartbeatInterval();
        }
        getConfig() {
            return this._config;
        }
        getProcessor() {
            return this._processor;
        }
        heartbeatHandler(handler) {
            if (handler != null) {
                this._heartbeatHandler = handler;
            }
            return this;
        }
        config(configHandler) {
            if (configHandler != null) {
                configHandler(this._config);
            }
            return this;
        }
        listen(listener) {
            if (listener != null) {
                this._processor.setListener(listener);
            }
            return this;
        }
        open() {
            return __awaiter(this, void 0, void 0, function* () {
                let connector = this.createConnector();
                let channel0 = yield connector.connect();
                let clientChannel = new ClientChannel_1.ClientChannel(channel0, connector);
                clientChannel.setHandshake(channel0.getHandshake());
                let session = new SessionDefault_1.SessionDefault(clientChannel);
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
define("socketd/cluster/ClusterClientSession", ["require", "exports", "socketd/utils/StrUtils", "socketd/exception/SocketdException", "socketd/transport/client/ClientChannel", "socketd/utils/RunUtils"], function (require, exports, StrUtils_4, SocketdException_6, ClientChannel_2, RunUtils_2) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClusterClientSession = void 0;
    class ClusterClientSession {
        constructor(sessions) {
            this._sessionSet = sessions;
            this._sessionId = StrUtils_4.StrUtils.guid();
            this._sessionRoundCounter = 0;
        }
        getSessionAll() {
            return this._sessionSet;
        }
        getSessionOne() {
            if (this._sessionSet.length == 0) {
                throw new SocketdException_6.SocketdException("No session!");
            }
            else if (this._sessionSet.length == 1) {
                return this._sessionSet[0];
            }
            else {
                let sessions = new ClientChannel_2.ClientChannel[this._sessionSet.length];
                let sessionsSize = 0;
                for (let s of this._sessionSet) {
                    if (s.isValid()) {
                        sessions[sessionsSize] = s;
                        sessionsSize++;
                    }
                }
                if (sessionsSize == 0) {
                    throw new SocketdException_6.SocketdException("No session is available!");
                }
                if (sessionsSize == 1) {
                    return sessions[0];
                }
                let counter = this._sessionRoundCounter++;
                let idx = counter % sessionsSize;
                if (counter > 999999999) {
                    this._sessionRoundCounter = 0;
                }
                return sessions[idx];
            }
        }
        isValid() {
            for (let session of this._sessionSet) {
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
            for (let session of this._sessionSet) {
                if (session.isValid() == false) {
                    session.reconnect();
                }
            }
        }
        send(event, content) {
            let sender = this.getSessionOne();
            sender.send(event, content);
        }
        sendAndRequest(event, content, consumer, timeout) {
            let sender = this.getSessionOne();
            return sender.sendAndRequest(event, content, consumer, timeout);
        }
        sendAndSubscribe(event, content, consumer, timeout) {
            let sender = this.getSessionOne();
            return sender.sendAndSubscribe(event, content, consumer, timeout);
        }
        close() {
            for (let session of this._sessionSet) {
                RunUtils_2.RunUtils.runAndTry(session.close);
            }
        }
    }
    exports.ClusterClientSession = ClusterClientSession;
});
define("socketd/cluster/ClusterClient", ["require", "exports", "socketd/SocketD", "socketd/cluster/ClusterClientSession"], function (require, exports, SocketD_2, ClusterClientSession_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ClusterClient = void 0;
    class ClusterClient {
        constructor(serverUrls) {
            this._serverUrls = serverUrls;
        }
        heartbeatHandler(heartbeatHandler) {
            this._heartbeatHandler = heartbeatHandler;
            return this;
        }
        config(configHandler) {
            this._configHandler = configHandler;
            return this;
        }
        listen(listener) {
            this._listener = listener;
            return this;
        }
        open() {
            return __awaiter(this, void 0, void 0, function* () {
                let sessionList = new ClusterClient[this._serverUrls.length];
                for (let urls of this._serverUrls) {
                    for (let url of urls.split(",")) {
                        url = url.trim();
                        if (!url) {
                            continue;
                        }
                        let client = SocketD_2.SocketD.createClient(url);
                        if (this._listener != null) {
                            client.listen(this._listener);
                        }
                        if (this._configHandler != null) {
                            client.config(this._configHandler);
                        }
                        if (this._heartbeatHandler != null) {
                            client.heartbeatHandler(this._heartbeatHandler);
                        }
                        let session = yield client.open();
                        sessionList.add(session);
                    }
                }
                return new ClusterClientSession_1.ClusterClientSession(sessionList);
            });
        }
    }
    exports.ClusterClient = ClusterClient;
});
define("socketd_websocket/WsChannelAssistant", ["require", "exports", "socketd/transport/core/Codec"], function (require, exports, Codec_3) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.WsChannelAssistant = void 0;
    class WsChannelAssistant {
        constructor(config) {
            this._config = config;
        }
        read(buffer) {
            return this._config.getCodec().read(new Codec_3.ArrayBufferCodecReader(buffer));
        }
        write(target, frame) {
            let tmp = this._config.getCodec()
                .write(frame, n => new Codec_3.ArrayBufferCodecWriter(n));
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
define("socketd/transport/core/ChannelDefault", ["require", "exports", "socketd/transport/core/Frame", "socketd/transport/core/Message", "socketd/transport/core/Constants", "socketd/transport/core/Channel", "socketd/transport/core/SessionDefault"], function (require, exports, Frame_5, Message_5, Constants_12, Channel_2, SessionDefault_2) {
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
                let message = frame.message();
                if (stream != null) {
                    this._streamManger.addStream(message.sid(), stream);
                }
                if (message.entity() != null) {
                    if (message.dataSize() > this.getConfig().getFragmentSize()) {
                        message.putMeta(Constants_12.EntityMetas.META_DATA_LENGTH, message.dataSize().toString());
                        let fragmentIndex = 0;
                        while (true) {
                            fragmentIndex++;
                            let fragmentEntity = this.getConfig().getFragmentHandler().nextFragment(this, fragmentIndex, message);
                            if (fragmentEntity != null) {
                                let fragmentFrame = new Frame_5.Frame(frame.flag(), new Message_5.MessageBuilder()
                                    .flag(frame.flag())
                                    .sid(message.sid())
                                    .entity(fragmentEntity)
                                    .build());
                                this._assistant.write(this._source, fragmentFrame);
                            }
                            else {
                                return;
                            }
                        }
                    }
                    else {
                        this._assistant.write(this._source, frame);
                        return;
                    }
                }
            }
            this._assistant.write(this._source, frame);
        }
        retrieve(frame) {
            let stream = this._streamManger.getStream(frame.message().sid());
            if (stream != null) {
                if (stream.isSingle() || frame.flag() == Constants_12.Flags.ReplyEnd) {
                    this._streamManger.removeStream(frame.message().sid());
                }
                if (stream.isSingle()) {
                    stream.onAccept(frame.message(), this);
                }
                else {
                    stream.onAccept(frame.message(), this);
                }
            }
            else {
                console.debug(`${this.getConfig().getRoleName()} stream not found, sid=${frame.message().sid()}, sessionId=${this.getSession().sessionId()}`);
            }
        }
        reconnect() {
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
define("socketd_websocket/impl/WebSocketClientImpl", ["require", "exports", "socketd/transport/client/ClientHandshakeResult", "socketd/transport/core/ChannelDefault", "socketd/transport/core/Constants", "socketd/exception/SocketdException"], function (require, exports, ClientHandshakeResult_1, ChannelDefault_1, Constants_13, SocketdException_7) {
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
                        if (frame.flag() == Constants_13.Flags.Connack) {
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
                    if (e instanceof SocketdException_7.SocketdConnectionException) {
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
            this.close();
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
define("socketd/SocketD", ["require", "exports", "socketd/transport/core/Asserts", "socketd/transport/client/ClientConfig", "socketd/cluster/ClusterClient", "socketd_websocket/WsClientProvider"], function (require, exports, Asserts_5, ClientConfig_1, ClusterClient_1, WsClientProvider_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.SocketD = void 0;
    class SocketD {
        static version() {
            return "2.2.1-SNAPSHOT";
        }
        static protocolVersion() {
            return "1.0";
        }
        static createClient(serverUrl) {
            let client = SocketD.createClientOrNull(serverUrl);
            if (client == null) {
                throw new Error("No socketd client providers were found.");
            }
            else {
                return client;
            }
        }
        static createClientOrNull(serverUrl) {
            Asserts_5.Asserts.assertNull("serverUrl", serverUrl);
            let idx = serverUrl.indexOf("://");
            if (idx < 2) {
                throw new Error("The serverUrl invalid: " + serverUrl);
            }
            let schema = serverUrl.substring(0, idx);
            let factory = SocketD.clientProviderMap.get(schema);
            if (factory == null) {
                return null;
            }
            else {
                let clientConfig = new ClientConfig_1.ClientConfig(serverUrl);
                return factory.createClient(clientConfig);
            }
        }
        static createClusterClient(serverUrls) {
            return new ClusterClient_1.ClusterClient(serverUrls);
        }
    }
    exports.SocketD = SocketD;
    SocketD.clientProviderMap = new Map();
    (() => {
        let provider = new WsClientProvider_1.WsClientProvider();
        for (let s of provider.schemas()) {
            SocketD.clientProviderMap.set(s, provider);
        }
    })();
});
