import {Client} from "../transport/client/Client";
import { ClientConfig } from "../transport/client/ClientConfig";
import { ClientSession } from "../transport/client/ClientSession";
import { Listener } from "../transport/core/Listener";
import { Session } from "../transport/core/Session";
import { IoConsumer } from "../transport/core/Types";
import {SocketD} from "../SocketD";
import {ClusterClientSession} from "./ClusterClientSession";

/**
 * 集群客户端
 *
 * @author noear
 */
export class ClusterClient implements Client {
    private _serverUrls: string[];

    private _heartbeatHandler: IoConsumer<Session>;
    private _configHandler: IoConsumer<ClientConfig>;
    private _listener: Listener;

    constructor(serverUrls: string[]) {
        this._serverUrls = serverUrls;
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

    /**
     * 打开
     */
    async open(): Promise<ClientSession> {
        let sessionList = new ClusterClient[this._serverUrls.length];

        for (let urls of this._serverUrls) {
            for (let url of urls.split(",")) {
                url = url.trim();
                if (!url) {
                    continue;
                }

                let client = SocketD.createClient(url);

                if (this._listener != null) {
                    client.listen(this._listener);
                }

                if (this._configHandler != null) {
                    client.config(this._configHandler);
                }

                if (this._heartbeatHandler != null) {
                    client.heartbeatHandler(this._heartbeatHandler);
                }

                let session = await client.open();
                sessionList.add(session);
            }
        }

        return new ClusterClientSession(sessionList);
    }
}
