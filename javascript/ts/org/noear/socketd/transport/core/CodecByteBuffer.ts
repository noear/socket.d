import {Codec} from "./Codec";

/**
 * 编解码器（基于 ByteBuffer 编解）
 *
 * @author noear
 * @since 2.0
 * @param {*} config
 * @class
 */
export class CodecByteBuffer implements Codec<BufferReader, org.noear.socketd.transport.core.buffer.BufferWriter> {
    /*private*/ config: org.noear.socketd.transport.core.Config;

    public constructor(config: org.noear.socketd.transport.core.Config) {
        if (this.config === undefined) { this.config = null; }
        this.config = config;
    }

    public write$org_noear_socketd_transport_core_Frame$java_util_function_Function<T extends org.noear.socketd.transport.core.buffer.BufferWriter>(frame: org.noear.socketd.transport.core.Frame, factory: (p1: number) => T): T {
        if (frame.getMessage() == null){
            const frameSize: number = javaemul.internal.IntegerHelper.BYTES + javaemul.internal.IntegerHelper.BYTES;
            const target: T = (target => (typeof target === 'function') ? target(frameSize) : (<any>target).apply(frameSize))(factory);
            target.putInt(frameSize);
            target.putInt(org.noear.socketd.transport.core.Flag["_$wrappers"][frame.getFlag()].getCode());
            target.flush();
            return target;
        } else {
            const sidB: number[] = /* getBytes */(frame.getMessage().sid()).split('').map(s => s.charCodeAt(0));
            const topicB: number[] = /* getBytes */(frame.getMessage().topic()).split('').map(s => s.charCodeAt(0));
            const metaStringB: number[] = /* getBytes */(frame.getMessage().metaString()).split('').map(s => s.charCodeAt(0));
            const frameSize: number = javaemul.internal.IntegerHelper.BYTES + javaemul.internal.IntegerHelper.BYTES + sidB.length + topicB.length + metaStringB.length + frame.getMessage().dataSize() + javaemul.internal.ShortHelper.BYTES * 3;
            this.assertSize("sid", sidB.length, org.noear.socketd.transport.core.Config.MAX_SIZE_SID);
            this.assertSize("topic", topicB.length, org.noear.socketd.transport.core.Config.MAX_SIZE_TOPIC);
            this.assertSize("metaString", metaStringB.length, org.noear.socketd.transport.core.Config.MAX_SIZE_META_STRING);
            this.assertSize("data", frame.getMessage().dataSize(), org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT);
            const target: T = (target => (typeof target === 'function') ? target(frameSize) : (<any>target).apply(frameSize))(factory);
            target.putInt(frameSize);
            target.putInt(org.noear.socketd.transport.core.Flag["_$wrappers"][frame.getFlag()].getCode());
            target['putBytes$byte_A'](sidB);
            target.putChar(('\n').charCodeAt(0));
            target['putBytes$byte_A'](topicB);
            target.putChar(('\n').charCodeAt(0));
            target['putBytes$byte_A'](metaStringB);
            target.putChar(('\n').charCodeAt(0));
            org.noear.socketd.utils.IoUtils.writeTo(frame.getMessage().data(), target);
            target.flush();
            return target;
        }
    }

    /**
     * 编码
     * @param {org.noear.socketd.transport.core.Frame} frame
     * @param {*} factory
     * @return {*}
     */
    public write<T0 = any>(frame?: any, factory?: any): any {
        if (((frame != null && frame instanceof <any>org.noear.socketd.transport.core.Frame) || frame === null) && ((typeof factory === 'function' && (<any>factory).length === 1) || factory === null)) {
            return <any>this.write$org_noear_socketd_transport_core_Frame$java_util_function_Function(frame, factory);
        } else throw new Error('invalid overload');
    }

