//
//  RouteSelectorDefault.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation

class RouteSelectorDefault<T> : RouteSelector<T> {
    private var inner:Dictionary<String,T> = Dictionary();
    
    override func select(_ route: String) -> T? {
        return self.inner[route];
    }
    
    override func put(_ route: String, _ target: T) {
        self.inner[route] = target;
    }
    
    override func remove(_ route: String) {
        self.inner.removeValue(forKey: route);
    }
    
    override func size() -> Int {
        self.inner.count;
    }
}
