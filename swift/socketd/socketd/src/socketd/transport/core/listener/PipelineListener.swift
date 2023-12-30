//
//  PipelineListener.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation

/**
 * 管道监听器
 *
 * @author noear
 * @since 2.0
 */
class PipelineListener : Listener{
    private var deque = Array<Listener>();
    
    /**
     * 前一个
     */
    func prev(_ listener:Listener)->PipelineListener {
        self.deque.insert(listener, at:0);
        return self;
    }
    
    /**
     * 后一个
     */
    func next(_ listener:Listener) -> PipelineListener {
        self.deque.append(listener);
        return self;
    }
    
    /**
     * 数量（二级监听器的数据）
     * */
    func size() -> Int{
        return self.deque.count;
    }
    
    func onOpen(_ session: Session) {
        for listener in deque {
            listener.onOpen(session);
        }
    }
    
    func onMessage(_ session: Session, _ message: Message) {
        for listener in deque {
            listener.onMessage(session, message);
        }
    }
    
    func onClose(_ session: Session) {
        for listener in deque {
            listener.onClose(session);
        }
    }
    
    func onError(_ session: Session, _ error: Error) {
        for listener in deque {
            listener.onError(session, error);
        }
    }
}
