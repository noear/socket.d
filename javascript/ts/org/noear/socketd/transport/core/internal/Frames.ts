/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 帧工厂
     * 
     * @author noear
     * @since 2.0
     * 
     * @class
     */
    export class Frames {
        /**
         * 构建连接帧
         * 
         * @param {string} url 连接地址
         * @param {string} sid
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public static connectFrame(sid: string, url: string): org.noear.socketd.transport.core.Frame {
            const entity: org.noear.socketd.transport.core.entity.EntityDefault = new org.noear.socketd.transport.core.entity.EntityDefault();
            entity.meta$java_lang_String$java_lang_String(org.noear.socketd.transport.core.EntityMetas.META_SOCKETD_VERSION, org.noear.socketd.SocketD.version());
            return new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Connect, new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(sid).topic$java_lang_String(url).entity$org_noear_socketd_transport_core_Entity(entity));
        }

        /**
         * 构建连接确认帧
         * 
         * @param {*} connectMessage 连接消息
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public static connackFrame(connectMessage: org.noear.socketd.transport.core.Message): org.noear.socketd.transport.core.Frame {
            const entity: org.noear.socketd.transport.core.entity.EntityDefault = new org.noear.socketd.transport.core.entity.EntityDefault();
            entity.meta$java_lang_String$java_lang_String(org.noear.socketd.transport.core.EntityMetas.META_SOCKETD_VERSION, org.noear.socketd.SocketD.version());
            return new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Connack, new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(connectMessage.sid()).topic$java_lang_String(connectMessage.topic()).entity$org_noear_socketd_transport_core_Entity(entity));
        }

        /**
         * 构建 ping 帧
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public static pingFrame(): org.noear.socketd.transport.core.Frame {
            return new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Ping, null);
        }

        /**
         * 构建 pong 帧
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public static pongFrame(): org.noear.socketd.transport.core.Frame {
            return new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Pong, null);
        }

        /**
         * 构建关闭帧（一般用不到）
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public static closeFrame(): org.noear.socketd.transport.core.Frame {
            return new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Close, null);
        }
    }
    Frames["__class"] = "org.noear.socketd.transport.core.internal.Frames";

}

