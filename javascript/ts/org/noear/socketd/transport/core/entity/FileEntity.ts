/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.entity {
    /**
     * 文件实体
     * 
     * @author noear
     * @since 2.0
     * @param {java.io.File} file
     * @class
     * @extends org.noear.socketd.transport.core.entity.EntityDefault
     */
    export class FileEntity extends org.noear.socketd.transport.core.entity.EntityDefault {
        public constructor(file: java.io.File) {
            super();
            this.data$java_io_InputStream(new java.io.FileInputStream(file));
            this.meta$java_lang_String$java_lang_String(org.noear.socketd.transport.core.EntityMetas.META_DATA_DISPOSITION_FILENAME, file.getName());
        }
    }
    FileEntity["__class"] = "org.noear.socketd.transport.core.entity.FileEntity";
    FileEntity["__interfaces"] = ["org.noear.socketd.transport.core.Entity"];


}

