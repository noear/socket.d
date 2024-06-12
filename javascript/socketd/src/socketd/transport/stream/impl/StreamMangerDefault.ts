import {Config} from "../../core/Config";
import {Asserts} from "../../core/Asserts";
import {Constants} from "../../core/Constants";
import {StreamInternal} from "../Stream";
import {StreamManger} from "../StreamManger";

export class StreamMangerDefault implements StreamManger{
    _config:Config;
    _streamMap: Map<string, StreamInternal<any>>

    constructor(config:Config) {
        this._config = config;
        this._streamMap = new Map<string, StreamInternal<any>>();
    }

    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    getStream(sid:string) {
        const tmp = this._streamMap.get(sid);
        if (tmp) {
            return tmp;
        } else {
            return null;
        }
    }

    /**
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    addStream(sid, stream: StreamInternal<any>) {
        Asserts.assertNull("stream", stream);

        if(stream.demands() == Constants.DEMANDS_ZERO){
            //零需求，则不添加
            return;
        }

        this._streamMap.set(sid, stream);

        //增加流超时处理（做为后备保险）
        const streamTimeout = stream.timeout() > 0 ? stream.timeout() : this._config.getStreamTimeout();
        if (streamTimeout > 0) {
            stream.insuranceStart(this, streamTimeout);
        }
    }

    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    removeStream(sid) {
        const stream = this.getStream(sid);

        if (stream) {
            this._streamMap.delete(sid);
            stream.insuranceCancel();
            console.debug(`${this._config.getRoleName()} stream removed, sid=${sid}`);
        }
    }
}
