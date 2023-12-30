//
//  PathListener.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation

/**
 * 路径监听器（根据握手地址路由，一般用于服务端）
 *
 * @author noear
 * @since 2.0
 */
class PathListener : Listener{
    /**
     * 路径路由选择器
     * */
    private var pathRouteSelector:RouteSelector<Listener>;
    
    init() {
        self.pathRouteSelector = RouteSelectorDefault<Listener>();
    }
    
    init( routeSelector:RouteSelector<Listener>) {
        self.pathRouteSelector = routeSelector;
    }
    
    /**
     * 路由
     */
    func  of(_ path:String,_  listener:Listener) -> PathListener{
        pathRouteSelector.put(path, listener);
        return self;
    }
    
    /**
     * 数量（二级监听器的数据）
     */
    func  size() -> Int{
        return pathRouteSelector.size();
    }
    
    func onOpen(_ session:Session)  {
        let l1 = pathRouteSelector.select(session.path());
        
        if (l1 != nil) {
            l1!.onOpen(session);
        }
    }
    
    func onMessage(_ session:Session, _ message:Message) {
        let l1 = pathRouteSelector.select(session.path());
        
        if (l1 != nil) {
            l1!.onMessage(session, message);
        }
    }
    
    func onClose(_ session:Session) {
        let l1 = pathRouteSelector.select(session.path());
        
        if (l1 != nil) {
            l1!.onClose(session);
        }
    }
    
    func onError(_ session:Session, _ error:Error) {
        let l1 = pathRouteSelector.select(session.path());
        
        if (l1 != nil) {
            l1!.onError(session, error);
        }
    }
}
