import {Codec, CodecByteBuffer} from "./Codec";
import {StreamManger} from "./Stream";

export interface Config {

    codec(): Codec;

    streamManger(): StreamManger;

    generateId(): string;
}

export class ConfigBase implements Config {
    _codec: Codec;
    _streamManger: StreamManger;

    constructor() {
        this._codec = new CodecByteBuffer();
        this._streamManger = new StreamManger();
    }

    codec(): Codec {
        return this._codec;
    }

    streamManger(): StreamManger {
        return this._streamManger;
    }

    generateId():string{
        return '';
    }
}