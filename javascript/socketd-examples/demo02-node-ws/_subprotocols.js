
const {SocketD}  = require('@noear/socket.d');

async function main() {
    let server = SocketD.createServer("sd:ws")
        .config(c => c.port(8602).useSubprotocols(true))
        .listen(SocketD.newEventListener().doOnOpen(s=>{
            console.log("..................: " + s.sessionId());
        }).doOnMessage((s,m)=>{
            console.log("..................: " + m.dataAsString());
        }))
        .start();

    let serverUrl = "ws://127.0.0.1:8602/path?u=a&p=2";


    let session = await SocketD.createClient(serverUrl)
        .config(c => c.useSubprotocols(true))
        .openOrThow();

    session.send("/demo", SocketD.newEntity("hello"));
}

main();