    public read$org_noear_socketd_transport_core_buffer_BufferReader(buffer: org.noear.socketd.transport.core.buffer.BufferReader): org.noear.socketd.transport.core.Frame {
        const frameSize: number = buffer.getInt();
        if (frameSize > (buffer.remaining() + javaemul.internal.IntegerHelper.BYTES)){
            return null;
        }
        const flag: number = buffer.getInt();
        if (frameSize === 8){
            return new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag_$WRAPPER.Of(flag), null);
        } else {
            const metaBufSize: number = Math.min(org.noear.socketd.transport.core.Config.MAX_SIZE_META_STRING, buffer.remaining());
            const sb: java.nio.ByteBuffer = java.nio.ByteBuffer.allocate(metaBufSize);
            const sid: string = this.decodeString(buffer, sb, org.noear.socketd.transport.core.Config.MAX_SIZE_SID);
            const topic: string = this.decodeString(buffer, sb, org.noear.socketd.transport.core.Config.MAX_SIZE_TOPIC);
            const metaString: string = this.decodeString(buffer, sb, org.noear.socketd.transport.core.Config.MAX_SIZE_META_STRING);
            const dataRealSize: number = frameSize - buffer.position();
            let data: number[];
            if (dataRealSize > org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT){
                data = (s => { let a=[]; while(s-->0) a.push(0); return a; })(org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT);
                buffer['get$byte_A$int$int'](data, 0, org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT);
                for(let i: number = dataRealSize - org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT; i > 0; i--) {{
                    buffer['get$']();
                };}
            } else {
                data = (s => { let a=[]; while(s-->0) a.push(0); return a; })(dataRealSize);
                if (dataRealSize > 0){
                    buffer['get$byte_A$int$int'](data, 0, dataRealSize);
                }
            }
            const message: org.noear.socketd.transport.core.internal.MessageDefault = new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(sid).topic$java_lang_String(topic).entity$org_noear_socketd_transport_core_Entity(new org.noear.socketd.transport.core.entity.EntityDefault().metaString$java_lang_String(metaString).data$byte_A(data));
            message.flag(org.noear.socketd.transport.core.Flag_$WRAPPER.Of(flag));
            return new org.noear.socketd.transport.core.Frame(message.getFlag(), message);
        }
    }

    /**
     * 解码
     * @param {*} buffer
     * @return {org.noear.socketd.transport.core.Frame}
     */
    public read(buffer?: any): org.noear.socketd.transport.core.Frame {
        if (((buffer != null && (buffer.constructor != null && buffer.constructor["__interfaces"] != null && buffer.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.buffer.BufferReader") >= 0)) || buffer === null)) {
            return <any>this.read$org_noear_socketd_transport_core_buffer_BufferReader(buffer);
        } else throw new Error('invalid overload');
    }

    decodeString(reader: org.noear.socketd.transport.core.buffer.BufferReader, buf: java.nio.ByteBuffer, maxLen: number): string {
        buf.clear();
        while((true)) {{
            const c: number = reader['get$']();
            if (c === 10){
                break;
            }
            if (maxLen > 0 && maxLen <= buf.position()){
            } else {
                if (c !== 0){
                    buf.put(c);
                }
            }
        }};
        buf.flip();
        if (buf.limit() < 1){
            return "";
        }
        return String.fromCharCode.apply(null, buf.array()).substr(0, buf.limit());
    }

    /*private*/ assertSize(name: string, size: number, limitSize: number) {
        if (size > limitSize){
            const buf: { str: string, toString: Function } = { str: "", toString: function() { return this.str; } };
            /* append */(sb => { sb.str += <any>")"; return sb; })(/* append */(sb => { sb.str += <any>size; return sb; })(/* append */(sb => { sb.str += <any>" ("; return sb; })(/* append */(sb => { sb.str += <any>limitSize; return sb; })(/* append */(sb => { sb.str += <any>" size is out of limit "; return sb; })(/* append */(sb => { sb.str += <any>name; return sb; })(/* append */(sb => { sb.str += <any>"This message "; return sb; })(buf)))))));
            throw new org.noear.socketd.exception.SocketdSizeLimitException(/* toString */buf.str);
        }
    }
}
