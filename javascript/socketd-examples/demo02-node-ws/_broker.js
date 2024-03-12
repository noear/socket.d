
const {SocketD}  = require('@noear/socket.d');

async function main() {
    let server = SocketD.createServer("sd:ws")
        .config(c => c.port(8602).fragmentHandler(SocketD.newBrokerFragmentHandler()))
        .listen(SocketD.newBrokerListener())
        .start();


    await SocketD.createClient("sd:ws://127.0.0.1:8602?@=app1")
        .listen(SocketD.newEventListener().doOn("hello", (s, m) => {
            s.reply(m, SocketD.newEntity("me too!"));
        }))
        .openOrThow();

    let session2 = await SocketD.createClient("sd:ws://127.0.0.1:8602?@=app2")
        .openOrThow();

    let r = await session2.sendAndRequest("hello", SocketD.newEntity("hi").at("app1")).await();
    console.info(r.dataAsString());
}

main();