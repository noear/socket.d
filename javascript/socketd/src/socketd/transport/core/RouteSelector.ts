/**
 * 路径映射器
 *
 * @author noear
 * @since 2.0
 */
export interface RouteSelector<T> {
    /**
     * 选择
     *
     * @param route 路由
     */
    select(route: string): T;

    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    put(route: string, target: T);

    /**
     * 移除
     */
    remove(route: string);

    /**
     * 数量
     */
    size(): number;
}

/**
 * 路径映射器默认实现（哈希）
 *
 * @author noear
 * @since 2.0
 */
export class RouteSelectorDefault<T> implements RouteSelector<T> {
    private _inner = new Map<string, T>();

    /**
     * 选择
     *
     * @param route 路由
     */
    select(route: string): T {
        return this._inner.get(route);
    }

    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    put(route: string, target: T) {
        this._inner.set(route, target);
    }

    /**
     * 移除
     *
     * @param route 路由
     */
    remove(route: string) {
        this._inner.delete(route);
    }

    /**
     * 数量
     */
    size(): number {
        return this._inner.size;
    }
}