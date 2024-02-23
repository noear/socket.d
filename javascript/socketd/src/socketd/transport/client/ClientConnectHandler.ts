import {ClientConnector} from "./ClientConnector";
import {ChannelInternal} from "../core/Channel";
import {IoFunction} from "../core/Typealias";

/**
 * 客户端连接处理器
 *
 * @author noear
 * @since 2.4
 */
export interface ClientConnectHandler {
    /**
     * 连接
     *
     * @param connector 连接器
     */
    clientConnect(connector: ClientConnector): Promise<ChannelInternal>;
}

export class ClientConnectHandlerDefault implements ClientConnectHandler {
    private _connectHandler: IoFunction<ClientConnector, Promise<ChannelInternal>> | null;

    constructor(connectHandler: IoFunction<ClientConnector, Promise<ChannelInternal>> | null) {
        this._connectHandler = connectHandler;
    }

    clientConnect(connector: ClientConnector): Promise<ChannelInternal> {
        if (this._connectHandler) {
            return this._connectHandler(connector);
        } else {
            return connector.connect();
        }
    }
}