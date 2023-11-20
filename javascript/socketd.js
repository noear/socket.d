
function Client(){

}

Client.prototype.open = function (url) {
    this.url = url;
    this.socket = new WebSocket(url);

    this.socket.onopen = function () {
        this.sendConnect(url);
    }

    this.socket.onmessage = function (e) {
        let frame = this.readFrame(e);

        onReceive(frame);
    }


    this.socket.onclose = function (e) {
        this.onClose(e);
    }

    this.socket.onerror = function (e) {
        this.onError(e);
    }
}

Client.prototype.sendConnect = function (url){
    this.socket.send()
}

Client.prototype.readFrame = function (e:MessageEvent) {
    return {flag: 1, sid: '', topic: '', metaString: '', data: []}
}

Client.prototype.onReceive = function (e){

}

Client.prototype.onOpen = function (e){

}

Client.prototype.onClose = function (e){

}

Client.prototype.onError = function (e){

}


///////

let client = new Client();


var sock = null;
//服务器的地址
var wsuri = "ws://127.0.0.1:18080/demoe/websocket/13?guid=2";


function send() {
    var msg = document.getElementById('message').value;
    sock.send(msg);
};