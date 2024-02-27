
const {SocketD}  = require('@noear/socket.d');
const {BrokerListener} = require("@noear/socket.d/broker/BrokerListener");
const {BrokerFragmentHandler} = require("@noear/socket.d/broker/BrokerFragmentHandler");
const {EventListener} = require("@noear/socket.d/transport/core/Listener");
const {StringEntity} = require("@noear/socket.d/transport/core/Entity");

async function main() {
    let server = SocketD.createServer("sd:ws")
        .config(c => c.port(8602).fragmentHandler(new BrokerFragmentHandler))
        .listen(new BrokerListener())
        .start();


    await SocketD.createClient("sd:ws://127.0.0.1:8602?@app1")
        .listen(new EventListener().doOn("hello", (s, m) => {
            s.reply(m, new StringEntity("me too!"));
        }))
        .openOrThow();

    let session2 = await SocketD.createClient("sd:ws://127.0.0.1:8602?@app2")
        .openOrThow();

    let r = await session2.sendAndRequest("hello", new StringEntity("hi").at("app1")).await();
    console.info(r.dataAsString());
}