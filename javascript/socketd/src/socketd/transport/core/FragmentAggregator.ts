import {MessageInternal} from "./Message";
import {Frame} from "./Frame";


/**
 * 分片聚合器
 *
 * @author noear
 * @since 2.1
 */
export interface FragmentAggregator {
    /**
     * 获取流Id
     */
    getSid(): string;

    /**
     * 数据流大小
     */
    getDataStreamSize(): number;

    /**
     * 数据总长度
     */
    getDataLength(): number;

    /**
     * 添加分片
     */
    add(index: number, message: MessageInternal);

    /**
     * 获取聚合帧
     */
    get(): Frame;
}