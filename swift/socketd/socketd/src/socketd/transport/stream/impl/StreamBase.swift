//
//  StreamBase.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation

class StreamBase : StreamInternal{
    private var _sid:String!;
    private var _demands:Int32!;
    private var _timeout:Int64!;
    
    private var _doOnError: IoConsumer<Error>?;
    private var _doOnProgress: IoTriConsumer<Bool, Int32, Int32>?;
    
    init(_ sid:String, _ demands:Int32, _ timeout:Int64) {
        self._sid = sid;
        self._demands = demands;
        self._timeout = timeout;
    }
    
    
    func sid() -> String {
        return _sid;
    }
    
    func demands() -> Int32 {
        return _demands;
    }
    
    func timeout() -> Int64 {
        return _timeout;
    }
    
    func insuranceStart(_ streamManger: StreamManger, _ streamTimeout: Int64) {
        
    }
    
    func insuranceCancel() {
        
    }
    
    func onError(_ error: Error) {
        if(_doOnError != nil){
            _doOnError!(error);
        }
    }
    
    
    func thenError(_ onError: @escaping IoConsumer<Error>) -> Self {
        _doOnError = onError;
        
        return self;
    }
    
    func onProgress(_ isSend: Bool, _ val: Int32, _ max: Int32) {
        if(_doOnProgress != nil){
            _doOnProgress!(isSend, val, max);
        }
    }
    
    func thenProgress(_ onProgress: @escaping IoTriConsumer<Bool, Int32, Int32>) -> Self {
        _doOnProgress = onProgress;
        
        return self;
    }
    
    
    func onReply(_ reply: MessageInternal) {
        
    }
    
    
    func isDone() -> Bool {
        return false;
    }
}
