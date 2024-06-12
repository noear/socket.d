import {StreamInternal} from "./Stream";

/**
 * 流管理器
 *
 * @author noear
 * @since 2.0
 */
export interface StreamManger {
    /**
     * 添加流
     *
     * @param sid    流Id
     * @param stream 流
     */
    addStream(sid: string, stream: StreamInternal<any>);

    /**
     * 获取流
     *
     * @param sid 流Id
     */
    getStream(sid: string): StreamInternal<any> | null;

    /**
     * 移除流
     *
     * @param sid 流Id
     */
    removeStream(sid: string);
}