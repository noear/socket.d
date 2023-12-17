import {SocketD} from "./socketd/SocketD";
import {StringEntity} from "./socketd/transport/core/Entity";


//服务器的地址
var serverUrl = "sd:ws://127.0.0.1:18080/demoe/websocket/13?guid=2";

let clientSession = SocketD.createClient(serverUrl)
    .onOpen(function (s){

    })
    .on("demo", function (s,m){

    })
    .open();

clientSession.send('demo',new StringEntity('').metaMapSet({'a':'1','b':'2'}));

clientSession.sendAndRequest('demo',new StringEntity('').metaMapSet({'a':'1','b':'2'}),reply=>{

});
clientSession.sendAndSubscribe('demo',new StringEntity('').metaMapSet({'a':'1','b':'2'}), reply=>{

});