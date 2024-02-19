import type {Client} from "../transport/client/Client";
import type { ClientConfig } from "../transport/client/ClientConfig";
import type { ClientSession } from "../transport/client/ClientSession";
import type { Listener } from "../transport/core/Listener";
import type { Session } from "../transport/core/Session";
import type {IoConsumer, IoFunction} from "../transport/core/Typealias";
import {ClusterClientSession} from "./ClusterClientSession";
import {SocketD} from "../SocketD";
import type {ClientConnector} from "../transport/client/ClientConnector";
import {ChannelInternal} from "../transport/core/Channel";

/**
 * 集群客户端
 *
 * @author noear
 */
export class ClusterClient implements Client {
    private _serverUrls: string[];

    private _connectHandler : IoFunction<ClientConnector, Promise<ChannelInternal>>;
    private _heartbeatHandler: IoConsumer<Session>;
    private _configHandler: IoConsumer<ClientConfig>;
    private _listener: Listener;

    constructor(serverUrls: string[] | string) {
        if (serverUrls instanceof Array) {
            this._serverUrls = serverUrls;
        } else {
            this._serverUrls = [serverUrls];
        }
    }

    connectHandler(connectHandler: IoFunction<ClientConnector, Promise<ChannelInternal>>) {
        this._connectHandler = connectHandler;
        return this;
    }

    heartbeatHandler(heartbeatHandler: IoConsumer<Session>): Client {
        this._heartbeatHandler = heartbeatHandler;
        return this;
    }

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ClientConfig>): Client {
        this._configHandler = configHandler;
        return this;
    }

    /**
     * 监听
     */
    listen(listener: Listener): Client {
        this._listener = listener;
        return this;
    }

    async open(): Promise<ClientSession> {
        return this.openDo(false);
    }

    /**
     * 打开
     */
    async openOrThow(): Promise<ClientSession> {
        return this.openDo(true);
    }

    async openDo(isThow: boolean): Promise<ClientSession> {
        const sessionList = new Array<ClientSession>();

        for (const urls of this._serverUrls) {
            for (let url of urls.split(",")) {
                url = url.trim();
                if (!url) {
                    continue;
                }

                const client = SocketD.createClient(url);

                if (this._listener) {
                    client.listen(this._listener);
                }

                if (this._configHandler) {
                    client.config(this._configHandler);
                }

                if (this._connectHandler) {
                    client.connectHandler(this._connectHandler);
                }

                if (this._heartbeatHandler) {
                    client.heartbeatHandler(this._heartbeatHandler);
                }

                if (isThow) {
                    sessionList.push(await client.openOrThow());
                } else {
                    sessionList.push(await client.open());
                }

            }
        }

        return new ClusterClientSession(sessionList);
    }
}
