import {Frame} from "./Message";

export interface ChannelAssistant<T> {
    /**
     * 写入
     *
     * @param target 目标
     * @param frame  帧
     */
    write(target: T, frame: Frame);

    /**
     * 是否有效
     */
    isValid(target: T): boolean;

    /**
     * 关闭
     */
    close(target: T);

    /**
     * 获取远程地址
     */
    getRemoteAddress(target: T): string;

    /**
     * 获取本地地址
     */
    getLocalAddress(target: T): string;
}