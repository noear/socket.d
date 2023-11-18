/**
 * let connentor = new ClientConnector(this.config);
 *  return new Session(new ClientChannel(connentor.connect(), connentor));
 * */
import {Client} from "./transport/client/Client";
import {ClientConfig} from "./transport/client/ClientConfig";
import {Entity} from "./transport/core/Entity";
import {WsClient} from "./impl_ws/WsClient";


export const SocketD = {
    createClient(url): Client {
        return new WsClient(new ClientConfig(url));
    }
}
