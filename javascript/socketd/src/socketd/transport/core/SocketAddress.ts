export class SocketAddress {
    address: string;
    family: string;
    port: number;

    constructor(address: string, family: string, port: number) {
        this.address = address;
        this.family = family;
        this.port = port;
    }
}