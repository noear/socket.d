//
//  Typealias.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation


typealias IoConsumer<T> = (_ t:T) -> ()
typealias IoBiConsumer<T1,T2> = (_ t1:T1,_  t2:T2 ) -> ()
typealias IoTriConsumer<T1,T2,T3> = (_ t1:T1,_  t2:T2,_ t3:T3 ) -> ()
typealias IoFunction<T1,T2> = (_ t1:T1) -> T2;
