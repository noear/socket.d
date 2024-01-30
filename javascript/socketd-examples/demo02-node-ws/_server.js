const {SocketD}  = require('@noear/socket.d');
const {EventListener} = require("@noear/socket.d/transport/core/Listener");
const {EntityMetas} = require("@noear/socket.d/transport/core/Constants");


function main(){
   let server = SocketD.createServer("sd:ws")
       .config(c=>c.port(8602).fragmentSize(1024 * 1024))
       .listen(buildListener())
       .start();
}

function buildListener() {
    return new EventListener()
        .doOnOpen(s => {
            console.info("onOpen: " + s.sessionId());
        }).doOnMessage((s, m) => {
            console.info("onMessage: " + m);
        }).doOn("/demo", (s, m) => {
            if (m.isRequest()) {
                s.reply(m, SocketD.newEntity("me to!"));
            }

            if (m.isSubscribe()) {
                let size = m.rangeSize();
                for (let i = 1; i <= size; i++ ) {
                    s.reply(m, SocketD.newEntity("me to-" + i));
                }
                s.replyEnd(m, SocketD.newEntity("welcome to my home!"));
            }
        }).doOn("/upload", (s, m) => {
            if (m.isRequest()) {
                let fileName = m.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);
                if (fileName) {
                    s.reply(m, SocketD.newEntity("no file! size: " + m.dataSize()));
                } else {
                    s.reply(m, SocketD.newEntity("file received: " + fileName + ", size: " + m.dataSize()));
                }
            }
        }).doOn("/download", (s, m) => {
            if (m.isRequest()) {
                let fileEntity = SocketD.newEntity("...");//todo://SocketD.newEntity(new File("/Users/noear/Movies/snack3-rce-poc.mov"));
                s.reply(m, fileEntity);
            }
        }).doOn("/push", (s, m) => {
            if (s.attrHas("push")) {
                return;
            }

            s.attrPut("push", "1");

            for (let i = 0; i++; i < 100) {
                if (s.attrHas("push") == false) {
                    break;
                }

                s.send("/push", SocketD.newEntity("push test"));
                //todo:sleep
            }
        }).doOn("/unpush", (s, m) => {
            s.attrMap().remove("push");
        })
        .doOnClose(s => {
            console.info("onClose: " + s.sessionId());
        }).doOnError((s, err) => {
            console.warn("onError: " + s.sessionId());
        });
}

main();