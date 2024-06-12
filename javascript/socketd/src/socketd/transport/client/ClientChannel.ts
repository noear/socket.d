import {Channel, ChannelInternal} from "../core/Channel";
import type { Frame } from "../core/Frame";
import type { Session } from "../core/Session";
import type { StreamInternal } from "../stream/Stream";
import type { ClientConnector } from "./ClientConnector";
import { Constants } from "../core/Constants";
import { Asserts } from "../core/Asserts";
import { SocketDChannelException, SocketDException } from "../../exception/SocketDException";
import {RunUtils} from "../../utils/RunUtils";
import {SessionDefault} from "../core/SessionDefault";
import {SocketAddress} from "../core/SocketAddress";
import {ClientHeartbeatHandler, ClientHeartbeatHandlerDefault} from "./ClientHeartbeatHandler";
import {ClientInternal} from "./Client";
import {ClientConnectHandler, ClientConnectHandlerDefault} from "./ClientConnectHandler";
import {ChannelBase} from "../core/impl/ChannelBase";

/**
 * 客户端通道
 *
 * @author noear
 * @since 2.0
 */
export class ClientChannel extends ChannelBase implements Channel {
    //客户端
    private _client: ClientInternal;
    //连接器
    private _connector: ClientConnector;
    //会话壳
    private _sessionShell: Session;
    //真实通道
    private _real: ChannelInternal | null;
    //连接处理
    private _connectHandler : ClientConnectHandler;
    //心跳处理
    private _heartbeatHandler: ClientHeartbeatHandler;
    //心跳调度
    private _heartbeatScheduledFuture: any;
    //连接状态
    private _isConnecting: boolean = false;

    constructor(client: ClientInternal, connector: ClientConnector) {
        super(connector.getConfig());
        this._client = client;
        this._connector = connector;
        this._sessionShell = new SessionDefault(this);

        this._connectHandler = new ClientConnectHandlerDefault(client.getConnectHandler());
        this._heartbeatHandler = new ClientHeartbeatHandlerDefault(client.getHeartbeatHandler());

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
            this._heartbeatScheduledFuture = setInterval(async () => {
                try {
                    await this.heartbeatHandle();
                } catch (e) {
                    console.debug("Client channel heartbeat failed: {link=" + this._connector.getConfig().getLinkUrl() + "}");
                }
            }, this._client.getHeartbeatInterval());
        }
    }

    /**
     * 心跳处理
     */
    async heartbeatHandle() {
        if (this._real) {
            //说明握手未成
            if (this._real.getHandshake() == null) {
                return;
            }

            //关闭并结束了
            if (Asserts.isClosedAndEnd(this._real)) {
                console.debug(`Client channel is closed (pause heartbeat), sessionId=${this.getSession().sessionId()}`);
                //可能是被内层的会话关闭的，跳过了外层
                this.close(this._real.closeCode());
                return;
            }

            //或者正在关闭中
            if (this._real.isClosing()) {
                return;
            }
        }

        try {
            await this.internalCheck();

            this._heartbeatHandler.clientHeartbeat(this.getSession());
        } catch (e) {
            if (e instanceof SocketDException) {
                throw e;
            }

            if (this._connector.autoReconnect()) {
                this.internalCloseIfError();
            }

            throw e;
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
     * 是否关闭中
     */
    isClosing() {
        if (this._real == null) {
            return false;
        } else {
            return this._real.isClosing();
        }
    }

    /**
     * 是否已关闭
     */
    closeCode(): number {
        if (this._real == null) {
            return 0;
        } else {
            return this._real.closeCode();
        }
    }

    getLiveTime(): number {
        if (this._real) {
            return this._real!.getLiveTime();
        } else {
            return 0;
        }
    }

    getRemoteAddress(): SocketAddress | null {
        if (this._real) {
            return  this._real.getRemoteAddress();
        } else {
            return null;
        }
    }

    getLocalAddress(): SocketAddress | null {
        if (this._real) {
            return  this._real.getLocalAddress();
        } else {
            return null;
        }
    }

    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    send(frame: Frame, stream: StreamInternal<any>) {
        Asserts.assertClosedAndEnd(this._real);

        this.internalCheck().then(res => {
            if (this._real) {
                try {
                    this._real!.send(frame, stream);
                } catch (err) {
                    if (stream) {
                        // @ts-ignore
                        stream.onError(err);
                    }
                }
            } else {
                //有可能此时仍未连接
                const err = new SocketDChannelException("Client channel is not connected");
                if (stream) {
                    stream.onError(err);
                }
            }
        }, err => {
            if (this._connector.autoReconnect()) {
                this.internalCloseIfError();
            }

            if (stream) {
                stream.onError(err);
            }
        });
    }

    async reconnect() {
        this.initHeartbeat();

        await this.internalCheck();
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
        return this._sessionShell;
    }

    async connect() {
        if (this._isConnecting) {
            return;
        } else {
            this._isConnecting = true;
        }

        try {
            if (this._real != null) {
                this._real.close(Constants.CLOSE2002_RECONNECT);
            }

            this._real = await this._connectHandler.clientConnect(this._connector);
            //原始 session 切换为带壳的 session
            this._real.setSession(this._sessionShell);
            //同步握手信息
            this.setHandshake(this._real.getHandshake());
        } finally {
            this._isConnecting = false;
        }
    }

    private internalCloseIfError() {
        if (this._real != null) {
            this._real.close(Constants.CLOSE2001_ERROR);
            this._real = null;
        }
    }

    /**
     * 预备检测
     *
     * @return 是否为新链接
     */
    private async internalCheck(): Promise<boolean> {
        if (this._real == null || this._real.isValid() == false) {
            await this.connect();

            return true;
        } else {
            return false;
        }
    }
}
