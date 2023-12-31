declare module "socketd/utils/StrUtils" {
    export class StrUtils {
        static guid(): string;
        static strToBuf(str: string, charet?: string): ArrayBuffer;
        static bufToStr(buf: ArrayBuffer, start: number, length: number, charet?: string): string;
        static bufToStrDo(buf: ArrayBuffer, charet?: string): string;
    }
}
declare module "socketd/transport/core/Constants" {
    /**
     * 常量
     *
     * @author noear
     * @since 2.0
     */
    export const Constants: {
        /**
         * 默认流id（占位）
         */
        DEF_SID: string;
        /**
         * 默认事件（占位）
         */
        DEF_EVENT: string;
        /**
         * 默认元信息字符串（占位）
         */
        DEF_META_STRING: string;
        /**
         * 因协议指令关闭
         */
        CLOSE1_PROTOCOL: number;
        /**
         * 因协议非法关闭
         */
        CLOSE2_PROTOCOL_ILLEGAL: number;
        /**
         * 因异常关闭
         */
        CLOSE3_ERROR: number;
        /**
         * 因用户主动关闭
         */
        CLOSE4_USER: number;
        /**
         * 流ID长度最大限制
         */
        MAX_SIZE_SID: number;
        /**
         * 事件长度最大限制
         */
        MAX_SIZE_EVENT: number;
        /**
         * 元信息串长度最大限制
         */
        MAX_SIZE_META_STRING: number;
        /**
         * 数据长度最大限制（也是分片长度最大限制）
         */
        MAX_SIZE_DATA: number;
        /**
         * 分片长度最小限制
         */
        MIN_FRAGMENT_SIZE: number;
    };
    /**
     * 标志
     *
     * @author noear
     * @since 2.0
     */
    export const Flags: {
        /**
         * 未知
         */
        Unknown: number;
        /**
         * 连接
         */
        Connect: number;
        /**
         * 连接确认
         */
        Connack: number;
        /**
         * Ping
         */
        Ping: number;
        /**
         * Pong
         */
        Pong: number;
        /**
         * 关闭（Udp 没有断链的概念，需要发消息）
         */
        Close: number;
        /**
         * 告警
         */
        Alarm: number;
        /**
         * 消息
         */
        Message: number;
        /**
         * 请求
         */
        Request: number;
        /**
         * 订阅
         */
        Subscribe: number;
        /**
         * 答复
         */
        Reply: number;
        /**
         * 答复结束（结束订阅接收）
         */
        ReplyEnd: number;
        of: (code: number) => any;
        name: (code: number) => "Connect" | "Connack" | "Ping" | "Pong" | "Close" | "Alarm" | "Message" | "Request" | "Subscribe" | "Reply" | "ReplyEnd" | "Unknown";
    };
    /**
     * 实体元信息常用名
     *
     * @author noear
     * @since 2.0
     */
    export const EntityMetas: {
        /**
         * 框架版本号
         */
        META_SOCKETD_VERSION: string;
        /**
         * 数据长度
         */
        META_DATA_LENGTH: string;
        /**
         * 数据类型
         */
        META_DATA_TYPE: string;
        /**
         * 数据分片索引
         */
        META_DATA_FRAGMENT_IDX: string;
        /**
         * 数据描述之文件名
         */
        META_DATA_DISPOSITION_FILENAME: string;
        /**
         * 数据范围开始（相当于分页）
         */
        META_RANGE_START: string;
        /**
         * 数据范围长度
         */
        META_RANGE_SIZE: string;
    };
}
declare module "socketd/transport/core/Message" {
    import type { Entity, Reply } from "socketd/transport/core/Entity";
    import type { CodecReader } from "socketd/transport/core/Codec";
    /**
     * 消息
     *
     * @author noear
     * @since 2.0
     */
    export interface Message extends Entity {
        /**
         * 是否为请求
         */
        isRequest(): boolean;
        /**
         * 是否为订阅
         */
        isSubscribe(): boolean;
        /**
         * 获取消息流Id（用于消息交互、分片）
         */
        sid(): string;
        /**
         * 获取消息事件
         */
        event(): string;
        /**
         * 获取消息实体（有时需要获取实体）
         */
        entity(): Entity | null;
    }
    /**
     * @author noear
     * @since 2.0
     */
    export interface MessageInternal extends Message, Entity, Reply {
        /**
         * 获取数据读模式
         * */
        dataAsReader(): CodecReader;
        /**
         * 获取标记
         */
        flag(): number;
    }
    /**
     * 消息默认实现（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    export class MessageBuilder {
        private _flag;
        private _sid;
        private _event;
        private _entity;
        /**
         * 设置标记
         */
        flag(flag: number): MessageBuilder;
        /**
         * 设置流id
         */
        sid(sid: string): MessageBuilder;
        /**
         * 设置事件
         */
        event(event: string): MessageBuilder;
        /**
         * 设置实体
         */
        entity(entity: Entity): MessageBuilder;
        /**
         * 构建
         */
        build(): MessageInternal;
    }
    /**
     * 消息默认实现（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    export class MessageDefault implements MessageInternal {
        private _flag;
        private _sid;
        private _event;
        private _entity;
        constructor(flag: number, sid: string, event: string, entity: Entity | null);
        at(): any;
        /**
         * 获取标记
         */
        flag(): number;
        /**
         * 是否为请求
         */
        isRequest(): boolean;
        /**
         * 是否为订阅
         */
        isSubscribe(): boolean;
        /**
         * 是否答复结束
         * */
        isEnd(): boolean;
        /**
         * 获取消息流Id（用于消息交互、分片）
         */
        sid(): string;
        /**
         * 获取消息事件
         */
        event(): string;
        /**
         * 获取消息实体
         */
        entity(): Entity | null;
        toString(): string;
        metaString(): string;
        metaMap(): URLSearchParams;
        meta(name: string): string | null;
        metaOrDefault(name: string, def: string): string;
        metaAsInt(name: string): number;
        metaAsFloat(name: string): number;
        putMeta(name: string, val: string): void;
        data(): ArrayBuffer;
        dataAsReader(): CodecReader;
        dataAsString(): string;
        dataSize(): number;
        release(): void;
    }
}
declare module "socketd/transport/core/Typealias" {
    export type IoConsumer<T> = (t: T) => void;
    export type IoBiConsumer<T1, T2> = (t1: T1, t2: T2) => void;
    export type IoFunction<T1, T2> = (t1: T1) => T2;
}
declare module "socketd/exception/SocketdException" {
    import type { Message } from "socketd/transport/core/Message";
    /**
     * 异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdException extends Error {
        constructor(message: any);
    }
    /**
     * 告警异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdAlarmException extends SocketdException {
        private _from;
        constructor(from: Message);
        getFrom(): Message;
    }
    /**
     * 通道异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdChannelException extends SocketdException {
        constructor(message: any);
    }
    /**
     * 编码异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdCodecException extends SocketdException {
        constructor(message: any);
    }
    /**
     * 连接异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdConnectionException extends SocketdException {
        constructor(message: any);
    }
    /**
     * 大小限制异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdSizeLimitException extends SocketdException {
        constructor(message: any);
    }
    /**
     * 超时异常
     *
     * @author noear
     * @since 2.0
     */
    export class SocketdTimeoutException extends SocketdException {
        constructor(message: any);
    }
}
declare module "socketd/transport/core/Stream" {
    import type { Reply } from "socketd/transport/core/Entity";
    import type { MessageInternal } from "socketd/transport/core/Message";
    import type { Channel } from "socketd/transport/core/Channel";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import type { Config } from "socketd/transport/core/Config";
    /**
     * 流
     *
     * @author noear
     * @since 2.1
     */
    export interface Stream {
        /**
         * 流Id
         */
        sid(): string;
        /**
         * 是否单收
         */
        isSingle(): boolean;
        /**
         * 是否完成
         */
        isDone(): boolean;
        /**
         * 超时设定（单位：毫秒）
         */
        timeout(): number;
        /**
         * 异常发生时
         */
        thenError(onError: IoConsumer<Error>): Stream;
    }
    /**
     * 流内部接口
     *
     * @author noear
     * @since 2.0
     */
    export interface StreamInternal extends Stream {
        /**
         * 保险开始（避免永久没有回调，造成内存不能释放）
         * */
        insuranceStart(streamManger: StreamMangerDefault, streamTimeout: number): any;
        /**
         * 保险取消息
         * */
        insuranceCancel(): any;
        /**
         * 接收时
         *
         * @param reply   答复
         * @param channel 通道
         */
        onAccept(reply: MessageInternal, channel: Channel): any;
        /**
         * 异常时
         *
         * @param error 异常
         */
        onError(error: Error): any;
    }
    /**
     * 流基类
     *
     * @author noear
     * @since 2.0
     */
    export abstract class StreamBase implements StreamInternal {
        private _insuranceFuture;
        private _sid;
        private _isSingle;
        private _timeout;
        private _doOnError;
        constructor(sid: string, isSingle: boolean, timeout: number);
        abstract onAccept(reply: MessageInternal, channel: Channel): any;
        abstract isDone(): boolean;
        sid(): string;
        isSingle(): boolean;
        timeout(): number;
        /**
         * 保险开始（避免永久没有回调，造成内存不能释放）
         *
         * @param streamManger  流管理器
         * @param streamTimeout 流超时
         */
        insuranceStart(streamManger: StreamMangerDefault, streamTimeout: number): void;
        /**
         * 保险取消息
         */
        insuranceCancel(): void;
        /**
         * 异常时
         *
         * @param error 异常
         */
        onError(error: Error): void;
        thenError(onError: IoConsumer<Error>): Stream;
    }
    /**
     * 请求流
     *
     * @author noear
     * @since 2.0
     */
    export class StreamRequest extends StreamBase implements StreamInternal {
        _future: IoConsumer<Reply>;
        _isDone: boolean;
        constructor(sid: string, timeout: number, future: IoConsumer<Reply>);
        isDone(): boolean;
        onAccept(reply: MessageInternal, channel: Channel): void;
    }
    /**
     * 订阅流
     *
     * @author noear
     * @since 2.0
     */
    export class StreamSubscribe extends StreamBase implements StreamInternal {
        _future: IoConsumer<Reply>;
        constructor(sid: string, timeout: number, future: IoConsumer<Reply>);
        isDone(): boolean;
        onAccept(reply: MessageInternal, channel: Channel): void;
    }
    /**
     * 流管理器
     *
     * @author noear
     * @since 2.0
     */
    export interface StreamManger {
        /**
         * 添加流
         *
         * @param sid    流Id
         * @param stream 流
         */
        addStream(sid: string, stream: StreamInternal): any;
        /**
         * 获取流
         *
         * @param sid 流Id
         */
        getStream(sid: string): StreamInternal | undefined;
        /**
         * 移除流
         *
         * @param sid 流Id
         */
        removeStream(sid: string): any;
    }
    export class StreamMangerDefault implements StreamManger {
        _config: Config;
        _streamMap: Map<string, StreamInternal>;
        constructor(config: Config);
        /**
         * 获取流接收器
         *
         * @param sid 流Id
         */
        getStream(sid: string): StreamInternal | undefined;
        /**
         * 添加流接收器
         *
         * @param sid    流Id
         * @param stream 流
         */
        addStream(sid: any, stream: StreamInternal): void;
        /**
         * 移除流接收器
         *
         * @param sid 流Id
         */
        removeStream(sid: any): void;
    }
}
declare module "socketd/transport/core/IdGenerator" {
    export interface IdGenerator {
        /**
         * 生成
         */
        generate(): string;
    }
    export class GuidGenerator implements IdGenerator {
        generate(): string;
    }
}
declare module "socketd/transport/core/Frame" {
    import { Message, MessageInternal } from "socketd/transport/core/Message";
    /**
     * 帧（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    export class Frame {
        private _flag;
        private _message;
        constructor(flag: number, message: MessageInternal | null);
        /**
         * 标志（保持与 Message 的获取风格）
         * */
        flag(): number;
        /**
         * 消息
         * */
        message(): MessageInternal | null;
        toString(): string;
    }
    /**
     * 帧工厂
     *
     * @author noear
     * @since 2.0
     * */
    export class Frames {
        /**
         * 构建连接帧
         *
         * @param sid 流Id
         * @param url 连接地址
         */
        static connectFrame(sid: string, url: string): Frame;
        /**
         * 构建连接确认帧
         *
         * @param connectMessage 连接消息
         */
        static connackFrame(connectMessage: Message): Frame;
        /**
         * 构建 ping 帧
         */
        static pingFrame(): Frame;
        /**
         * 构建 pong 帧
         */
        static pongFrame(): Frame;
        /**
         * 构建关闭帧（一般用不到）
         */
        static closeFrame(): Frame;
        /**
         * 构建告警帧（一般用不到）
         */
        static alarmFrame(from: Message, alarm: string): Frame;
    }
}
declare module "socketd/transport/core/FragmentHolder" {
    import type { MessageInternal } from "socketd/transport/core/Message";
    export class FragmentHolder {
        private _index;
        private _message;
        constructor(index: number, message: MessageInternal);
        /**
         * 获取顺序位
         */
        getIndex(): number;
        /**
         * 获取分片帧
         */
        getMessage(): MessageInternal;
    }
}
declare module "socketd/transport/core/FragmentAggregator" {
    import { MessageInternal } from "socketd/transport/core/Message";
    import { Frame } from "socketd/transport/core/Frame";
    /**
     * 分片聚合器
     *
     * @author noear
     * @since 2.1
     */
    export interface FragmentAggregator {
        /**
         * 获取流Id
         */
        getSid(): string;
        /**
         * 数据流大小
         */
        getDataStreamSize(): number;
        /**
         * 数据总长度
         */
        getDataLength(): number;
        /**
         * 添加分片
         */
        add(index: number, message: MessageInternal): any;
        /**
         * 获取聚合帧
         */
        get(): Frame;
    }
    /**
     * 分片聚合器
     *
     * @author noear
     * @since 2.0
     */
    export class FragmentAggregatorDefault implements FragmentAggregator {
        private _main;
        private _fragmentHolders;
        private _dataStreamSize;
        private _dataLength;
        constructor(main: MessageInternal);
        /**
         * 获取消息流Id（用于消息交互、分片）
         */
        getSid(): string;
        /**
         * 数据流大小
         */
        getDataStreamSize(): number;
        /**
         * 数据总长度
         */
        getDataLength(): number;
        /**
         * 添加帧
         */
        add(index: number, message: MessageInternal): void;
        /**
         * 获取聚合后的帧
         */
        get(): Frame;
    }
}
declare module "socketd/transport/core/FragmentHandler" {
    import type { Channel } from "socketd/transport/core/Channel";
    import type { Frame } from "socketd/transport/core/Frame";
    import type { MessageInternal } from "socketd/transport/core/Message";
    import { Entity } from "socketd/transport/core/Entity";
    import type { CodecReader } from "socketd/transport/core/Codec";
    /**
     * 数据分片处理（分片必须做，聚合可开关）
     *
     * @author noear
     * @since 2.0
     */
    export interface FragmentHandler {
        /**
         * 获取下个分片
         *
         * @param channel       通道
         * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
         * @param message       总包消息
         */
        nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity | null;
        /**
         * 聚合所有分片
         *
         * @param channel       通道
         * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
         * @param message       分片消息
         */
        aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame | null;
        /**
         * 聚合启用
         */
        aggrEnable(): boolean;
    }
    /**
     * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
     *
     * @author noear
     * @since 2.0
     */
    export class FragmentHandlerDefault implements FragmentHandler {
        /**
         * 获取下个分片
         *
         * @param channel       通道
         * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
         * @param message       总包消息
         */
        nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity | null;
        /**
         * 聚合所有分片
         *
         * @param channel       通道
         * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
         * @param message       分片消息
         */
        aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame | null;
        aggrEnable(): boolean;
        readFragmentData(ins: CodecReader, maxSize: number): ArrayBuffer;
    }
}
declare module "socketd/transport/core/Config" {
    import { Codec } from "socketd/transport/core/Codec";
    import type { StreamManger } from "socketd/transport/core/Stream";
    import { IdGenerator } from "socketd/transport/core/IdGenerator";
    import { FragmentHandler } from "socketd/transport/core/FragmentHandler";
    /**
     * 配置接口
     *
     * @author noear
     * @since 2.0
     */
    export interface Config {
        /**
         * 是否客户端模式
         */
        clientMode(): boolean;
        /**
         * 获取流管理器
         */
        getStreamManger(): StreamManger;
        /**
         * 获取角色名
         */
        getRoleName(): string;
        /**
         * 获取字符集
         */
        getCharset(): string;
        /**
         * 获取编解码器
         */
        getCodec(): Codec;
        /**
         * 获取Id生成器
         */
        getIdGenerator(): IdGenerator;
        /**
         * 获取分片处理器
         */
        getFragmentHandler(): FragmentHandler;
        /**
         * 获取分片大小
         */
        getFragmentSize(): number;
        /**
         * 核心线程数（第二优先）
         */
        getCoreThreads(): number;
        /**
         * 最大线程数
         */
        getMaxThreads(): number;
        /**
         * 获取读缓冲大小
         */
        getReadBufferSize(): number;
        /**
         * 配置读缓冲大小
         */
        getWriteBufferSize(): number;
        /**
         * 获取连接空闲超时（单位：毫秒）
         */
        getIdleTimeout(): number;
        /**
         * 获取请求超时（单位：毫秒）
         */
        getRequestTimeout(): number;
        /**
         * 获取消息流超时（单位：毫秒）
         */
        getStreamTimeout(): number;
        /**
         * 允许最大UDP包大小
         */
        getMaxUdpSize(): number;
    }
    export abstract class ConfigBase implements Config {
        private _clientMode;
        private _streamManger;
        private _codec;
        private _idGenerator;
        private _fragmentHandler;
        private _fragmentSize;
        protected _charset: string;
        protected _coreThreads: number;
        protected _maxThreads: number;
        protected _readBufferSize: number;
        protected _writeBufferSize: number;
        protected _idleTimeout: number;
        protected _requestTimeout: number;
        protected _streamTimeout: number;
        protected _maxUdpSize: number;
        constructor(clientMode: boolean);
        /**
         * 是否客户端模式
         */
        clientMode(): boolean;
        /**
         * 获取流管理器
         */
        getStreamManger(): StreamManger;
        /**
         * 获取角色名
         * */
        getRoleName(): string;
        /**
         * 获取字符集
         */
        getCharset(): string;
        /**
         * 配置字符集
         */
        charset(charset: string): this;
        /**
         * 获取编解码器
         */
        getCodec(): Codec;
        /**
         * 获取标识生成器
         */
        getIdGenerator(): IdGenerator;
        /**
         * 配置标识生成器
         */
        idGenerator(idGenerator: IdGenerator): this;
        /**
         * 获取分片处理
         */
        getFragmentHandler(): FragmentHandler;
        /**
         * 配置分片处理
         */
        fragmentHandler(fragmentHandler: FragmentHandler): this;
        /**
         * 获取分片大小
         */
        getFragmentSize(): number;
        /**
         * 配置分片大小
         */
        fragmentSize(fragmentSize: number): this;
        /**
         * 获取核心线程数
         */
        getCoreThreads(): number;
        /**
         * 配置核心线程数
         */
        coreThreads(coreThreads: number): this;
        /**
         * 获取最大线程数
         */
        getMaxThreads(): number;
        /**
         * 配置最大线程数
         */
        maxThreads(maxThreads: number): this;
        /**
         * 获取读缓冲大小
         */
        getReadBufferSize(): number;
        /**
         * 配置读缓冲大小
         */
        readBufferSize(readBufferSize: number): this;
        /**
         * 获取写缓冲大小
         */
        getWriteBufferSize(): number;
        /**
         * 配置写缓冲大小
         */
        writeBufferSize(writeBufferSize: number): this;
        /**
         * 配置连接空闲超时
         */
        getIdleTimeout(): number;
        /**
         * 配置连接空闲超时
         */
        idleTimeout(idleTimeout: number): this;
        /**
         * 配置请求默认超时
         */
        getRequestTimeout(): number;
        /**
         * 配置请求默认超时
         */
        requestTimeout(requestTimeout: number): this;
        /**
         * 获取消息流超时（单位：毫秒）
         * */
        getStreamTimeout(): number;
        /**
         * 配置消息流超时（单位：毫秒）
         * */
        streamTimeout(streamTimeout: number): this;
        /**
         * 获取允许最大UDP包大小
         */
        getMaxUdpSize(): number;
        /**
         * 配置允许最大UDP包大小
         */
        maxUdpSize(maxUdpSize: number): this;
        /**
         * 生成 id
         * */
        generateId(): string;
    }
}
declare module "socketd/transport/core/Handshake" {
    import type { MessageInternal } from "socketd/transport/core/Message";
    /**
     * 握手信息
     *
     * @author noear
     * @since 2.0
     */
    export interface Handshake {
        /**
         * 协议版本
         */
        version(): string | null;
        /**
         * 获请传输地址
         *
         * @return tcp://192.168.0.1/path?user=1&path=2
         */
        uri(): URL;
        /**
         * 获取参数集合
         */
        paramMap(): Map<string, string>;
        /**
         * 获取参数
         *
         * @param name 参数名
         */
        param(name: string): string | undefined;
        /**
         * 获取参数或默认值
         *
         * @param name 参数名
         * @param def  默认值
         */
        paramOrDefault(name: string, def: string): string;
        /**
         * 设置或修改参数
         */
        paramPut(name: string, value: string): any;
    }
    /**
     * @author noear
     * @since 2.0
     */
    export interface HandshakeInternal extends Handshake {
        /**
         * 获取消息源
         */
        getSource(): MessageInternal;
    }
}
declare module "socketd/transport/core/Channel" {
    import type { Session } from "socketd/transport/core/Session";
    import type { Config } from "socketd/transport/core/Config";
    import type { HandshakeInternal } from "socketd/transport/core/Handshake";
    import type { Message } from "socketd/transport/core/Message";
    import type { Frame } from "socketd/transport/core/Frame";
    import type { StreamInternal } from "socketd/transport/core/Stream";
    import type { IoBiConsumer } from "socketd/transport/core/Typealias";
    /**
     * 通道
     *
     * @author noear
     * @since 2.0
     */
    export interface Channel {
        /**
         * 获取附件
         */
        getAttachment<T>(name: string): T | null;
        /**
         * 放置附件
         */
        putAttachment(name: string, val: object | null): any;
        /**
         * 是否有效
         */
        isValid(): any;
        /**
         * 是否已关闭
         */
        isClosed(): number;
        /**
         * 关闭（1协议关，2用户关）
         */
        close(code: number): any;
        /**
         * 获取配置
         */
        getConfig(): Config;
        /**
         * 设置握手信息
         *
         * @param handshake 握手信息
         */
        setHandshake(handshake: HandshakeInternal): any;
        /**
         * 获取握手信息
         */
        getHandshake(): HandshakeInternal;
        /**
         * 发送连接（握手）
         *
         * @param url 连接地址
         */
        sendConnect(url: string): any;
        /**
         * 发送连接确认（握手）
         *
         * @param connectMessage 连接消息
         */
        sendConnack(connectMessage: Message): any;
        /**
         * 发送 Ping（心跳）
         */
        sendPing(): any;
        /**
         * 发送 Pong（心跳）
         */
        sendPong(): any;
        /**
         * 发送 Close
         */
        sendClose(): any;
        /**
         * 发送告警
         */
        sendAlarm(from: Message, alarm: string): any;
        /**
         * 发送
         *
         * @param frame  帧
         * @param stream 流（没有则为 null）
         */
        send(frame: Frame, stream: StreamInternal | null): any;
        /**
         * 接收（接收答复帧）
         *
         * @param frame 帧
         */
        retrieve(frame: Frame): any;
        /**
         * 手动重连（一般是自动）
         */
        reconnect(): any;
        /**
         * 出错时
         */
        onError(error: any): any;
        /**
         * 获取会话
         */
        getSession(): Session;
    }
    /**
     * 通道内部扩展
     *
     * @author noear
     * @since 2.0
     */
    export interface ChannelInternal extends Channel {
        /**
         * 设置会话
         * */
        setSession(session: Session): any;
        /**
         * 当打开时
         * */
        onOpenFuture(future: IoBiConsumer<boolean, any>): any;
        /**
         * 执行打开时
         * */
        doOpenFuture(r: boolean, e: any): any;
    }
    export abstract class ChannelBase implements Channel {
        protected _config: Config;
        private _attachments;
        private _handshake;
        private _isClosed;
        constructor(config: Config);
        getAttachment<T>(name: string): T | null;
        putAttachment(name: string, val: object | null): void;
        abstract isValid(): any;
        isClosed(): number;
        close(code: number): void;
        getConfig(): Config;
        setHandshake(handshake: HandshakeInternal): void;
        getHandshake(): HandshakeInternal;
        sendConnect(url: string): void;
        sendConnack(connectMessage: Message): void;
        sendPing(): void;
        sendPong(): void;
        sendClose(): void;
        sendAlarm(from: Message, alarm: string): void;
        abstract send(frame: Frame, stream: StreamInternal | null): any;
        abstract retrieve(frame: Frame): any;
        abstract reconnect(): any;
        abstract onError(error: any): any;
        abstract getSession(): Session;
    }
}
declare module "socketd/transport/core/Asserts" {
    import type { Channel } from "socketd/transport/core/Channel";
    /**
     * 断言
     *
     * @author noear
     * @since 2.0
     */
    export class Asserts {
        /**
         * 断言关闭
         */
        static assertClosed(channel: Channel | null): void;
        /**
         * 断言关闭
         */
        static assertClosedByUser(channel: Channel | null): void;
        /**
         * 断言 null
         */
        static assertNull(name: string, val: any): void;
        /**
         * 断言 empty
         */
        static assertEmpty(name: string, val: string): void;
        /**
         * 断言 size
         */
        static assertSize(name: string, size: number, limitSize: number): void;
    }
}
declare module "socketd/transport/core/Codec" {
    import { Frame } from "socketd/transport/core/Frame";
    import type { Config } from "socketd/transport/core/Config";
    import type { IoFunction } from "socketd/transport/core/Typealias";
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
        getBytes(dst: ArrayBuffer, offset: number, length: number): any;
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
        /**
         * 长度
         * */
        size(): number;
        /**
         * 重置索引
         * */
        reset(): any;
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
        putBytes(src: ArrayBuffer): any;
        /**
         * 推入 int
         */
        putInt(val: number): any;
        /**
         * 推入 char
         */
        putChar(val: number): any;
        /**
         * 冲刷
         */
        flush(): any;
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
        read(buffer: CodecReader): Frame | null;
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
        private _config;
        constructor(config: Config);
        /**
         * 解码写入
         *
         * @param frame         帧
         * @param targetFactory 目标工厂
         */
        write<T extends CodecWriter>(frame: Frame, targetFactory: IoFunction<number, T>): T;
        /**
         * 编码读取
         *
         * @param buffer 缓冲
         */
        read(buffer: CodecReader): Frame | null;
        protected decodeString(reader: CodecReader, buf: ArrayBuffer, maxLen: number): string;
    }
    export class ArrayBufferCodecReader implements CodecReader {
        _buf: ArrayBuffer;
        _bufView: DataView;
        _bufViewIdx: number;
        constructor(buf: ArrayBuffer);
        getByte(): number;
        getBytes(dst: ArrayBuffer, offset: number, length: number): void;
        getInt(): number;
        remaining(): number;
        position(): number;
        size(): number;
        reset(): void;
    }
    export class ArrayBufferCodecWriter implements CodecWriter {
        _buf: ArrayBuffer;
        _bufView: DataView;
        _bufViewIdx: number;
        constructor(n: number);
        putBytes(src: ArrayBuffer): void;
        putInt(val: number): void;
        putChar(val: number): void;
        flush(): void;
        getBuffer(): ArrayBuffer;
    }
}
declare module "socketd/transport/core/Entity" {
    import { CodecReader } from "socketd/transport/core/Codec";
    /**
     * 消息实体（帧[消息[实体]]）
     *
     * @author noear
     * @since 2.0
     */
    export interface Entity {
        /**
         * at
         *
         * @since 2.1
         */
        at(): any;
        /**
         * 获取元信息字符串（queryString style）
         */
        metaString(): string;
        /**
         * 获取元信息字典
         */
        metaMap(): URLSearchParams;
        /**
         * 获取元信息
         */
        meta(name: string): string | null;
        /**
         * 获取元信息或默认
         */
        metaOrDefault(name: string, def: string): string;
        /**
         * 获取元信息并转为 int
         */
        metaAsInt(name: string): number;
        /**
         * 获取元信息并转为 float
         */
        metaAsFloat(name: string): number;
        /**
         * 添加元信息
         * */
        putMeta(name: string, val: string): any;
        /**
         * 获取数据
         */
        data(): ArrayBuffer;
        /**
         * 获取数据并转为读取器
         */
        dataAsReader(): CodecReader;
        /**
         * 获取数据并转为字符串
         */
        dataAsString(): string;
        /**
         * 获取数据长度
         */
        dataSize(): number;
        /**
         * 释放资源
         */
        release(): any;
    }
    /**
     * 答复实体
     *
     * @author noear
     * @since 2.1
     */
    export interface Reply extends Entity {
        /**
         * 流Id
         */
        sid(): string;
        /**
         * 是否答复结束
         */
        isEnd(): boolean;
    }
    /**
     * 实体默认实现
     *
     * @author noear
     * @since 2.0
     */
    export class EntityDefault implements Entity {
        private _metaMap;
        private _data;
        private _dataAsReader;
        constructor();
        /**
         * At
         * */
        at(): string | null;
        /**
         * 设置元信息字符串
         * */
        metaStringSet(metaString: string): EntityDefault;
        /**
         * 放置元信息字典
         *
         * @param map 元信息字典
         */
        metaMapPut(map: any): EntityDefault;
        /**
         * 放置元信息
         *
         * @param name 名字
         * @param val  值
         */
        metaPut(name: string, val: string): EntityDefault;
        /**
         * 获取元信息字符串（queryString style）
         */
        metaString(): string;
        /**
         * 获取元信息字典
         */
        metaMap(): URLSearchParams;
        /**
         * 获取元信息
         *
         * @param name 名字
         */
        meta(name: string): string | null;
        /**
         * 获取元信息或默认值
         *
         * @param name 名字
         * @param def  默认值
         */
        metaOrDefault(name: string, def: string): string;
        /**
         * 获取元信息并转为 int
         */
        metaAsInt(name: string): number;
        /**
         * 获取元信息并转为 float
         */
        metaAsFloat(name: string): number;
        /**
         * 放置元信息
         *
         * @param name 名字
         * @param val  值
         */
        putMeta(name: string, val: string): void;
        /**
         * 设置数据
         *
         * @param data 数据
         */
        dataSet(data: ArrayBuffer): EntityDefault;
        /**
         * 获取数据（若多次复用，需要reset）
         */
        data(): ArrayBuffer;
        dataAsReader(): CodecReader;
        /**
         * 获取数据并转成字符串
         */
        dataAsString(): string;
        /**
         * 获取数据长度
         */
        dataSize(): number;
        /**
         * 释放资源
         */
        release(): void;
        toString(): string;
    }
    /**
     * 字符串实体
     *
     * @author noear
     * @since 2.0
     */
    export class StringEntity extends EntityDefault implements Entity {
        constructor(data: string);
    }
    export class FileEntity extends EntityDefault implements Entity {
        private _file;
        constructor(file: File);
        load(): Promise<FileEntity>;
    }
}
declare module "socketd/transport/client/ClientSession" {
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import type { Stream } from "socketd/transport/core/Stream";
    import type { Entity, Reply } from "socketd/transport/core/Entity";
    /**
     * 客户会话
     *
     * @author noear
     */
    export interface ClientSession {
        /**
         * 是否有效
         */
        isValid(): boolean;
        /**
         * 获取会话Id
         */
        sessionId(): string;
        /**
         * 手动重连（一般是自动）
         */
        reconnect(): any;
        /**
         * 发送
         *
         * @param event   事件
         * @param content 内容
         */
        send(event: string, content: Entity): any;
        /**
         * 发送并请求（限为一次答复；指定回调）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout  超时（毫秒）
         * @return 流
         */
        sendAndRequest(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout?: number): Stream;
        /**
         * 发送并订阅（答复结束之前，不限答复次数）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout  超时（毫秒）
         * @return 流
         */
        sendAndSubscribe(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout?: number): Stream;
        /**
         * 关闭
         * */
        close(): any;
    }
}
declare module "socketd/transport/core/Session" {
    import type { Entity, Reply } from "socketd/transport/core/Entity";
    import type { Message } from "socketd/transport/core/Message";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import type { Channel } from "socketd/transport/core/Channel";
    import type { Stream } from "socketd/transport/core/Stream";
    import type { ClientSession } from "socketd/transport/client/ClientSession";
    import type { Handshake } from "socketd/transport/core/Handshake";
    /**
     * 会话
     *
     * @author noear
     * @since 2.0
     */
    export interface Session extends ClientSession {
        /**
         * 获取握手信息
         */
        handshake(): Handshake;
        /**
         * broker player name
         *
         * @since 2.1
         */
        name(): string | undefined;
        /**
         * 获取握手参数
         *
         * @param name 名字
         */
        param(name: string): string | undefined;
        /**
         * 获取握手参数或默认值
         *
         * @param name 名字
         * @param def  默认值
         */
        paramOrDefault(name: string, def: string): string;
        /**
         * 获取握手路径
         */
        path(): string;
        /**
         * 设置握手新路径
         */
        pathNew(pathNew: string): any;
        /**
         * 获取所有属性
         */
        attrMap(): Map<string, any>;
        /**
         * 是有属性
         *
         * @param name 名字
         */
        attrHas(name: string): any;
        /**
         * 获取属性
         *
         * @param name 名字
         */
        attr(name: string): any;
        /**
         * 获取属性或默认值
         *
         * @param name 名字
         * @param def  默认值
         */
        attrOrDefault(name: string, def: object): object;
        /**
         * 设置属性
         *
         * @param name  名字
         * @param val 值
         */
        attrPut(name: string, val: object): any;
        /**
         * 手动发送 Ping（一般是自动）
         */
        sendPing(): any;
        /**
         * 发送告警
         */
        sendAlarm(from: Message, alarm: string): any;
        /**
         * 答复
         *
         * @param from    来源消息
         * @param content 内容
         */
        reply(from: Message, content: Entity): any;
        /**
         * 答复并结束（即最后一次答复）
         *
         * @param from    来源消息
         * @param content 内容
         */
        replyEnd(from: Message, content: Entity): any;
    }
    /**
     * 会话基类
     *
     * @author noear
     */
    export abstract class SessionBase implements Session {
        protected _channel: Channel;
        private _sessionId;
        private _attrMap;
        constructor(channel: Channel);
        sessionId(): string;
        name(): string | undefined;
        attrMap(): Map<string, any>;
        attrHas(name: string): boolean;
        attr(name: string): any;
        attrOrDefault(name: string, def: object): object;
        attrPut(name: string, val: object): void;
        abstract handshake(): Handshake;
        abstract param(name: string): string | undefined;
        abstract paramOrDefault(name: string, def: string): string;
        abstract path(): string;
        abstract pathNew(pathNew: string): any;
        abstract sendPing(): any;
        abstract sendAlarm(from: Message, alarm: string): any;
        abstract reply(from: Message, entity: Entity): any;
        abstract replyEnd(from: Message, entity: Entity): any;
        abstract isValid(): boolean;
        abstract reconnect(): any;
        abstract send(event: string, content: Entity): any;
        abstract sendAndRequest(event: string, content: Entity, callback: IoConsumer<Reply>, timeout?: number): Stream;
        abstract sendAndSubscribe(event: string, content: Entity, callback: IoConsumer<Reply>, timeout?: number): Stream;
        abstract close(): any;
        protected generateId(): string;
    }
}
declare module "socketd/transport/core/RouteSelector" {
    /**
     * 路径映射器
     *
     * @author noear
     * @since 2.0
     */
    export interface RouteSelector<T> {
        /**
         * 选择
         *
         * @param route 路由
         */
        select(route: string): T | undefined;
        /**
         * 放置
         *
         * @param route  路由
         * @param target 目标
         */
        put(route: string, target: T): any;
        /**
         * 移除
         */
        remove(route: string): any;
        /**
         * 数量
         */
        size(): number;
    }
    /**
     * 路径映射器默认实现（哈希）
     *
     * @author noear
     * @since 2.0
     */
    export class RouteSelectorDefault<T> implements RouteSelector<T> {
        private _inner;
        /**
         * 选择
         *
         * @param route 路由
         */
        select(route: string): T | undefined;
        /**
         * 放置
         *
         * @param route  路由
         * @param target 目标
         */
        put(route: string, target: T): void;
        /**
         * 移除
         *
         * @param route 路由
         */
        remove(route: string): void;
        /**
         * 数量
         */
        size(): number;
    }
}
declare module "socketd/transport/core/Listener" {
    import type { Session } from "socketd/transport/core/Session";
    import type { Message } from "socketd/transport/core/Message";
    import type { IoBiConsumer, IoConsumer } from "socketd/transport/core/Typealias";
    import { RouteSelector } from "socketd/transport/core/RouteSelector";
    /**
     * 监听器
     *
     * @author noear
     * @since 2.0
     */
    export interface Listener {
        /**
         * 打开时
         *
         * @param session 会话
         */
        onOpen(session: Session): any;
        /**
         * 收到消息时
         *
         * @param session 会话
         * @param message 消息
         */
        onMessage(session: Session, message: Message): any;
        /**
         * 关闭时
         *
         * @param session 会话
         */
        onClose(session: Session): any;
        /**
         * 出错时
         *
         * @param session 会话
         * @param error   错误信息
         */
        onError(session: Session, error: any): any;
    }
    /**
     * 简单监听器（一般用于占位）
     *
     * @author noear
     * @since 2.0
     */
    export class SimpleListener implements Listener {
        onOpen(session: Session): void;
        onMessage(session: Session, message: Message): void;
        onClose(session: Session): void;
        onError(session: Session, error: Error): void;
    }
    /**
     * 事件监听器（根据消息事件路由）
     *
     * @author noear
     * @since 2.0
     */
    export class EventListener implements Listener {
        private _doOnOpen;
        private _doOnMessage;
        private _doOnClose;
        private _doOnError;
        private _eventRouteSelector;
        constructor(routeSelector?: RouteSelector<IoBiConsumer<Session, Message>>);
        doOn(event: string, consumer: IoBiConsumer<Session, Message>): EventListener;
        doOnOpen(consumer: IoConsumer<Session>): EventListener;
        doOnMessage(consumer: IoBiConsumer<Session, Message>): EventListener;
        doOnClose(consumer: IoConsumer<Session>): EventListener;
        doOnError(consumer: IoBiConsumer<Session, Error>): EventListener;
        onOpen(session: Session): void;
        onMessage(session: Session, message: Message): void;
        onClose(session: Session): void;
        onError(session: Session, error: Error): void;
    }
    /**
     * 路径监听器（根据握手地址路由，一般用于服务端）
     *
     * @author noear
     * @since 2.0
     */
    export class PathListener implements Listener {
        /**
         * 路径路由选择器
         * */
        protected _pathRouteSelector: RouteSelector<Listener>;
        constructor(routeSelector?: RouteSelector<Listener>);
        /**
         * 路由
         */
        of(path: string, listener: Listener): PathListener;
        /**
         * 数量（二级监听器的数据）
         */
        size(): number;
        onOpen(session: Session): void;
        onMessage(session: Session, message: Message): void;
        onClose(session: Session): void;
        onError(session: Session, error: Error): void;
    }
    /**
     * 管道监听器
     *
     * @author noear
     * @since 2.0
     */
    export class PipelineListener implements Listener {
        protected _deque: Listener[];
        /**
         * 前一个
         */
        prev(listener: Listener): PipelineListener;
        /**
         * 后一个
         */
        next(listener: Listener): PipelineListener;
        /**
         * 数量（二级监听器的数据）
         * */
        size(): number;
        /**
         * 打开时
         *
         * @param session 会话
         */
        onOpen(session: Session): void;
        /**
         * 收到消息时
         *
         * @param session 会话
         * @param message 消息
         */
        onMessage(session: Session, message: Message): void;
        /**
         * 关闭时
         *
         * @param session 会话
         */
        onClose(session: Session): void;
        /**
         * 出错时
         *
         * @param session 会话
         * @param error   错误信息
         */
        onError(session: Session, error: Error): void;
    }
}
declare module "socketd/transport/client/ClientConfig" {
    import { ConfigBase } from "socketd/transport/core/Config";
    export class ClientConfig extends ConfigBase {
        private _schema;
        private _linkUrl;
        private _url;
        private _uri;
        private _port;
        private _heartbeatInterval;
        private _connectTimeout;
        private _autoReconnect;
        constructor(url: string);
        /**
         * 获取通讯架构（tcp, ws, udp）
         */
        getSchema(): string;
        /**
         * 获取连接地址
         */
        getUrl(): string;
        /**
         * 获取连接地址
         */
        getUri(): URL;
        /**
         * 获取链接地址
         */
        getLinkUrl(): string;
        /**
         * 获取连接主机
         */
        getHost(): string;
        /**
         * 获取连接端口
         */
        getPort(): number;
        /**
         * 获取心跳间隔（单位毫秒）
         */
        getHeartbeatInterval(): number;
        /**
         * 配置心跳间隔（单位毫秒）
         */
        heartbeatInterval(heartbeatInterval: number): ClientConfig;
        /**
         * 获取连接超时（单位毫秒）
         */
        getConnectTimeout(): number;
        /**
         * 配置连接超时（单位毫秒）
         */
        connectTimeout(connectTimeout: number): this;
        /**
         * 获取是否自动重链
         */
        isAutoReconnect(): boolean;
        /**
         * 配置是否自动重链
         */
        autoReconnect(autoReconnect: boolean): this;
        idleTimeout(idleTimeout: number): this;
        toString(): string;
    }
}
declare module "socketd/transport/core/HandshakeDefault" {
    import type { HandshakeInternal } from "socketd/transport/core/Handshake";
    import type { MessageInternal } from "socketd/transport/core/Message";
    export class HandshakeDefault implements HandshakeInternal {
        private _source;
        private _url;
        private _version;
        private _paramMap;
        constructor(source: MessageInternal);
        getSource(): MessageInternal;
        param(name: string): string | undefined;
        paramMap(): Map<string, string>;
        paramOrDefault(name: string, def: string): string;
        paramPut(name: string, value: string): void;
        uri(): URL;
        version(): string | null;
    }
}
declare module "socketd/transport/core/Processor" {
    import { Listener } from "socketd/transport/core/Listener";
    import type { ChannelInternal } from "socketd/transport/core/Channel";
    import type { Message } from "socketd/transport/core/Message";
    import type { Frame } from "socketd/transport/core/Frame";
    /**
     * 处理器
     *
     * @author noear
     * @since 2.0
     */
    export interface Processor {
        /**
         * 设置监听器
         */
        setListener(listener: Listener): any;
        /**
         * 接收处理
         */
        onReceive(channel: ChannelInternal, frame: Frame): any;
        /**
         * 打开时
         *
         * @param channel 通道
         */
        onOpen(channel: ChannelInternal): any;
        /**
         * 收到消息时
         *
         * @param channel 通道
         * @param message 消息
         */
        onMessage(channel: ChannelInternal, message: Message): any;
        /**
         * 关闭时
         *
         * @param channel 通道
         */
        onClose(channel: ChannelInternal): any;
        /**
         * 出错时
         *
         * @param channel 通道
         * @param error   错误信息
         */
        onError(channel: ChannelInternal, error: Error): any;
    }
    export class ProcessorDefault implements Processor {
        private _listener;
        constructor();
        setListener(listener: Listener): void;
        onReceive(channel: ChannelInternal, frame: any): void;
        onReceiveDo(channel: ChannelInternal, frame: any, isReply: any): void;
        onOpen(channel: ChannelInternal): void;
        onMessage(channel: ChannelInternal, message: any): void;
        onClose(channel: ChannelInternal): void;
        onCloseInternal(channel: ChannelInternal): void;
        onError(channel: ChannelInternal, error: any): void;
    }
}
declare module "socketd/transport/core/ChannelAssistant" {
    import type { Frame } from "socketd/transport/core/Frame";
    /**
     * 通道助理
     *
     * @author noear
     * @since 2.0
     */
    export interface ChannelAssistant<T> {
        /**
         * 写入
         *
         * @param target 目标
         * @param frame  帧
         */
        write(target: T, frame: Frame): any;
        /**
         * 是否有效
         */
        isValid(target: T): boolean;
        /**
         * 关闭
         */
        close(target: T): any;
        /**
         * 获取远程地址
         */
        getRemoteAddress(target: T): string;
        /**
         * 获取本地地址
         */
        getLocalAddress(target: T): string;
    }
}
declare module "socketd/transport/client/ClientConnector" {
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import type { Session } from "socketd/transport/core/Session";
    import type { ChannelInternal } from "socketd/transport/core/Channel";
    import type { ClientInternal } from "socketd/transport/client/Client";
    /**
     * 客户端连接器
     *
     * @author noear
     * @since 2.0
     */
    export interface ClientConnector {
        /**
         * 心跳处理
         */
        heartbeatHandler(): IoConsumer<Session>;
        /**
         * 心跳频率（单位：毫秒）
         */
        heartbeatInterval(): number;
        /**
         * 是否自动重连
         */
        autoReconnect(): boolean;
        /**
         * 连接
         *
         * @return 通道
         */
        connect(): Promise<ChannelInternal>;
        /**
         * 关闭
         */
        close(): any;
    }
    /**
     * 客户端连接器基类
     *
     * @author noear
     * @since 2.0
     */
    export abstract class ClientConnectorBase<T extends ClientInternal> implements ClientConnector {
        protected _client: T;
        constructor(client: T);
        heartbeatHandler(): IoConsumer<Session>;
        heartbeatInterval(): number;
        autoReconnect(): boolean;
        abstract connect(): Promise<ChannelInternal>;
        abstract close(): any;
    }
}
declare module "socketd/transport/core/HeartbeatHandler" {
    import type { Session } from "socketd/transport/core/Session";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    export interface HeartbeatHandler {
        heartbeat(session: Session): any;
    }
    export class HeartbeatHandlerDefault implements HeartbeatHandler {
        private _heartbeatHandler;
        constructor(heartbeatHandler: IoConsumer<Session> | null);
        heartbeat(session: Session): void;
    }
}
declare module "socketd/utils/RunUtils" {
    export class RunUtils {
        static runAndTry(fun: any): void;
    }
}
declare module "socketd/transport/client/ClientChannel" {
    import { Channel, ChannelBase } from "socketd/transport/core/Channel";
    import type { Frame } from "socketd/transport/core/Frame";
    import type { Session } from "socketd/transport/core/Session";
    import type { StreamInternal } from "socketd/transport/core/Stream";
    import type { ClientConnector } from "socketd/transport/client/ClientConnector";
    /**
     * 客户端通道
     *
     * @author noear
     * @since 2.0
     */
    export class ClientChannel extends ChannelBase implements Channel {
        private _connector;
        private _real;
        private _heartbeatHandler;
        private _heartbeatScheduledFuture;
        constructor(real: Channel, connector: ClientConnector);
        /**
         * 初始化心跳（关闭后，手动重链时也会用到）
         */
        initHeartbeat(): void;
        /**
         * 心跳处理
         */
        heartbeatHandle(): Promise<void>;
        /**
         * 预备检测
         *
         * @return 是否为新链接
         */
        prepareCheck(): Promise<boolean>;
        /**
         * 是否有效
         */
        isValid(): any;
        /**
         * 是否已关闭
         */
        isClosed(): number;
        /**
         * 发送
         *
         * @param frame  帧
         * @param stream 流（没有则为 null）
         */
        send(frame: Frame, stream: StreamInternal): Promise<void>;
        retrieve(frame: Frame): void;
        reconnect(): void;
        onError(error: any): void;
        close(code: number): void;
        getSession(): Session;
    }
}
declare module "socketd/transport/core/SessionDefault" {
    import { SessionBase } from "socketd/transport/core/Session";
    import type { Channel } from "socketd/transport/core/Channel";
    import type { Handshake } from "socketd/transport/core/Handshake";
    import type { Entity, Reply } from "socketd/transport/core/Entity";
    import { Message } from "socketd/transport/core/Message";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import { Stream } from "socketd/transport/core/Stream";
    /**
     * 会话默认实现
     *
     * @author noear
     * @since 2.0
     */
    export class SessionDefault extends SessionBase {
        private _pathNew;
        constructor(channel: Channel);
        isValid(): boolean;
        handshake(): Handshake;
        /**
         * 获取握手参数
         *
         * @param name 名字
         */
        param(name: string): string | undefined;
        /**
         * 获取握手参数或默认值
         *
         * @param name 名字
         * @param def  默认值
         */
        paramOrDefault(name: string, def: string): string;
        /**
         * 获取路径
         */
        path(): string;
        /**
         * 设置新路径
         */
        pathNew(pathNew: string): void;
        /**
         * 手动重连（一般是自动）
         */
        reconnect(): void;
        /**
         * 手动发送 Ping（一般是自动）
         */
        sendPing(): void;
        sendAlarm(from: Message, alarm: string): void;
        /**
         * 发送
         */
        send(event: string, content: Entity): void;
        /**
         * 发送并请求（限为一次答复；指定超时）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout 超时
         */
        sendAndRequest(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream;
        /**
         * 发送并订阅（答复结束之前，不限答复次数）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout 超时
         */
        sendAndSubscribe(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream;
        /**
         * 答复
         *
         * @param from    来源消息
         * @param content 内容
         */
        reply(from: Message, content: Entity): void;
        /**
         * 答复并结束（即最后一次答复）
         *
         * @param from    来源消息
         * @param content 内容
         */
        replyEnd(from: Message, content: Entity): void;
        /**
         * 关闭
         */
        close(): void;
    }
}
declare module "socketd/transport/client/Client" {
    import type { Listener } from "socketd/transport/core/Listener";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import type { ClientSession } from "socketd/transport/client/ClientSession";
    import type { ClientConfig } from "socketd/transport/client/ClientConfig";
    import { Processor } from "socketd/transport/core/Processor";
    import type { ChannelAssistant } from "socketd/transport/core/ChannelAssistant";
    import type { Session } from "socketd/transport/core/Session";
    import type { ClientConnector } from "socketd/transport/client/ClientConnector";
    /**
     * 客户端（用于构建会话）
     *
     * @author noear
     * @since 2.0
     */
    export interface Client {
        /**
         * 心跳
         */
        heartbeatHandler(handler: IoConsumer<Session>): any;
        /**
         * 配置
         */
        config(configHandler: IoConsumer<ClientConfig>): any;
        /**
         * 监听
         */
        listen(listener: Listener): Client;
        /**
         * 打开会话
         */
        open(): Promise<ClientSession>;
    }
    /**
     * 客户端内部扩展接口
     *
     * @author noear
     * @since  2.1
     */
    export interface ClientInternal extends Client {
        /**
         * 获取心跳处理
         */
        getHeartbeatHandler(): IoConsumer<Session>;
        /**
         * 获取心跳间隔（毫秒）
         */
        getHeartbeatInterval(): number;
        /**
         * 获取配置
         */
        getConfig(): ClientConfig;
        /**
         * 获取处理器
         */
        getProcessor(): Processor;
    }
    /**
     * 客户端基类
     *
     * @author noear
     * @since 2.0
     */
    export abstract class ClientBase<T extends ChannelAssistant<Object>> implements ClientInternal {
        private _config;
        private _heartbeatHandler;
        private _processor;
        private _assistant;
        constructor(clientConfig: ClientConfig, assistant: T);
        /**
         * 获取通道助理
         */
        getAssistant(): T;
        /**
         * 获取心跳处理
         */
        getHeartbeatHandler(): IoConsumer<Session>;
        /**
         * 获取心跳间隔（毫秒）
         */
        getHeartbeatInterval(): number;
        /**
         * 获取配置
         */
        getConfig(): ClientConfig;
        /**
         * 获取处理器
         */
        getProcessor(): Processor;
        /**
         * 设置心跳
         */
        heartbeatHandler(handler: IoConsumer<Session>): this;
        /**
         * 配置
         */
        config(configHandler: IoConsumer<ClientConfig>): this;
        /**
         * 设置监听器
         */
        listen(listener: Listener): Client;
        /**
         * 打开会话
         */
        open(): Promise<ClientSession>;
        /**
         * 创建连接器
         */
        protected abstract createConnector(): ClientConnector;
    }
}
declare module "socketd/transport/client/ClientProvider" {
    import type { Client } from "socketd/transport/client/Client";
    import type { ClientConfig } from "socketd/transport/client/ClientConfig";
    /**
     * 客户端工厂
     *
     * @author noear
     * @since 2.0
     */
    export interface ClientProvider {
        /**
         * 协议架构
         */
        schemas(): string[];
        /**
         * 创建客户端
         */
        createClient(clientConfig: ClientConfig): Client;
    }
}
declare module "socketd/cluster/ClusterClientSession" {
    import type { ClientSession } from "socketd/transport/client/ClientSession";
    import type { Entity, Reply } from "socketd/transport/core/Entity";
    import type { Stream } from "socketd/transport/core/Stream";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    /**
     * 集群客户端会话
     *
     * @author noear
     * @since 2.1
     */
    export class ClusterClientSession implements ClientSession {
        private _sessionSet;
        private _sessionRoundCounter;
        private _sessionId;
        constructor(sessions: ClientSession[]);
        /**
         * 获取所有会话
         */
        getSessionAll(): ClientSession[];
        /**
         * 获取一个会话（轮询负栽均衡）
         */
        getSessionOne(): ClientSession;
        isValid(): boolean;
        sessionId(): string;
        reconnect(): void;
        /**
         * 发送
         *
         * @param event   事件
         * @param content 内容
         */
        send(event: string, content: Entity): void;
        /**
         * 发送并请求（限为一次答复；指定回调）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout  超时
         */
        sendAndRequest(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream;
        /**
         * 发送并订阅（答复结束之前，不限答复次数）
         *
         * @param event    事件
         * @param content  内容
         * @param consumer 回调消费者
         * @param timeout  超时
         */
        sendAndSubscribe(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream;
        /**
         * 关闭
         */
        close(): void;
    }
}
declare module "socketd/cluster/ClusterClient" {
    import type { Client } from "socketd/transport/client/Client";
    import type { ClientConfig } from "socketd/transport/client/ClientConfig";
    import type { ClientSession } from "socketd/transport/client/ClientSession";
    import type { Listener } from "socketd/transport/core/Listener";
    import type { Session } from "socketd/transport/core/Session";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    /**
     * 集群客户端
     *
     * @author noear
     */
    export class ClusterClient implements Client {
        private _serverUrls;
        private _heartbeatHandler;
        private _configHandler;
        private _listener;
        constructor(serverUrls: string[]);
        heartbeatHandler(heartbeatHandler: IoConsumer<Session>): Client;
        /**
         * 配置
         */
        config(configHandler: IoConsumer<ClientConfig>): Client;
        /**
         * 监听
         */
        listen(listener: Listener): Client;
        /**
         * 打开
         */
        open(): Promise<ClientSession>;
    }
}
declare module "socketd_websocket/WsChannelAssistant" {
    import type { ChannelAssistant } from "socketd/transport/core/ChannelAssistant";
    import type { Frame } from "socketd/transport/core/Frame";
    import type { Config } from "socketd/transport/core/Config";
    export class WsChannelAssistant implements ChannelAssistant<WebSocket> {
        _config: Config;
        constructor(config: Config);
        read(buffer: ArrayBuffer): Frame | null;
        write(target: WebSocket, frame: Frame): void;
        isValid(target: WebSocket): boolean;
        close(target: WebSocket): void;
        getRemoteAddress(target: WebSocket): string;
        getLocalAddress(target: WebSocket): string;
    }
}
declare module "socketd/transport/client/ClientHandshakeResult" {
    import type { ChannelInternal } from "socketd/transport/core/Channel";
    /**
     * 客户端握手结果
     *
     * @author noear
     * @since 2.0
     */
    export class ClientHandshakeResult {
        private _channel;
        private _throwable;
        constructor(channel: ChannelInternal, throwable: any);
        getChannel(): ChannelInternal;
        getThrowable(): any;
    }
}
declare module "socketd/transport/core/ChannelSupporter" {
    import type { Processor } from "socketd/transport/core/Processor";
    import type { Config } from "socketd/transport/core/Config";
    import type { ChannelAssistant } from "socketd/transport/core/ChannelAssistant";
    /**
     * 通道支持者（创建通道的参数）
     *
     * @author noear
     * @since 2.1
     */
    export interface ChannelSupporter<S> {
        /**
         * 处理器
         */
        getProcessor(): Processor;
        /**
         * 配置
         */
        getConfig(): Config;
        /**
         * 通道助理
         */
        getAssistant(): ChannelAssistant<S>;
    }
}
declare module "socketd/transport/core/ChannelDefault" {
    import type { StreamInternal } from "socketd/transport/core/Stream";
    import type { Session } from "socketd/transport/core/Session";
    import type { ChannelSupporter } from "socketd/transport/core/ChannelSupporter";
    import type { Config } from "socketd/transport/core/Config";
    import { Frame } from "socketd/transport/core/Frame";
    import { ChannelBase, ChannelInternal } from "socketd/transport/core/Channel";
    import type { IoBiConsumer } from "socketd/transport/core/Typealias";
    export class ChannelDefault<S> extends ChannelBase implements ChannelInternal {
        private _source;
        private _processor;
        private _assistant;
        private _streamManger;
        private _session;
        private _onOpenFuture;
        constructor(source: S, supporter: ChannelSupporter<S>);
        onOpenFuture(future: IoBiConsumer<boolean, Error>): void;
        doOpenFuture(r: boolean, e: Error): void;
        isValid(): boolean;
        config(): Config;
        sendPing(): void;
        sendPong(): void;
        send(frame: Frame, stream: StreamInternal | null): void;
        retrieve(frame: Frame): void;
        reconnect(): void;
        onError(error: Error): void;
        getSession(): Session;
        setSession(session: Session): void;
        close(code: any): void;
    }
}
declare module "socketd_websocket/impl/WebSocketClientImpl" {
    import type { WsClient } from "socketd_websocket/WsClient";
    import type { IoConsumer } from "socketd/transport/core/Typealias";
    import { ClientHandshakeResult } from "socketd/transport/client/ClientHandshakeResult";
    import type { ChannelInternal } from "socketd/transport/core/Channel";
    export class WebSocketClientImpl {
        _real: WebSocket;
        _client: WsClient;
        _channel: ChannelInternal;
        _handshakeFuture: IoConsumer<ClientHandshakeResult>;
        constructor(url: string, client: WsClient, handshakeFuture: IoConsumer<ClientHandshakeResult>);
        onOpen(e: Event): void;
        onMessage(e: MessageEvent): void;
        onClose(e: CloseEvent): void;
        onError(e: any): void;
        close(): void;
    }
}
declare module "socketd_websocket/WsClientConnector" {
    import { ClientConnectorBase } from "socketd/transport/client/ClientConnector";
    import type { ChannelInternal } from "socketd/transport/core/Channel";
    import type { WsClient } from "socketd_websocket/WsClient";
    import { WebSocketClientImpl } from "socketd_websocket/impl/WebSocketClientImpl";
    export class WsClientConnector extends ClientConnectorBase<WsClient> {
        _real: WebSocketClientImpl;
        constructor(client: WsClient);
        connect(): Promise<ChannelInternal>;
        close(): void;
    }
}
declare module "socketd_websocket/WsClient" {
    import { ClientBase } from "socketd/transport/client/Client";
    import type { ClientConfig } from "socketd/transport/client/ClientConfig";
    import { WsChannelAssistant } from "socketd_websocket/WsChannelAssistant";
    import type { ClientConnector } from "socketd/transport/client/ClientConnector";
    import type { ChannelSupporter } from "socketd/transport/core/ChannelSupporter";
    export class WsClient extends ClientBase<WsChannelAssistant> implements ChannelSupporter<WebSocket> {
        constructor(clientConfig: ClientConfig);
        protected createConnector(): ClientConnector;
    }
}
declare module "socketd_websocket/WsClientProvider" {
    import type { Client } from "socketd/transport/client/Client";
    import type { ClientConfig } from "socketd/transport/client/ClientConfig";
    import type { ClientProvider } from "socketd/transport/client/ClientProvider";
    export class WsClientProvider implements ClientProvider {
        schemas(): string[];
        createClient(clientConfig: ClientConfig): Client;
    }
}
declare module "socketd/SocketD" {
    import type { Client } from "socketd/transport/client/Client";
    import type { ClientProvider } from "socketd/transport/client/ClientProvider";
    import { ClusterClient } from "socketd/cluster/ClusterClient";
    import { EntityDefault, FileEntity, StringEntity } from "socketd/transport/core/Entity";
    import { EventListener, Listener, PathListener, PipelineListener, SimpleListener } from "socketd/transport/core/Listener";
    import type { RouteSelector } from "socketd/transport/core/RouteSelector";
    import type { IoBiConsumer } from "socketd/transport/core/Typealias";
    import type { Session } from "socketd/transport/core/Session";
    import type { Message } from "socketd/transport/core/Message";
    export class SocketD {
        /**
         * 框架版本号
         */
        static version(): string;
        /**
         * 协议版本号
         */
        static protocolVersion(): string;
        static clientProviderMap: Map<String, ClientProvider>;
        /**
         * 创建客户端（支持 url 自动识别）
         *
         * @param serverUrl 服务器地址
         */
        static createClient(serverUrl: string): Client;
        /**
         * 创建客户端（支持 url 自动识别），如果没有则为 null
         *
         * @param serverUrl 服务器地址
         */
        static createClientOrNull(serverUrl: string): Client | null;
        /**
         * 创建集群客户端
         *
         * @param serverUrls 服务端地址
         */
        static createClusterClient(serverUrls: string[]): ClusterClient;
    }
    /**
     * 框架版本号
     */
    export function version(): string;
    /**
     * 协议版本号
     */
    export function protocolVersion(): string;
    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    export function createClient(serverUrl: string): Client;
    /**
     * 创建客户端（支持 url 自动识别），如果没有则为 null
     *
     * @param serverUrl 服务器地址
     */
    export function createClientOrNull(serverUrl: string): Client | null;
    /**
     * 创建集群客户端
     *
     * @param serverUrls 服务端地址
     */
    export function createClusterClient(serverUrls: string[]): ClusterClient;
    /**
     * 创建实体
     * */
    export function newEntity(): EntityDefault;
    /**
     * 创建字符串实体
     * */
    export function newStringEntity(data: string): StringEntity;
    /**
     * 创建文件实体
     * */
    export function newFileEntity(file: File): FileEntity;
    /**
     * 创建简单临听器
     * */
    export function newSimpleListener(): SimpleListener;
    /**
     * 创建事件监听器
     * */
    export function newEventListener(routeSelector?: RouteSelector<IoBiConsumer<Session, Message>>): EventListener;
    /**
     * 创建路径监听器（一般用于服务端）
     * */
    export function newPathListener(routeSelector?: RouteSelector<Listener>): PathListener;
    /**
     * 创建管道监听器
     * */
    export function newPipelineListener(): PipelineListener;
    /**
     * 元信息字典
     * */
    export const Metas: {
        META_SOCKETD_VERSION: string;
        META_DATA_LENGTH: string;
        META_DATA_TYPE: string;
        META_DATA_FRAGMENT_IDX: string;
        META_DATA_DISPOSITION_FILENAME: string; /**
         * 创建文件实体
         * */
        META_RANGE_START: string;
        META_RANGE_SIZE: string;
    };
}
