/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 处理器默认实现
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class ProcessorDefault implements org.noear.socketd.transport.core.Processor {
        static log: org.slf4j.Logger; public static log_$LI$(): org.slf4j.Logger { if (ProcessorDefault.log == null) { ProcessorDefault.log = org.slf4j.LoggerFactory.getLogger(ProcessorDefault); }  return ProcessorDefault.log; }

        /*private*/ listener: org.noear.socketd.transport.core.Listener;

        /**
         * 设置监听
         * @param {*} listener
         */
        public setListener(listener: org.noear.socketd.transport.core.Listener) {
            if (listener != null){
                this.listener = listener;
            }
        }

        /**
         * 接收处理
         * @param {*} channel
         * @param {org.noear.socketd.transport.core.Frame} frame
         */
        public onReceive(channel: org.noear.socketd.transport.core.Channel, frame: org.noear.socketd.transport.core.Frame) {
            if (ProcessorDefault.log_$LI$().isDebugEnabled()){
                if (channel.getConfig().clientMode()){
                    ProcessorDefault.log_$LI$().debug("C-REV:{}", frame);
                } else {
                    ProcessorDefault.log_$LI$().debug("S-REV:{}", frame);
                }
            }
            if (frame.getFlag() === org.noear.socketd.transport.core.Flag.Connect){
                const handshake: org.noear.socketd.transport.core.internal.HandshakeInternal = new org.noear.socketd.transport.core.internal.HandshakeInternal(frame.getMessage());
                channel.setHandshake(handshake);
                this.onOpen(channel);
                if (channel.isValid()){
                    channel.sendConnack(frame.getMessage());
                }
            } else if (frame.getFlag() === org.noear.socketd.transport.core.Flag.Connack){
                const handshake: org.noear.socketd.transport.core.internal.HandshakeInternal = new org.noear.socketd.transport.core.internal.HandshakeInternal(frame.getMessage());
                channel.setHandshake(handshake);
                this.onOpen(channel);
            } else {
                if (channel.getHandshake() == null){
                    channel.close(org.noear.socketd.transport.core.Constants.CLOSE1_PROTOCOL);
                    if (frame.getFlag() === org.noear.socketd.transport.core.Flag.Close){
                        throw new org.noear.socketd.exception.SocketdConnectionException("Connection request was rejected");
                    }
                    if (ProcessorDefault.log_$LI$().isWarnEnabled()){
                        ProcessorDefault.log_$LI$().warn("Channel andshake is null, sessionId={}", channel.getSession().sessionId());
                    }
                    return;
                }
                try {
                    switch((frame.getFlag())) {
                    case org.noear.socketd.transport.core.Flag.Ping:
                        {
                            channel.sendPong();
                            break;
                        };
                    case org.noear.socketd.transport.core.Flag.Pong:
                        {
                            break;
                        };
                    case org.noear.socketd.transport.core.Flag.Close:
                        {
                            channel.close(org.noear.socketd.transport.core.Constants.CLOSE1_PROTOCOL);
                            this.onCloseInternal(channel);
                            break;
                        };
                    case org.noear.socketd.transport.core.Flag.Message:
                    case org.noear.socketd.transport.core.Flag.Request:
                    case org.noear.socketd.transport.core.Flag.Subscribe:
                        {
                            this.onReceiveDo(channel, frame, false);
                            break;
                        };
                    case org.noear.socketd.transport.core.Flag.Reply:
                    case org.noear.socketd.transport.core.Flag.ReplyEnd:
                        {
                            this.onReceiveDo(channel, frame, true);
                            break;
                        };
                    default:
                        {
                            channel.close(org.noear.socketd.transport.core.Constants.CLOSE1_PROTOCOL);
                            this.onClose(channel);
                        };
                    }
                } catch(e) {
                    this.onError(channel, e);
                }
            }
        }

        /*private*/ onReceiveDo(channel: org.noear.socketd.transport.core.Channel, frame: org.noear.socketd.transport.core.Frame, isReply: boolean) {
            const fragmentIdxStr: string = frame.getMessage().meta(org.noear.socketd.transport.core.EntityMetas.META_DATA_FRAGMENT_IDX);
            if (fragmentIdxStr != null){
                const index: number = /* parseInt */parseInt(fragmentIdxStr);
                const frameNew: org.noear.socketd.transport.core.Frame = channel.getConfig().getFragmentHandler().aggrFragment(channel, index, frame);
                if (frameNew == null){
                    return;
                } else {
                    frame = frameNew;
                }
            }
            if (isReply){
                channel.retrieve(frame, (error) => {
                    this.onError(channel, error);
                });
            } else {
                this.onMessage(channel, frame.getMessage());
            }
        }

        /**
         * 打开时
         * 
         * @param {*} channel 通道
         */
        public onOpen(channel: org.noear.socketd.transport.core.Channel) {
            this.listener.onOpen(channel.getSession());
        }

        /**
         * 收到消息时
         * 
         * @param {*} channel 通道
         * @param {*} message 消息
         */
        public onMessage(channel: org.noear.socketd.transport.core.Channel, message: org.noear.socketd.transport.core.Message) {
            channel.getConfig().getChannelExecutor().submit(() => {
                try {
                    this.listener.onMessage(channel.getSession(), message);
                } catch(e) {
                    if (ProcessorDefault.log_$LI$().isWarnEnabled()){
                        ProcessorDefault.log_$LI$().warn("{}", e);
                    }
                }
            });
        }

        /**
         * 关闭时
         * 
         * @param {*} channel 通道
         */
        public onClose(channel: org.noear.socketd.transport.core.Channel) {
            if (channel.isClosed() === 0){
                this.onCloseInternal(channel);
            }
        }

        /**
         * 关闭时（内部处理）
         * 
         * @param {*} channel 通道
         * @private
         */
        /*private*/ onCloseInternal(channel: org.noear.socketd.transport.core.Channel) {
            this.listener.onClose(channel.getSession());
        }

        /**
         * 出错时
         * 
         * @param {*} channel 通道
         * @param {Error} error   错误信息
         */
        public onError(channel: org.noear.socketd.transport.core.Channel, error: Error) {
            this.listener.onError(channel.getSession(), error);
        }

        constructor() {
            this.listener = new org.noear.socketd.transport.core.listener.SimpleListener();
        }
    }
    ProcessorDefault["__class"] = "org.noear.socketd.transport.core.internal.ProcessorDefault";
    ProcessorDefault["__interfaces"] = ["org.noear.socketd.transport.core.Processor"];


}

