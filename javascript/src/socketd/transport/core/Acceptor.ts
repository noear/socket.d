import {Message} from "./Message";
import {Consumer} from "../../utils/Consumer";


/**
 * 答复接收器
 *
 * @author noear
 * @since 1.0
 */
export interface Acceptor {
    /**
     * 是否单发接收
     * */
    isSingle(): boolean;

    /**
     * 是否结束接收
     * */
    isDone(): boolean;

    /**
     * 超时设定（单位：毫秒）
     * */
    timeout(): bigint;

    /**
     * 接收答复
     * */
    accept(message: Message, onError: Consumer<Error>): void
}