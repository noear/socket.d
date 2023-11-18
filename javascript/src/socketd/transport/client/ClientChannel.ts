import {Channel} from "../core/Channel";
import {ClientConnector} from "./ClientConnector";
import {ClientConfig} from "./ClientConfig";
import {Session} from "../core/Session";
import {Frame} from "../core/Frame";


export class ClientChannel implements Channel {
    real: Channel
    connector: ClientConnector
    config: ClientConfig;

    constructor(channel: Channel, connector: ClientConnector) {
        this.real = channel;
        this.connector = connector;
    }

    open(): Session {
        return null;
    }

    send(frame: Frame): void {
    }

    getHandshake(): object {
        return undefined;
    }

    setHandshake(handshake: object): void {
    }

    setSession(): Session {
        return undefined;
    }

    getSession(): Session {
        return undefined;
    }
}