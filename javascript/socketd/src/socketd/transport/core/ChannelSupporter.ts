import type {Processor} from "./Processor";
import type {Config} from "./Config";
import type {ChannelAssistant} from "./ChannelAssistant";

/**
 * 通道支持者（创建通道的参数）
 *
 * @author noear
 * @since 2.1
 */
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