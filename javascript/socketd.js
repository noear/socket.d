
//-------------------------------------------------
//<script src="/org.noear/socket.d.js"></script>
let session = SocketD.createClient("sd:ws://...")
    .config(c=>c.replyTimeout(12))
    .onOpen(s=>{
        //
    })
    .onMessage((s,m)=>{
        //
    })
    .on("mq.rev",(s,m)=>{
        //
    }).open();


session.send("xxx", {data:""});
session.send("xxx", {meta:{user:1}, data:""});
let response = session.sendAndRequest("xxx", {data:""});
session.sendAndSubscribe("xxx", {data:"xxx"}, stream=>{
    stream.getMetaString();
    stream.getMetaMap();
    stream.getMeta("");
    stream.getData();
    stream.getDataAsString();
    stream.getDataAsBytes();
});