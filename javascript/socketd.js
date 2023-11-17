var SimpleListener = /** @class */ (function () {
    function SimpleListener() {
    }
    SimpleListener.prototype.onOpen = function (session) {
    };
    SimpleListener.prototype.onMessage = function (session, message) {
    };
    SimpleListener.prototype.onClose = function (session) {
    };
    SimpleListener.prototype.onError = function (session, error) {
    };
    return SimpleListener;
}());
var Entity = /** @class */ (function () {
    function Entity() {
    }
    return Entity;
}());
var Message = /** @class */ (function () {
    function Message(sid, topic, entity) {
        this.sid = sid;
        this.topic = topic;
        this.entity = entity;
    }
    return Message;
}());
var Frame = /** @class */ (function () {
    function Frame(flag, message) {
        this.flag = flag;
        this.message = message;
    }
    return Frame;
}());
var ClientConfig = /** @class */ (function () {
    function ClientConfig(url) {
        this.url = url;
    }
    return ClientConfig;
}());
var SessionDefault = /** @class */ (function () {
    function SessionDefault(channel) {
        this.channel = channel;
    }
    SessionDefault.prototype.attr = function (name) {
        return undefined;
    };
    // @ts-ignore
    SessionDefault.prototype.attrMap = function () {
        return undefined;
    };
    SessionDefault.prototype.attrOrDefault = function (name, def) {
        return undefined;
    };
    SessionDefault.prototype.attrSet = function (name, value) {
    };
    SessionDefault.prototype.handshake = function () {
        return undefined;
    };
    SessionDefault.prototype.isValid = function () {
        return false;
    };
    SessionDefault.prototype.localAddress = function () {
        return undefined;
    };
    SessionDefault.prototype.param = function (name) {
        return "";
    };
    SessionDefault.prototype.paramOrDefault = function (name, value) {
        return "";
    };
    SessionDefault.prototype.path = function () {
        return "";
    };
    SessionDefault.prototype.pathNew = function (pathNew) {
    };
    SessionDefault.prototype.reconnect = function () {
    };
    SessionDefault.prototype.remoteAddress = function () {
        return undefined;
    };
    SessionDefault.prototype.reply = function (from, entity) {
    };
    SessionDefault.prototype.replyEnd = function (from, entity) {
    };
    SessionDefault.prototype.send = function (topic, entity) {
    };
    SessionDefault.prototype.sendAndRequest = function (topic, entity) {
        return undefined;
    };
    SessionDefault.prototype.sendAndSubscribe = function (topic, entity, consumer) {
    };
    SessionDefault.prototype.sendPing = function () {
    };
    SessionDefault.prototype.sessionId = function () {
        return "";
    };
    return SessionDefault;
}());
var ClientChannel = /** @class */ (function () {
    function ClientChannel(channel, connector) {
        this.real = channel;
        this.connector = connector;
    }
    ClientChannel.prototype.open = function () {
        return null;
    };
    ClientChannel.prototype.send = function (frame) {
    };
    ClientChannel.prototype.getHandshake = function () {
        return undefined;
    };
    ClientChannel.prototype.setHandshake = function (handshake) {
    };
    ClientChannel.prototype.setSession = function () {
        return undefined;
    };
    ClientChannel.prototype.getSession = function () {
        return undefined;
    };
    return ClientChannel;
}());
var Client = /** @class */ (function () {
    function Client(cfg) {
        this._config = cfg;
    }
    Client.prototype.config = function (consumer) {
        consumer(this._config);
        return this;
    };
    Client.prototype.listen = function (listener) {
        return this;
    };
    Client.prototype.onOpen = function (fun) {
        this._onOpen = fun;
        return this;
    };
    Client.prototype.onMessage = function (fun) {
        this._onMessage = fun;
        return this;
    };
    Client.prototype.on = function (topic, fun) {
        this._onMap.set(topic, fun);
        return this;
    };
    Client.prototype.onClose = function (fun) {
        this._onClose = fun;
        return this;
    };
    Client.prototype.onError = function (fun) {
        this._onError = fun;
        return this;
    };
    Client.prototype.open = function () {
        var channel0 = this._connector.connect();
        var clientChannel = new ClientChannel(channel0, this._connector);
        //同步握手信息
        clientChannel.setHandshake(channel0.getHandshake());
        var session = new SessionDefault(clientChannel);
        //原始通道切换为带壳的 session
        channel0.setSession(session);
        return session;
    };
    return Client;
}());
/**
 * let connentor = new ClientConnector(this.config);
 *  return new Session(new ClientChannel(connentor.connect(), connentor));
 * */
var SocketD = {
    createClient: function (url) {
        return new Client(new ClientConfig(url));
    }
};
var session = SocketD.createClient("tcp://xxx.xxx.x")
    .config(function (cfg) {
    cfg.replyTimeout = 12;
}).listen({
    onOpen: function (session) {
    },
    onMessage: function (session, message) {
    },
    onClose: function (session) {
    },
    onError: function (session, error) {
    }
}).open();
session.send("/demo", new Entity());
var entity = session.sendAndRequest("/demo", new Entity());
session.sendAndSubscribe("/demo", new Entity(), function (entity) {
});
