//
//  RouteSelector.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation

/**
 * 路由选择器
 *
 * @author noear
 * @since 2.0
 */
class RouteSelector<T>{
    /**
     * 选择
     *
     * @param route 路由
     */
    func select(_ route:String) -> T?{
        return nil;
    }
    
    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    func put(_ route:String,_ target:T){
        
    }
    
    /**
     * 移除
     *
     * @param route 路由
     */
    func remove(_ route:String){
        
    }
    
    /**
     * 数量
     */
    func size() -> Int{
        return -1;
    }
}
