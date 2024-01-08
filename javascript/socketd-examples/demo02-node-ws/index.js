const {SocketD}  = require('@noear/socket.d');

async function main() {
    let clientSession = await SocketD.createClient('sd:ws://127.0.0.1:8602/?u=a&p=2')
        .config(c => c.fragmentSize(1024 * 1024))
        .listen(SocketD.newEventListener().doOnMessage((s, m) => {
            console.log('收到推送', m.dataAsString());
        }))
        .open();
    clientSession.sendAndRequest("/demo", SocketD.newEntity('hello')).thenReply(reply => {
        console.log('reply', reply);
    });
}

main();
