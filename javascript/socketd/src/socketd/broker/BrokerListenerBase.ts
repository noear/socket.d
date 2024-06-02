import {LoadBalancer} from "../cluster/LoadBalancer";
import {Session} from "../transport/core/Session";
import {Message} from "../transport/core/Message";
import {EntityMetas} from "../transport/core/EntityMetas";

export abstract class BrokerListenerBase {
    private _sessionAll = new Map<string, Session>();
    //玩家会话
    private _playerSessions = new Map<string, Set<Session>>;

    /**
     * 获取所有会话（包括没有名字的）
     */
    getSessionAll(): IterableIterator<Session> {
        return this._sessionAll.values();
    }

    /**
     * 获取任意会话（包括没有名字的）
     */
    getSessionAny(): Session | null{
        return LoadBalancer.getAnyByPoll(new Set<Session>(this._sessionAll.values()));
    }

    /**
     * 获取会话数量
     * */
    getSessionCount(): number {
        return this._sessionAll.size;
    }

    /**
     * 获取所有玩家的名字
     */
    getNameAll(): Set<string> {
        return new Set<string>(this._playerSessions.keys());
    }

    /**
     * 获取所有玩家数量
     *
     * @param name 名字
     */
    getPlayerCount(name: string): number {
        let tmp = this.getPlayerAll(name);

        if (tmp == null) {
            return 0;
        } else {
            return tmp.size;
        }
    }


    /**
     * 获取所有玩家会话
     *
     * @param name 名字
     */
    getPlayerAll(name: string | null): Set<Session> | null {
        if (name) {
            let tmp = this._playerSessions.get(name);
            return tmp ? tmp : null;
        } else {
            return null;
        }
    }

    /**
     * 获取任意一个玩家会话
     *
     * @param atName 目标名字
     * @since 2.3
     */
    getPlayerAny(atName: string, requester?: Session|null, message?:Message): Session | null {
        if (!atName) {
            return null;
        }

        if (atName.endsWith("!")) {
            atName = atName.substring(0, atName.length - 1);
            let x_hash: string | null = null;

            if (message != null) {
                x_hash = message.meta(EntityMetas.META_X_HASH);
            }

            if (!x_hash) {
                if (requester == null) {
                    return LoadBalancer.getAnyByPoll(this.getPlayerAll(atName));
                } else {
                    //使用请求者 ip 分流
                    return LoadBalancer.getAnyByHash(this.getPlayerAll(atName), requester.remoteAddress()!.address);
                }
            } else {
                //使用指定 hash 分流
                return LoadBalancer.getAnyByHash(this.getPlayerAll(atName), x_hash);
            }
        } else {
            return LoadBalancer.getAnyByPoll(this.getPlayerAll(atName));
        }
    }


    /**
     * 添加玩家会话
     *
     * @param name    名字
     * @param session 玩家会话
     */
    addPlayer(name: string | null, session: Session) {
        //注册玩家会话
        if (name) {
            let sessions = this._playerSessions.get(name);
            if (!sessions) {
                sessions = new Set<Session>();
                this._playerSessions.set(name, sessions);
            }
            sessions.add(session);
        }

        this._sessionAll.set(session.sessionId(), session);
    }

    /**
     * 移除玩家会话
     *
     * @param name    名字
     * @param session 玩家会话
     */
    removePlayer(name: string | null, session: Session) {
        //注销玩家会话
        if (name) {
            let sessions = this.getPlayerAll(name);
            if (sessions != null) {
                sessions.delete(session);
            }
        }

        this._sessionAll.delete(session.sessionId());
    }
}