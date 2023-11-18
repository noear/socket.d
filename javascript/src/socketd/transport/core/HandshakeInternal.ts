import {Handshake} from "./Handshake";
import {Message} from "./Message";

/**
 * 握手信息内部实现类
 *
 * @author noear
 * @since 2.0
 */
export class HandshakeInternal implements Handshake {
    _source: Message

    /**
     * 消息源
     */
    getSource(): Message {
        return this._source;
    }

    constructor(source: Message) {
        this._source = source;
    }

    param(name: string): string {
        return "";
    }

    // @ts-ignore
    paramMap(): Map<string, string> {
        return undefined;
    }

    paramOrDefault(name: string, def: string): string {
        return "";
    }

    paramSet(name: string, val: string): void {
    }

    version(): string {
        return "";
    }

}