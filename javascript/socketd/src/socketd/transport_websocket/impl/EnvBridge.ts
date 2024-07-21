import {SdWebSocketListener} from "./SdWebSocket";
import {SdWebSocketBrowserClient} from "./SdWebSocketBrowserClient";
import {SdWebSocketNodeJsClient} from "./SdWebSocketNodeJsClient";
import {SdWebSocketUniappClient} from "./SdWebSocketUniappClient";
import {SdWebSocketWeixinClient} from "./SdWebSocketWeixinClient";
import {Config} from "../../transport/core/Config";

export enum Runtime {
    Unknown = 0,
    Browser = 1,
    NodeJs = 2,
    Uniapp = 3,
    Weixin=4
}

export class EnvBridge {
    private static getRuntime(): Runtime {
        if (typeof window != 'undefined') {
            // @ts-ignore
            if (typeof uni != 'undefined' && uni.connectSocket) {
                //如果有 Uniapp
                return Runtime.Uniapp;
            } else {
                // @ts-ignore
                if (typeof wx != 'undefined' && wx.connectSocket && wx.request) {
                    //如果是 Weixin 接口
                    return Runtime.Weixin;
                } else {
                    return Runtime.Browser;
                }
            }
        } else if (typeof process !== 'undefined' && process.versions && process.versions.node) {
            return Runtime.NodeJs;
        } else {
            // @ts-ignore
            if (typeof uni != 'undefined' && uni.connectSocket) {
                //如果有 Uniapp，优先 Uniapp 接口
                return Runtime.Uniapp;
            } else {
                // @ts-ignore
                if (typeof wx != 'undefined' && wx.connectSocket && wx.request) {
                    //如果是 Weixin 接口
                    return Runtime.Weixin;
                } else {
                    return Runtime.Unknown;
                }
            }
        }
    }

    static createSdWebSocketClient(url: string, config:Config, listener: SdWebSocketListener) {
        let runtime = this.getRuntime();

        if (runtime == Runtime.Weixin) {
            console.info("Client channel use wechat api!");
            return new SdWebSocketWeixinClient(url, config, listener);
        } else if (runtime == Runtime.Uniapp) {
            console.info("Client channel use uniapp api!");
            return new SdWebSocketUniappClient(url, config, listener);
        } else if (runtime == Runtime.NodeJs) {
            console.info("Client channel use nodejs api");
            return new SdWebSocketNodeJsClient(url, config, listener);
        } else {
            console.info("Client channel use browser api");
            return new SdWebSocketBrowserClient(url, config, listener);
        }
    }
}