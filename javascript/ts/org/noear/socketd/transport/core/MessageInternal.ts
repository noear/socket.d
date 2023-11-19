import {Message} from "./Message";

export interface MessageInternal extends Message {
    /**
     * 获取标记
     */
    flag(): number;
}