//
//  ClientConfig.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
class ClientConfig : ConfigBase {
    //通讯架构（tcp, ws, udp）
    private var _schema: String;
    
    //连接地址
    private var _linkUrl: String;
    private var _url: String;
    private var _host: String;
    private var _port: Int32;
    private var _metaMap = Dictionary<String, String>();
    
    //心跳间隔（毫秒）
    private var _heartbeatInterval: Int64;
    
    //连接越时（毫秒）
    private var _connectTimeout: Int64;
    
    //是否自动重链
    private var _autoReconnect: Bool;
    
    init(url: String) {
        super.init(true);
        
        //支持 sd: 开头的架构
        if (url.starts(with: "sd:")) {
            self._url = url.substring(from: url.index(url.startIndex, offsetBy: 3))
        }else{
            self._url = url;
        }
        
        self._linkUrl = "sd:" + self._url;
        
        var _uri = URL(url);
        
        self._host = _uri.host;
        self._port = _uri.port;
        self._schema = _uri.protocol;
        
        if (self._port < 0) {
            self._port = 8602;
        }
        
        self._connectTimeout = 10_000;
        self._heartbeatInterval = 20_000;
        
        self._autoReconnect = true;
    }
    
    
    /**
     * 获取通讯架构（tcp, ws, udp）
     */
    func getSchema() -> String {
        return self._schema;
    }
    
    /**
     * 获取链接地址
     */
    func getLinkUrl() ->  String {
        return self._linkUrl;
    }
    
    /**
     * 获取连接地址
     */
    func getUrl() ->  String {
        return self._url;
    }
    
    /**
     * 获取连接主机
     */
    func getHost() ->  String {
        return self._host;
    }
    
    /**
     * 获取连接端口
     */
    func getPort() ->  Int32 {
        return self._port;
    }
    
    /**
     * 获取连接元信息字典
     */
    func getMetaMap() ->  Dictionary<string, string> {
        return self._metaMap;
    }
    
    func metaPut(name: String, val: String) ->  Self {
        self._metaMap.set(name, val);
        return this;
    }
    
    
    /**
     * 获取心跳间隔（单位毫秒）
     */
    func getHeartbeatInterval() ->  Int64 {
        return self._heartbeatInterval;
    }
    
    /**
     * 配置心跳间隔（单位毫秒）
     */
    func  heartbeatInterval(heartbeatInterval: number) ->  Self {
        self._heartbeatInterval = heartbeatInterval;
        return this;
    }
    
    /**
     * 获取连接超时（单位毫秒）
     */
    func   getConnectTimeout() ->  number {
        return self._connectTimeout;
    }
    
    /**
     * 配置连接超时（单位毫秒）
     */
    func connectTimeout(connectTimeout: Int64) ->  Self {
        self._connectTimeout = connectTimeout;
        return this;
    }
    
    /**
     * 获取是否自动重链
     */
    func isAutoReconnect() ->  Bool {
        return self._autoReconnect;
    }
    
    /**
     * 配置是否自动重链
     */
    func autoReconnect(autoReconnect: Bool) ->  Self {
        self._autoReconnect = autoReconnect;
        return this;
    }
    
    func idleTimeout(idleTimeout: Int64) ->  Self {
        if (self._autoReconnect == false) {
            //自动重链下，禁用 idleTimeout
            self._idleTimeout = (idleTimeout);
            return this;
        } else {
            self._idleTimeout = (0);
            return this;
        }
    }
    
    func toString() ->  String {
        return "ClientConfig{" +
        "schema='" + self._schema + '\'' +
        ", charset=" + self._charset +
        ", url='" + self._url + '\'' +
        ", heartbeatInterval=" + self._heartbeatInterval +
        ", connectTimeout=" + self._connectTimeout +
        ", idleTimeout=" + self._idleTimeout +
        ", requestTimeout=" + self._requestTimeout +
        ", readBufferSize=" + self._readBufferSize +
        ", writeBufferSize=" + self._writeBufferSize +
        ", autoReconnect=" + self._autoReconnect +
        ", maxUdpSize=" + self._maxUdpSize +
        "}";
    }
}

