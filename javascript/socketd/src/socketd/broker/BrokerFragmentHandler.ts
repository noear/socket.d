import {FragmentHandlerDefault} from "../transport/core/FragmentHandler";

/**
 * 经纪人分片处理（关掉聚合）
 *
 * @author noear
 * @since 2.1
 */
export class BrokerFragmentHandler extends FragmentHandlerDefault{
    aggrEnable(): boolean {
        return false;
    }
}