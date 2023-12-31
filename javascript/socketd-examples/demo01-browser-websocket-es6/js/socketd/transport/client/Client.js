var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { ProcessorDefault } from "../core/Processor";
import { ClientChannel } from "./ClientChannel";
import { SessionDefault } from "../core/SessionDefault";
/**
 * 客户端基类
 *
 * @author noear
 * @since 2.0
 */
export class ClientBase {
    constructor(clientConfig, assistant) {
        this._config = clientConfig;
        this._assistant = assistant;
        this._processor = new ProcessorDefault();
    }
    /**
     * 获取通道助理
     */
    getAssistant() {
        return this._assistant;
    }
    /**
     * 获取心跳处理
     */
    getHeartbeatHandler() {
        return this._heartbeatHandler;
    }
    /**
     * 获取心跳间隔（毫秒）
     */
    getHeartbeatInterval() {
        return this.getConfig().getHeartbeatInterval();
    }
    /**
     * 获取配置
     */
    getConfig() {
        return this._config;
    }
    /**
     * 获取处理器
     */
    getProcessor() {
        return this._processor;
    }
    /**
     * 设置心跳
     */
    heartbeatHandler(handler) {
        if (handler != null) {
            this._heartbeatHandler = handler;
        }
        return this;
    }
    /**
     * 配置
     */
    config(configHandler) {
        if (configHandler != null) {
            configHandler(this._config);
        }
        return this;
    }
    /**
     * 设置监听器
     */
    listen(listener) {
        if (listener != null) {
            this._processor.setListener(listener);
        }
        return this;
    }
    /**
     * 打开会话
     */
    open() {
        return __awaiter(this, void 0, void 0, function* () {
            const connector = this.createConnector();
            //连接
            const channel0 = yield connector.connect();
            //新建客户端通道
            const clientChannel = new ClientChannel(channel0, connector);
            //同步握手信息
            clientChannel.setHandshake(channel0.getHandshake());
            const session = new SessionDefault(clientChannel);
            //原始通道切换为带壳的 session
            channel0.setSession(session);
            console.info(`Socket.D client successfully connected: {link=${this.getConfig().getLinkUrl()}`);
            return session;
        });
    }
}
