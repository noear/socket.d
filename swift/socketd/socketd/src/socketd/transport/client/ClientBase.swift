//
//  ClientBase.swift
//  socketd
//
//  Created by noear on 2024/1/14.
//

import Foundation

public class ClientBase<T : ChannelAssistant> : ClientInternal {
    //协议处理器
    var _processor = ProcessorDefault();
    //心跳处理
    var _heartbeatHandler:HeartbeatHandler;
    
    //配置
    private var _config:ClientConfig;
    //助理
    private var _assistant:T;
    
    init(_ clientConfig:ClientConfig, _ assistant:T) {
        self._config = clientConfig;
        self._assistant = assistant;
    }
    
    /**
     * 获取通道助理
     */
    func  getAssistant() -> T{
        return _assistant;
    }
    
    /**
     * 获取心跳处理
     */
    func  getHeartbeatHandler() -> HeartbeatHandler{
        return _heartbeatHandler;
    }
    
    /**
     * 获取心跳间隔（毫秒）
     */
    func  getHeartbeatInterval() -> Int64{
        return _config.getHeartbeatInterval();
    }
    
    
    /**
     * 获取配置
     */
    func  getConfig() -> ClientConfig{
        return _config;
    }
    
    /**
     * 获取处理器
     */
    func  getProcessor() -> Processor{
        return _processor;
    }
    
    /**
     * 设置心跳
     */
    func  heartbeatHandler(_ handler:HeartbeatHandler?) -> Self{
        if (handler != nil) {
            _heartbeatHandler = handler!;
        }
        
        return self;
    }
    
    /**
     * 配置
     */
    func config(_ configHandler:ClientConfigHandler?) -> Self{
        if (configHandler != nil) {
            configHandler!.clientConfig(config);
        }
        return self;
    }
    
    /**
     * 设置监听器
     */
    func listen(_ listener:Listener?) -> Self{
        if (listener != nil) {
            processor.setListener(listener);
        }
        return this;
    }
    
    /**
     * 打开会话
     */
    func  open() -> Session {
        var connector = createConnector();
        
        //连接
        var channel0 = connector.connect();
        //新建客户端通道
        var clientChannel = new ClientChannel(channel0, connector);
        //同步握手信息
        clientChannel.setHandshake(channel0.getHandshake());
        var session = new SessionDefault(clientChannel);
        //原始通道切换为带壳的 session
        channel0.setSession(session);
        
        //log.info("Socket.D client successfully connected: {link={}}", getConfig().getLinkUrl());
        
        return session;
    }
    
    /**
     * 创建连接器
     */
    func createConnector() -> ClientConnector {
        
    }
}

