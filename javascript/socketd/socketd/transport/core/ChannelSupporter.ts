import {Processor} from "./Processor";
import {Config} from "./Config";
import {ChannelAssistant} from "./ChannelAssistant";

export interface ChannelSupporter<S> {
    /**
     * 处理器
     */
    getProcessor(): Processor;

    /**
     * 配置
     */
    getConfig(): Config;

    /**
     * 通道助理
     */
    getAssistant(): ChannelAssistant<S>;
}