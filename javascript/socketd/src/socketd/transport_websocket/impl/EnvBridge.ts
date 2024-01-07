import {SdWebSocketListener} from "./SdWebSocket";
import {SdWebSocketBrowserImpl} from "./SdWebSocketBrowserImpl";
import {SdWebSocketNodeImpl} from "./SdWebSocketNodeImpl";
import {SdWebSocketUniappImpl} from "./SdWebSocketUniappImpl";

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
            if (typeof wx != 'undefined' && wx.request) {
                //如果有微信，优先微信接口
                return Runtime.Weixin;
                // @ts-ignore
            } else if (typeof uni != 'undefined') {
                //如果有 Uniapp，优先 Uniapp 接口
                return Runtime.Uniapp;
            } else {
                return Runtime.Browser;
            }
        } else if (typeof process !== 'undefined' && process.versions && process.versions.node) {
            return Runtime.NodeJs;
        } else {
            return Runtime.Unknown;
        }
    }

    static createSdWebSocketClient(url: string, connector: SdWebSocketListener) {
        let runtime = this.getRuntime();

        if (runtime == Runtime.Uniapp) {
            console.info("Client channel use uniapp api!");
            return new SdWebSocketUniappImpl(url, connector);
        } else if (runtime == Runtime.NodeJs) {
            console.info("Client channel use nodejs api");
            return new SdWebSocketNodeImpl(url, connector);
        } else {
            console.info("Client channel use browser api");
            return new SdWebSocketBrowserImpl(url, connector);
        }
    }
}