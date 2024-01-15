//
//  ProcessorDefault.swift
//  socketd
//
//  Created by noear on 2024/1/14.
//

import Foundation

class ProcessorDefault : Processor {
    func setListener(_ listener: Listener) {
        <#code#>
    }
    
    func onReceive(_ channel: ChannelInternal, _ frame: Frame) {
        <#code#>
    }
    
    func onOpen(_ channel: ChannelInternal) {
        <#code#>
    }
    
    func onMessage(_ channel: ChannelInternal, _ message: Message) {
        <#code#>
    }
    
    func onClose(_ channel: ChannelInternal) {
        <#code#>
    }
    
    func onError(_ channel: ChannelInternal, _ error: Error) {
        <#code#>
    }
    
    
}
