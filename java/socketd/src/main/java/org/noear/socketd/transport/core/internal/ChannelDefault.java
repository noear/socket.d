package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * 通道默认实现（每个连接都会建立一个或多个通道）
 *
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S> extends ChannelBase implements ChannelInternal {
    private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);

    private final S source;

    //助理
    private final ChannelAssistant<S> assistant;
    //流管理器
    private final StreamManger acceptorManger;
    //会话（懒加载）
    private Session session;

    public ChannelDefault(S source, Config config, ChannelAssistant<S> assistant) {
        super(config);
        this.source = source;
        this.assistant = assistant;
        this.acceptorManger = config.getStreamManger();
    }

    /**
     * 是否有效
     */
    @Override
    public boolean isValid() {
        return isClosed() == 0 && assistant.isValid(source);
    }

    /**
     * 获取远程地址
     */
    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return assistant.getRemoteAddress(source);
    }

    /**
     * 获取本地地址
     */
    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return assistant.getLocalAddress(source);
    }

    private Object SEND_LOCK  = new Object();

    /**
     * 发送
     */
    @Override
    public void send(Frame frame, StreamAcceptorBase acceptor) throws IOException {
        Asserts.assertClosed(this);

        if (log.isDebugEnabled()) {
            if (getConfig().clientMode()) {
                log.debug("C-SEN:{}", frame);
            } else {
                log.debug("S-SEN:{}", frame);
            }
        }

        synchronized (SEND_LOCK) {
            if (frame.getMessage() != null) {
                MessageInternal message = frame.getMessage();

                //注册流接收器
                if (acceptor != null) {
                    acceptorManger.addAcceptor(message.sid(), acceptor);
                }

                //如果有实体（尝试分片）
                if (message.entity() != null) {
                    //确保用完自动关闭

                    if (message.dataSize() > Constants.MAX_SIZE_FRAGMENT) {
                        //满足分片条件
                        int fragmentIndex = 0;
                        while (true) {
                            //获取分片
                            fragmentIndex++;
                            Entity fragmentEntity = getConfig().getFragmentHandler().nextFragment(this, fragmentIndex, message);

                            if (fragmentEntity != null) {
                                //主要是 sid 和 entity
                                Frame fragmentFrame = new Frame(frame.getFlag(), new MessageDefault()
                                        .flag(frame.getFlag())
                                        .sid(message.sid())
                                        .entity(fragmentEntity));

                                assistant.write(source, fragmentFrame);
                            } else {
                                //没有分片，说明发完了
                                return;
                            }
                        }
                    } else {
                        //不满足分片条件，直接发
                        assistant.write(source, frame);
                        return;
                    }

                }
            }

            assistant.write(source, frame);
        }
    }

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    @Override
    public void retrieve(Frame frame, Consumer<Throwable> onError) {
        StreamAcceptor acceptor = acceptorManger.getAcceptor(frame.getMessage().sid());

        if (acceptor != null) {
            if (acceptor.isSingle() || frame.getFlag() == Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                acceptorManger.removeAcceptor(frame.getMessage().sid());
            }

            if (acceptor.isSingle()) {
                //单收时，内部已经是异步机制
                acceptor.accept(frame.getMessage(), onError);
            } else {
                //改为异步处理，避免卡死Io线程
                getConfig().getChannelExecutor().submit(() -> {
                    acceptor.accept(frame.getMessage(), onError);
                });
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("{} acceptor not found, sid={}, sessionId={}",
                        getConfig().getRoleName(), frame.getMessage().sid(), getSession().sessionId());
            }
        }
    }

    /**
     * 手动重连（一般是自动）
     */
    @Override
    public void reconnect() throws IOException {
        //由 ClientChannel 实现
    }

    /**
     * 获取会话
     */
    @Override
    public Session getSession() {
        if (session == null) {
            session = new SessionDefault(this);
        }

        return session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * 关闭
     */
    @Override
    public void close(int code) {

        if (log.isDebugEnabled()) {
            log.debug("{} channel will be closed, sessionId={}", getConfig().getRoleName(), getSession().sessionId());
        }

        super.close(code);

        try {
            assistant.close(source);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("{} channel close error, sessionId={}",
                        getConfig().getRoleName(), getSession().sessionId(), e);
            }
        }
    }
}