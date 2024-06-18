import type {Config} from "../Config";
import type {HandshakeInternal} from "../Handshake";
import {Constants} from "../Constants";
import {type Frame} from "../Frame";
import type {Message} from "../Message";
import {SocketAddress} from "../SocketAddress";
import type {StreamInternal} from "../../stream/Stream";
import type {Session} from "../Session";
import {Channel} from "../Channel";
import {Frames} from "./Frames";
import {Entity} from "../Entity";

export abstract class  ChannelBase implements Channel {
    protected _config: Config;
    private _attachments: Map<string, any>;
    private _handshake: HandshakeInternal;

    constructor(config: Config) {
        this._config = config;
        this._attachments = new Map<string, object>();
    }

    abstract getLiveTime(): number;

    getAttachment<T>(name: string): T | null {
        return this._attachments.get(name);
    }

    putAttachment(name: string, val: object | null) {
        if (val == null) {
            this._attachments.delete(name);
        } else {
            this._attachments.set(name, val);
        }
    }

    abstract isValid(): boolean;

    abstract isClosing(): boolean;

    abstract closeCode(): number;

    close(code: number) {
        if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this._attachments.clear();
        }
    }


    getConfig(): Config {
        return this._config;
    }

    setHandshake(handshake: HandshakeInternal) {
        this._handshake = handshake;
    }

    getHandshake(): HandshakeInternal {
        return this._handshake;
    }

    sendConnect(url: string, metaMap: Map<string, string>) {
        this.send(Frames.connectFrame(this.getConfig().genId(), url, metaMap), null)
    }

    sendConnack() {
        this.send(Frames.connackFrame(this.getHandshake()), null);
    }

    sendPing() {
        this.send(Frames.pingFrame(), null);
    }

    sendPong() {
        this.send(Frames.pongFrame(), null);
    }

    sendClose(code:number) {
        this.send(Frames.closeFrame(code), null);
    }

    sendAlarm(from: Message, alarm: Entity) {
        this.send(Frames.alarmFrame(from, alarm), null);
    }

    abstract getRemoteAddress(): SocketAddress | null ;

    abstract getLocalAddress(): SocketAddress | null ;

    abstract send(frame: Frame, stream: StreamInternal<any> | null);

    abstract reconnect();

    abstract onError(error: any);

    abstract getSession(): Session ;
}