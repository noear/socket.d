package org.noear.socketd.transport.java_tcp_nio;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.java_tcp_nio.impl.NioAttachment;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author noear
 * @since 2.4
 */
public class TcpNioServer extends ServerBase<TcpNioChannelAssistant> implements ChannelSupporter<SocketChannel> {

    public TcpNioServer(ServerConfig config) {
        super(config, new TcpNioChannelAssistant(config));
    }

    @Override
    public String getTitle() {
        return "tcp/nio/java-tcp/" + SocketD.version();
    }

    private Selector selector;
    private Thread selectThread;

    private ServerSocketChannel serverSocketChannel;
    private ExecutorService serverExecutor;

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        if (StrUtils.isEmpty(getConfig().getHost())) {
            serverSocketChannel.socket().bind(new InetSocketAddress(getConfig().getPort()));
        } else {
            serverSocketChannel.socket().bind(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()));
        }

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectThread = new Thread(this::select0);
        selectThread.start();

        serverExecutor = Executors.newFixedThreadPool(getConfig().getCodecThreads());

        return this;
    }

    private void select0() {
        while (selectThread.isInterrupted() == false) {
            try {
                if (selector.select() > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        try {
                            onSelect(selectionKey);
                        } catch (IOException e) {
                            onError((NioAttachment) selectionKey.attachment(), e);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                //stop();
            } catch (ClosedSelectorException e){
                stop();
                return;
            }
        }
    }

    private void onSelect(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();

            SocketChannel socketChannel = serverChannel.accept();

            //闲置超时
            if (getConfig().getIdleTimeout() > 0L) {
                //单位：毫秒
                socketChannel.socket().setSoTimeout((int) getConfig().getIdleTimeout());
            }

            //读缓冲
            if (getConfig().getReadBufferSize() > 0) {
                socketChannel.socket().setReceiveBufferSize(getConfig().getReadBufferSize());
            }

            //写缓冲
            if (getConfig().getWriteBufferSize() > 0) {
                socketChannel.socket().setSendBufferSize(getConfig().getWriteBufferSize());
            }


            socketChannel.configureBlocking(false);

            NioAttachment attachment = new NioAttachment(getConfig());
            socketChannel.register(selector, SelectionKey.OP_READ, attachment);

            this.onConnect(socketChannel, attachment);
        } else if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            NioAttachment socketAttachment = (NioAttachment) selectionKey.attachment();

            //serverExecutor.submit(()->{
                onRead(socketChannel, socketAttachment);
            //});

        } else if (selectionKey.isWritable()) {
            // 处理写事件
        }

        if (selectionKey.isValid() == false) {
            onClose((NioAttachment) selectionKey.attachment());
        }
    }

    private void onConnect(SocketChannel socket, NioAttachment attachment) throws IOException{
        ChannelInternal channel = new ChannelDefault<>(socket, this);
        attachment.channelInternal = channel;

        //getProcessor().onOpen(attachment.channelInternal);
    }

    private void onRead(SocketChannel socket, NioAttachment attachment) {
        ByteBuffer buffer = attachment.buffer;

        try {
            int len = -1;
            while ((len = socket.read(buffer)) > 0) {
                buffer.flip();
                this.onRead0(socket, attachment, buffer);

                if (buffer.hasRemaining() == false) {
                    buffer.clear();
                }
            }
        }catch (IOException e){
            onClose(attachment);
            onError(attachment, e);
        }
    }

    private void onRead0(SocketChannel socket, NioAttachment attachment, ByteBuffer buffer) {
        Frame frame = getAssistant().read(socket, attachment, buffer);
        if (frame != null) {
            if (buffer.hasRemaining()) {
                buffer.compact();
            }
            getProcessor().reveFrame(attachment.channelInternal, frame);
        }
    }


    private void onClose(NioAttachment attachment) {
        if (attachment != null && attachment.channelInternal != null) {
            getProcessor().onClose(attachment.channelInternal);
        }
    }

    private void onError(NioAttachment attachment, Throwable err) {
        if (attachment != null && attachment.channelInternal != null) {
            getProcessor().onError(attachment.channelInternal, err);
        }
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        super.stop();

        if (selector != null) {
            RunUtils.runAndTry(selector::close);
        }

        if (serverSocketChannel != null) {
            RunUtils.runAndTry(serverSocketChannel::close);
        }

        selectThread.interrupt();
    }
}