import {ClientSession} from "../transport/client/ClientSession";
import {IoSupplier} from "../transport/core/Typealias";

/**
 * 负载均衡器
 *
 * @author noear
 * @since 2.3
 */
export class LoadBalancer {
    //轮环计数器
    private static roundCounter: number = 0;

    private static roundCounterGet(): number {
        let counter = LoadBalancer.roundCounter++;
        if (counter > 999_999) {
            LoadBalancer.roundCounter == 0;
        }
        return counter;
    }

    private static hashcode(str): number {
        var hash = 0, i, chr, len;
        if (str.length === 0) return hash;
        for (i = 0, len = str.length; i < len; i++) {
            chr = str.charCodeAt(i);
            hash = ((hash << 5) - hash) + chr;
            hash |= 0; // Convert to 32bit integer
        }
        return hash;
    }

    /**
     * 根据 poll 获取任意一个
     */
    static getAnyByPoll<T extends ClientSession>(coll: Set<T> | null): T | null {
        return LoadBalancer.getAny(coll, () => LoadBalancer.roundCounterGet());
    }

    /**
     * 根据 hash 获取任意一个
     */
    static getAnyByHash<T extends ClientSession>(coll: Set<T> | null, diversion: string): T | null {
        return LoadBalancer.getAny(coll, () => LoadBalancer.hashcode(diversion));
    }


    /**
     * 获取任意一个
     */
    static getAny<T extends ClientSession>(coll: Set<T> | null, randomSupplier: IoSupplier<number>): T | null {
        if (coll == null || coll.size == 0) {
            return null;
        } else {
            //查找可用的会话
            let sessions = new Array<T>();
            for (let s of coll) {
                if (s.isValid() && !s.isClosing()) {
                    sessions.push(s);
                }
            }

            if (sessions.length == 0) {
                return null;
            }

            if (sessions.length == 1) {
                return sessions[0];
            }

            //随机获取
            let random = Math.abs(randomSupplier());
            let idx = random % sessions.length;
            return sessions[idx];
        }
    }

    /**
     * 获取第一个
     */
    public static getFirst<T extends ClientSession>(coll: Array<T>): T | null {
        if (coll == null || coll.length == 0) {
            return null;
        } else {
            //查找可用的会话
            for (let s of coll) {
                if (s.isValid() && !s.isClosing()) {
                    return s;
                }
            }

            return null;
        }
    }
}