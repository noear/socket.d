//
//  EventListener.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation


/**
 * 事件监听器（根据消息事件路由）
 *
 * @author noear
 * @since 2.0
 */
class EventListener : Listener{
    
    private var doOnOpenHandler:IoConsumer<Session>?;
    private var doOnMessageHandler:IoBiConsumer<Session, Message>?;
    private var doOnCloseHandler:IoConsumer<Session>?;
    private var doOnErrorHandler:IoBiConsumer<Session, Error>?;
    
    /**
     * 事件路由选择器
     * */
    private var  eventRouteSelector:RouteSelector<IoBiConsumer<Session, Message>>;
    
    init() {
        self.eventRouteSelector = RouteSelectorDefault<IoBiConsumer<Session, Message>>();
    }
    
    init(routeSelector:RouteSelector<IoBiConsumer<Session, Message>>){
        self.eventRouteSelector = routeSelector;
    }
    
    
    //for builder
    func  doOnOpen(_ onOpen:IoConsumer<Session>!) ->EventListener{
        self.doOnOpenHandler = onOpen;
        return self;
    }
    
    func  doOnMessage(_ onMessage:IoBiConsumer<Session, Message>!)->EventListener {
        self.doOnMessageHandler = onMessage;
        return self;
    }
    
    func  doOnClose(_ onClose:IoConsumer<Session>!) -> EventListener{
        self.doOnCloseHandler = onClose;
        return self;
    }
    
    func doOnError(_ onError:IoBiConsumer<Session, Error>!) ->EventListener{
        self.doOnErrorHandler = onError;
        return self;
    }
    
    func doOn(_ event:String!,_  handler:IoBiConsumer<Session, Message>!) ->EventListener{
        self.eventRouteSelector.put(event, handler);
        return self;
    }
    
    
    // for Listener
    
    
    func onOpen(_ session: Session) {
        if(doOnOpenHandler != nil){
            doOnOpenHandler!(session);
        }
    }
    
    func onMessage(_ session: Session, _ message: Message) {
        if(doOnMessageHandler != nil){
            doOnMessageHandler!(session, message);
        }
        
        let messageHandler = self.eventRouteSelector.select(message.event());
        if (messageHandler != nil) {
            messageHandler!(session, message);
        }
    }
    
    func onClose(_ session: Session) {
        if(doOnCloseHandler != nil){
            doOnCloseHandler!(session);
        }
    }
    
    func onError(_ session: Session, _ error: Error) {
        if(doOnErrorHandler != nil){
            doOnErrorHandler!(session, error);
        }
    }
}
