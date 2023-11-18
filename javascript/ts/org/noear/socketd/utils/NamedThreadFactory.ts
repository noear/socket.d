/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.utils {
    /**
     * @param {string} namePrefix 名字前缀
     * @class
     * @author noear
     */
    export class NamedThreadFactory {
        /*private*/ namePrefix: string;

        /*private*/ threadCount: java.util.concurrent.atomic.AtomicInteger;

        /*private*/ __group: java.lang.ThreadGroup;

        /*private*/ __daemon: boolean;

        /*private*/ __priority: number;

        public constructor(namePrefix: string) {
            if (this.namePrefix === undefined) { this.namePrefix = null; }
            this.threadCount = new java.util.concurrent.atomic.AtomicInteger(0);
            if (this.__group === undefined) { this.__group = null; }
            this.__daemon = false;
            this.__priority = java.lang.Thread.NORM_PRIORITY;
            if (org.noear.socketd.utils.Utils.isEmpty$java_lang_String(namePrefix)){
                this.namePrefix = /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>this.constructor)) + "-";
            } else {
                this.namePrefix = namePrefix;
            }
        }

        /**
         * 线程组
         * @param {java.lang.ThreadGroup} group
         * @return {org.noear.socketd.utils.NamedThreadFactory}
         */
        public group(group: java.lang.ThreadGroup): NamedThreadFactory {
            this.__group = group;
            return this;
        }

        /**
         * 线程守护
         * @param {boolean} daemon
         * @return {org.noear.socketd.utils.NamedThreadFactory}
         */
        public daemon(daemon: boolean): NamedThreadFactory {
            this.__daemon = daemon;
            return this;
        }

        /**
         * 优先级
         * @param {number} priority
         * @return {org.noear.socketd.utils.NamedThreadFactory}
         */
        public priority(priority: number): NamedThreadFactory {
            this.__priority = priority;
            return this;
        }

        /**
         * 
         * @param {() => void} r
         * @return {java.lang.Thread}
         */
        public newThread(r: () => void): java.lang.Thread {
            const t: java.lang.Thread = new java.lang.Thread(this.__group, <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return () =>  (funcInst['run'] ? funcInst['run'] : funcInst) .call(funcInst)})(r)), this.namePrefix + this.threadCount.incrementAndGet());
            t.setDaemon(this.__daemon);
            t.setPriority(this.__priority);
            return t;
        }
    }
    NamedThreadFactory["__class"] = "org.noear.socketd.utils.NamedThreadFactory";

}

