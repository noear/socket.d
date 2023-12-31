var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { ClusterClientSession } from "./ClusterClientSession";
import { createClient } from "../SocketD";
/**
 * 集群客户端
 *
 * @author noear
 */
export class ClusterClient {
    constructor(serverUrls) {
        this._serverUrls = serverUrls;
    }
    heartbeatHandler(heartbeatHandler) {
        this._heartbeatHandler = heartbeatHandler;
        return this;
    }
    /**
     * 配置
     */
    config(configHandler) {
        this._configHandler = configHandler;
        return this;
    }
    /**
     * 监听
     */
    listen(listener) {
        this._listener = listener;
        return this;
    }
    /**
     * 打开
     */
    open() {
        return __awaiter(this, void 0, void 0, function* () {
            const sessionList = new ClusterClient[this._serverUrls.length];
            for (const urls of this._serverUrls) {
                for (let url of urls.split(",")) {
                    url = url.trim();
                    if (!url) {
                        continue;
                    }
                    const client = createClient(url);
                    if (this._listener != null) {
                        client.listen(this._listener);
                    }
                    if (this._configHandler != null) {
                        client.config(this._configHandler);
                    }
                    if (this._heartbeatHandler != null) {
                        client.heartbeatHandler(this._heartbeatHandler);
                    }
                    const session = yield client.open();
                    sessionList.add(session);
                }
            }
            return new ClusterClientSession(sessionList);
        });
    }
}
