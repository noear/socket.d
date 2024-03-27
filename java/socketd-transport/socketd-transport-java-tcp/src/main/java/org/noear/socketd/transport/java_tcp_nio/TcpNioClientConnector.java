package org.noear.socketd.transport.java_tcp_nio;

import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.java_tcp_nio.impl.NioAttachment;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.4
 */
public class TcpNioClientConnector extends ClientConnectorBase<TcpNioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioClientConnector.class);

    private Selector selector;
    private Thread selectThread;

    private SocketChannel real;

    private CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

    public TcpNioClientConnector(TcpNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        selector = Selector.open();
        real = SocketChannel.open();

        try {
            real.configureBlocking(false);

            if (StrUtils.isEmpty(getConfig().getHost())) {
                real.connect(new InetSocketAddress(getConfig().getPort()));
            } else {
                real.connect(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()));
            }

            real.register(selector, SelectionKey.OP_CONNECT);

            selectThread = new Thread(this::select0);
            selectThread.start();


            //等待握手结果
            ClientHandshakeResult handshakeResult = handshakeFuture.get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketDConnectionException("Connection timeout: " + client.getConfig().getLinkUrl());
        } catch (Throwable e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketDConnectionException("Connection failed: " + client.getConfig().getLinkUrl(), e);
            }
        }
    }

    private void select0(){
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
                close();
                return;
            }
        }
    }

    private void onSelect(SelectionKey selectionKey) throws IOException{
        if (selectionKey.isConnectable()) {
            // 处理连接事件
            SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

            if(socketChannel.finishConnect()) {
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
            }
        } else if (selectionKey.isReadable()) {
            // 处理读取事件
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            NioAttachment socketAttachment = (NioAttachment) selectionKey.attachment();
            ByteBuffer buffer = socketAttachment.buffer;

            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                this.onRead(socketChannel, socketAttachment, buffer);

                if(buffer.hasRemaining()==false){
                    buffer.clear();
                }
            }

        } else if (selectionKey.isWritable()) {
            // 处理写事件
        }

        if (selectionKey.isValid() == false) {
            onClose((NioAttachment) selectionKey.attachment());
        }
    }

    private void onConnect(SocketChannel socket, NioAttachment attachment) throws IOException{
        ChannelInternal channel = new ChannelDefault<>(socket, client);
        attachment.channelInternal = channel;

        //开始发连接包
        channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());

        //client.getProcessor().onOpen(attachment.channelInternal);
    }

    private void onRead(SocketChannel socket, NioAttachment attachment, ByteBuffer buffer) {
        Frame frame = client.getAssistant().read(socket, attachment, buffer);

        if (frame != null) {
            if (buffer.hasRemaining()) {
                buffer.compact();
            }

            if (frame.flag() == Flags.Connack) {
                attachment.channelInternal.onOpenFuture((r, e) -> {
                    handshakeFuture.complete(new ClientHandshakeResult(attachment.channelInternal, e));
                });
            }

            client.getProcessor().onReceive(attachment.channelInternal, frame);
        }
    }


    private void onClose(NioAttachment attachment) {
        if (attachment != null && attachment.channelInternal != null) {
            client.getProcessor().onClose(attachment.channelInternal);
        }
    }

    private void onError(NioAttachment attachment, Throwable err) {
        if (attachment != null && attachment.channelInternal != null) {
            client.getProcessor().onError(attachment.channelInternal, err);
        }
    }

    @Override
    public void close() {
        if (selector != null) {
            RunUtils.runAndTry(selector::close);
        }

        if (real != null) {
            RunUtils.runAndTry(real::close);
        }

        selectThread.interrupt();
    }
}
