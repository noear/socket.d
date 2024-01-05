import {EnvUtils} from '../../utils/EnvUtils';
import {BrowserWsClientImpl} from './impl/BridgeWsClientImpl.browser';
import {NodeWsClientImpl} from './impl/BridgeWsClientImpl.node';
import {BridgeWsClientConnector} from "./BridgeWsClient";

export function createWsClient(url: string, connector: BridgeWsClientConnector) {
    if (EnvUtils.isRunInBrowser()) {
        console.log('浏览器端ws');
        return new BrowserWsClientImpl(url, connector);
    }
    console.log('node端ws');
    return new NodeWsClientImpl(url, connector);
}
