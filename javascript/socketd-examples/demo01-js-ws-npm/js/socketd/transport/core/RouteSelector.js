"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RouteSelectorDefault = void 0;
/**
 * 路径映射器默认实现（哈希）
 *
 * @author noear
 * @since 2.0
 */
class RouteSelectorDefault {
    constructor() {
        this._inner = new Map();
    }
    /**
     * 选择
     *
     * @param route 路由
     */
    select(route) {
        return this._inner.get(route);
    }
    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    put(route, target) {
        this._inner.set(route, target);
    }
    /**
     * 移除
     *
     * @param route 路由
     */
    remove(route) {
        this._inner.delete(route);
    }
    /**
     * 数量
     */
    size() {
        return this._inner.size;
    }
}
exports.RouteSelectorDefault = RouteSelectorDefault;
