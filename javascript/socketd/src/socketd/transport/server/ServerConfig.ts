import {ConfigBase} from "../core/Config";

export class ServerConfig extends ConfigBase {
    private readonly _schema: string;
    private readonly _schemaCleaned: string;

    //主机名
    private _host: string;
    //端口
    private _port: number;
    //http server
    private _httpServer: any;

    constructor(schema: string) {
        super(false);

        this._schema = schema;

        //支持 sd: 开头的架构
        if (schema.startsWith("sd:")) {
            schema = schema.substring(3);
        }

        this._schemaCleaned = schema;

        this._host = "";
        this._port = 8602;
    }


    /**
     * 获取协议架构
     */
    getSchema(): string {
        return this._schema;
    }

    /**
     * 获取主机
     */
    getHost(): string {
        return this._host;
    }

    /**
     * 获取 http-server
     * */
    getHttpServer(): any {
        return this._httpServer;
    }

    /**
     * 配置 http-server（可共用端口）
     * */
    httpServer(httpServer: any): this {
        this._httpServer = httpServer;
        return this;
    }

    /**
     * 配置主机
     */
    host(host: string): this {
        this._host = host;
        return this;
    }

    /**
     * 获取端口
     */
    getPort(): number {
        return this._port;
    }

    /**
     * 配置端口
     */
    port(port: number): this {
        this._port = port;
        return this;
    }

    /**
     * 获取本机地址
     */
    getLocalUrl(): string {
        if (this._host) {
            return "sd:" + this._schemaCleaned + "://" + this._host + ":" + this._port;
        } else {
            return "sd:" + this._schemaCleaned + "://127.0.0.1:" + this._port;
        }
    }

    toString(): string {
        return "ServerConfig{" +
            "schema='" + this._schemaCleaned + '\'' +
            ", charset=" + this._charset +
            ", host='" + this._host + '\'' +
            ", port=" + this._port +
            ", ioThreads=" + this._ioThreads +
            ", codecThreads=" + this._codecThreads +
            ", exchangeThreads=" + this._exchangeThreads +
            ", idleTimeout=" + this._idleTimeout +
            ", requestTimeout=" + this._requestTimeout +
            ", streamTimeout=" + this._streamTimeout +
            ", readBufferSize=" + this._readBufferSize +
            ", writeBufferSize=" + this._writeBufferSize +
            ", maxUdpSize=" + this._maxUdpSize +
            '}';
    }
}