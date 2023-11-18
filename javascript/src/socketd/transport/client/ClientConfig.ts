export class ClientConfig {
    readonly url: string
    schema?: string
    replyTimeout?: number

    constructor(url: string) {
        this.url = url;
    }
}