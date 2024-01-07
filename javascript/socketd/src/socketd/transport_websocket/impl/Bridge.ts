import {EnvUtils} from '../../utils/EnvUtils';
import {SdWebSocketListener} from "./SdWebSocket";
import {SdWebSocketBrowserImpl} from "./SdWebSocketBrowserImpl";
import {SdWebSocketNodeImpl} from "./SdWebSocketNodeImpl";

export function createSdWebSocketClient(url: string, connector: SdWebSocketListener) {
    if (EnvUtils.isRunInBrowser()) {
        console.log('浏览器端ws');
        return new SdWebSocketBrowserImpl(url, connector);
    }
    console.log('node端ws');
    return new SdWebSocketNodeImpl(url, connector);
}
