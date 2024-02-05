package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.bytebuf.ByteBufAllocator;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.PipeHandler;
import net.hasor.neta.handler.PipeRcvQueue;
import net.hasor.neta.handler.PipeSndQueue;
import net.hasor.neta.handler.PipeStatus;

import java.util.Objects;

/**
 * 固定长帧处理
 *
 * @author noear
 * @since 2.3
 */
public class FixedLengthFrameHandler implements PipeHandler<ByteBuf, ByteBuf> {
    private final int maxLength;
    private final ByteBufAllocator bufAllocator;

    public FixedLengthFrameHandler(int maxLength) {
        this(maxLength, ByteBufAllocator.DEFAULT);
    }

    public FixedLengthFrameHandler(int maxLength, ByteBufAllocator bufAllocator) {
        this.maxLength = maxLength;
        this.bufAllocator = Objects.requireNonNull(bufAllocator);
    }

    public PipeStatus onMessage(PipeContext context, PipeRcvQueue<ByteBuf> src, PipeSndQueue<ByteBuf> dst) {
        ByteBuf dstBuf = null;
        int dstSize = 0;

        while (src.hasMore() && dst.hasSlot()) {
            ByteBuf srcBuf = (ByteBuf) src.peekMessage();
            if (srcBuf == null) {
                break;
            }

            while (srcBuf.hasReadable() && dst.hasSlot()) {
                if (dstBuf == null) {
                    if (srcBuf.readableBytes() < Integer.BYTES) {
                        break;
                    }

                    dstSize = srcBuf.readInt32();

                    if (dstSize > maxLength) {
                        //todo:这个如果想异常退出，并关闭通道是不是这样处理？
                        return PipeStatus.Exit;
                    }

                    dstBuf = this.bufAllocator.buffer(dstSize);
                    dstBuf.writeInt32(dstSize);
                }

                this.fillLimitFrame(srcBuf, dstBuf, dstBuf.writableBytes());
                if (dstBuf.writerIndex() == dstSize) {
                    dstBuf.markWriter();
                    dst.offerMessage(dstBuf);
                    dstBuf = null;
                }
            }

            if (!srcBuf.hasReadable()) {
                src.skipMessage(1);
            }
        }

        if (dstBuf != null) {
            dstBuf.markWriter();
            dst.offerMessage(dstBuf);
        }

        return PipeStatus.Next;
    }

    private int fillLimitFrame(ByteBuf src, ByteBuf dst, int size) {
        int wlen = Math.min(src.readableBytes(), size - dst.writerIndex());
        int len = src.read(dst, wlen);
        src.markReader();
        return len;
    }
}
