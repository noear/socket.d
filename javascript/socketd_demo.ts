import {Entity} from "./src/socketd/transport/core/Entity";
import {SocketD} from "./src/socketd/Socketd";

let session = SocketD.createClient("tcp://xxx.xxx.x")
    .config(cfg => {
        cfg.replyTimeout = 12
    })
    .listen({
        onOpen: function (session) {

        },
        onMessage: function (session, message) {

        },
        onClose: function (session) {

        },
        onError: function (session, error) {

        }
    })
    .open();

session.send("/demo", new Entity());

let entity = session.sendAndRequest("/demo", new Entity());
session.sendAndSubscribe("/demo", new Entity(), entity => {

});