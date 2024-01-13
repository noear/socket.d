//
//  StreamMangerDefault.swift
//  socketd
//
//  Created by noear on 2024/1/13.
//

import Foundation

class StreamMangerDefault : StreamManger {
    private var _config:Config;
    private var _streamMap: Dictionary<String, StreamInternal>;
    
    init(_ config: Config) {
        self._config = config
        self._streamMap = Dictionary();
    }
    
    func addStream(_ sid: String, _ stream: StreamInternal) {
        _streamMap[sid] = stream;
    }
    
    func getStream(_ sid: String) -> StreamInternal? {
        return _streamMap[sid];
    }
    
    func removeStream(_ sid: String) {
        _streamMap.removeValue(forKey: sid);
    }
}
