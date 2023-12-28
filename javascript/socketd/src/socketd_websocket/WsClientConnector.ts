import {ClientConnectorBase} from "../socketd/transport/client/ClientConnector";
import { ChannelInternal } from "../socketd/transport/core/Channel";
import {WsClient} from "./WsClient";
import {WebSocketClientImpl} from "./impl/WebSocketClientImpl";

export class WsClientConnector extends ClientConnectorBase<WsClient> {
    _real: WebSocketClientImpl;

    constructor(client: WsClient) {
        super(client);
    }

    connect(): Promise<ChannelInternal> {
        //关闭之前的资源
        this.close();

        //处理自定义架构的影响（重连时，新建实例比原生重链接口靠谱）
        let url = this._client.getConfig().getUrl();


        return new Promise<ChannelInternal>((resolve, reject) => {
            this._real = new WebSocketClientImpl(url, this._client, (r) => {
                if (r.getThrowable()) {
                    reject(r.getThrowable());
                } else {
                    resolve(r.getChannel());
                }
            });
        })
    }


    close() {
        if (this._real) {
            this._real.close();
        }
    }
}