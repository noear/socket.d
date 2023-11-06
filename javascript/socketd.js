function Consumer(T){
}
function Entity(metaString: string, data: object) {
    this.metaString = metaString;
    this.data = data;
}

function Message(sid:string, entity:Entity){
    this.sid = sid;
    this.entity = entity;
}

function Frame() {
    this.flag;
    this.message;
}

function Channel(config){
    this.config = config;
    this.send(frame)
}

function Session(channel: Channel) {
    this.channel = channel;

    this.send = function (topic:string, entity:Entity){

    }
    this.sendAndRequest = function (topic:string, entity:Entity) : Entity{
        return null;
    }
    this.sendAndSubscribe = function (topic:string, entity:Entity, consumer:Consumer){

    }
}

function ClientConfig(url:string){
    this.url = url;
    this.schema="";
}

function ClientConnector(config: ClientConfig){
    this.config = config;

    this.connect = function ():Channel{

    }
}

function ClientChannel(channel:Channel, connector:ClientConnector) {
    this.channel = channel;
    this.connector = connector;
}

function Client(config: ClientConfig){
    this.config = config;

    this.config = function (configNew:ClientConfig):Client{
        return this;
    }
    this.listen = function (listener){
        return this;
    }
    this.open = function ():Session{
        let connentor = new ClientConnector(this.config);
        return new Session(new ClientChannel(connentor.connect(), connentor));
    }
}

var SocketD={
    createClient:function (url) : Client {
        let config = new ClientConfig(url);
        return new Client(url);
    }
}

let session = SocketD.createClient("tcp://xxx.xxx.x")
    .config({replyTimeout: 12})
    .listen({
        onOpen: function (session){

        },
        onMessage: function (session, message){

        },
        onClose: function (session){

        },
        onError: function (session, error){

        }
    })
    .open();

session.send("/demo", {data: ""});

let entity = session.sendAndRequest("/demo", {data: ""});
session.sendAndSubscribe("/demo",{data:""},  entity=>{

});