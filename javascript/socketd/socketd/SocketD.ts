import {Client} from "./transport/client/Client";


export class SocketD {
    static protocolVersion(): string {
        return "1.0";
    }

    static createClient(serverUrl: string): Client {
        return null;
    }
}