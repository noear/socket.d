/**
 * 会话基类
 *
 * @author noear
 */
export class SessionBase {
    constructor(channel) {
        this._channel = channel;
        this._sessionId = this.generateId();
    }
    sessionId() {
        return this._sessionId;
    }
    name() {
        return this.param("@");
    }
    attrMap() {
        if (this._attrMap == null) {
            this._attrMap = new Map();
        }
        return this._attrMap;
    }
    attrHas(name) {
        if (this._attrMap == null) {
            return false;
        }
        return this._attrMap.has(name);
    }
    attr(name) {
        if (this._attrMap == null) {
            return null;
        }
        return this._attrMap.get(name);
    }
    attrOrDefault(name, def) {
        const tmp = this.attr(name);
        return tmp ? tmp : def;
    }
    attrPut(name, val) {
        this.attrMap().set(name, val);
    }
    generateId() {
        return this._channel.getConfig().getIdGenerator().generate();
    }
}
