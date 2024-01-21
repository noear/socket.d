package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import org.noear.socketd.transport.core.CodecWriter;

import java.io.IOException;

/**
 * @author noear 2024/1/21 created
 */
public class ByteBufCodecWriter implements CodecWriter {
    public ByteBufCodecWriter(int size){

    }
    @Override
    public void putBytes(byte[] bytes) throws IOException {

    }

    @Override
    public void putInt(int val) throws IOException {

    }

    @Override
    public void putChar(int val) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    public ByteBuf buffer(){
        return null;
    }
}
