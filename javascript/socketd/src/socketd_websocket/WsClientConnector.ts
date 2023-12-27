import {ClientConnectorBase} from "../socketd/transport/client/ClientConnector";
import { ChannelInternal } from "../socketd/transport/core/Channel";
import {WsClient} from "./WsClient";
import {WebSocketClientImpl} from "./impl/WebSocketClientImpl";
import {ClientHandshakeResult} from "../socketd/transport/client/ClientHandshakeResult";

export class WsClientConnector extends ClientConnectorBase<WsClient> {
    _real: WebSocketClientImpl;

    constructor(client: WsClient) {
        super(client);
    }

    connect(): ChannelInternal {
        //关闭之前的资源
        this.close();

        //处理自定义架构的影响（重连时，新建实例比原生重链接口靠谱）
        let url = this._client.getConfig().getUrl();

        let handshakeResult: ClientHandshakeResult;

        this._real = new WebSocketClientImpl(url, this._client, (r) => {
            handshakeResult = r;
        });

        //异步转同步（估计得改）
        while (!handshakeResult);

        return handshakeResult.getChannel();
    }


    close() {
        if (this._real) {
            this._real.close();
        }
    }
}