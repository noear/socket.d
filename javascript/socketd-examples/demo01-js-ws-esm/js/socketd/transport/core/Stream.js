import { SocketdTimeoutException } from "../../exception/SocketdException";
import { Asserts } from "./Asserts";
/**
 * 流基类
 *
 * @author noear
 * @since 2.0
 */
export class StreamBase {
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
            this.onError(new SocketdTimeoutException("The stream response timeout, sid=" + this.sid()));
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
/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
export class StreamRequest extends StreamBase {
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
/**
 * 订阅流
 *
 * @author noear
 * @since 2.0
 */
export class StreamSubscribe extends StreamBase {
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
export class StreamMangerDefault {
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
        Asserts.assertNull("stream", stream);
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
