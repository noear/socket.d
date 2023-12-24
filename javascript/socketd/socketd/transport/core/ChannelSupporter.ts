import {Processor} from "./Processor";
import {Config} from "./Config";
import {ChannelAssistant} from "./ChannelAssistant";

export interface ChannelSupporter<S> {
    /**
     * 处理器
     */
    processor(): Processor;

    /**
     * 配置
     */
    config(): Config;

    /**
     * 通道助理
     */
    assistant(): ChannelAssistant<S>;
}