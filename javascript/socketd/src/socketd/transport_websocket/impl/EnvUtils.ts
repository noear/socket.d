export enum Runtime {
    None = -1,
    Browser = 1,
    Node = 2,
    Uniapp = 3,
    Weixin=4
}

export class EnvUtils {
    static _runtime = (typeof window != 'undefined') ? Runtime.Browser :
        (typeof process !== 'undefined' && process.versions && process.versions.node) ? Runtime.Node : Runtime.None;

    static runtime(): Runtime {
        return EnvUtils._runtime;
    }

    static isRunInBrowser(): boolean {
        return EnvUtils._runtime == Runtime.Browser;
    }

    static isRunInNode(): boolean {
        return EnvUtils._runtime == Runtime.Node;
    }
}
