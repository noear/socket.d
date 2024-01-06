import { Channel, ChannelBase } from "../core/Channel";
import type { Frame } from "../core/Frame";
import type { Session } from "../core/Session";
import type { StreamInternal } from "../core/Stream";
import type { ClientConnector } from "./ClientConnector";
import { HeartbeatHandler, HeartbeatHandlerDefault} from "../core/HeartbeatHandler";
import { Constants } from "../core/Constants";
import { Asserts } from "../core/Asserts";
import { SocketdChannelException, SocketdException } from "../../exception/SocketdException";
import {RunUtils} from "../../utils/RunUtils";

/**
 * 客户端通道
 *
 * @author noear
 * @since 2.0
 */
export class ClientChannel extends ChannelBase implements Channel {
    private _connector: ClientConnector;
    private _real: Channel | null;
    private  _heartbeatHandler: HeartbeatHandler;
    private _heartbeatScheduledFuture: any;

    constructor(real: Channel, connector: ClientConnector) {
        super(real.getConfig());

        this._connector = connector;
        this._real = real;

        if (connector.heartbeatHandler() == null) {
            this._heartbeatHandler = new HeartbeatHandlerDefault(null);
        } else {
            this._heartbeatHandler = new HeartbeatHandlerDefault(connector.heartbeatHandler());
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
                } catch (e) {
                    console.warn("Client channel heartbeat error", e);
                }
            }, this._connector.heartbeatInterval());
        }
    }

    /**
     * 心跳处理
     */
    async heartbeatHandle() {
        if (this._real != null) {
            //说明握手未成
            if (this._real.getHandshake() == null) {
                return;
            }

            //手动关闭
            if (this._real.isClosed() == Constants.CLOSE4_USER) {
                console.debug(`Client channel is closed (pause heartbeat), sessionId=${this.getSession().sessionId()}`);
                return;
            }
        }

        try {
            await this.prepareCheck();

            this._heartbeatHandler.heartbeat(this.getSession());
        } catch (e) {
            if (e instanceof SocketdException) {
                throw e;
            }

            if (this._connector.autoReconnect()) {
                this._real!.close(Constants.CLOSE3_ERROR);
                this._real = null;
            }

            throw new SocketdChannelException(e);
        }
    }

    /**
     * 预备检测
     *
     * @return 是否为新链接
     */
    async prepareCheck(): Promise<boolean> {
        if (this._real == null || this._real.isValid() == false) {
            this._real = await this._connector.connect();

            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否有效
     */
    isValid() {
        if (this._real == null) {
            return false;
        } else {
            return this._real.isValid();
        }
    }

    /**
     * 是否已关闭
     */
    isClosed(): number {
        if (this._real == null) {
            return 0;
        } else {
            return this._real.isClosed();
        }
    }

    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    async send(frame: Frame, stream: StreamInternal<any>) {
        Asserts.assertClosedByUser(this._real);

        try {
            await this.prepareCheck();

            this._real!.send(frame, stream);
        } catch (e) {
            if (this._connector.autoReconnect()) {
                this._real!.close(Constants.CLOSE3_ERROR);
                this._real = null;
            }

            throw e;
        }
    }

    retrieve(frame: Frame, stream: StreamInternal<any> | null) {
        this._real!.retrieve(frame, stream);
    }

    reconnect() {
        this.initHeartbeat();

        this.prepareCheck();
    }

    onError(error: any) {
        this._real!.onError(error);
    }

    close(code: number) {
        RunUtils.runAndTry(() => clearInterval(this._heartbeatScheduledFuture));
        RunUtils.runAndTry(() => this._connector.close());
        if (this._real) {
            RunUtils.runAndTry(() => this._real!.close(code));
        }
        super.close(code);
    }

    getSession(): Session {
        return this._real!.getSession();
    }
}
