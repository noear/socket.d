// 连接是否开启

let isOpen = false;

async function open(callback) {
    let serverUrl =  document.getElementById("serverUrl").value;
    if (!serverUrl) {
        alert('serverUrl不能为空!');
        return;
    }
    window.clientSession = await SocketD.createClient(serverUrl)
        .listen(new EventListener().doOnMessage((s,m)=>{
            appendToMessageList('收到推送', m.dataAsString());
        }))
        .open();
    console.log('session', clientSession);
    if (callback) callback();
}

function close(callback) {
    clientSession.close();
    if (callback) callback();
}

function send(type) {
    let input = document.getElementById("input").value;
    if (!input) {
        alert("输入消息不能为空!");
        return;
    }


    if (type == 1) {
        appendToMessageList('发送并请求', input);
        clientSession.sendAndRequest("/demo", new StringEntity(input), reply => {
            console.log('reply', reply);
            appendToMessageList('答复', reply.dataAsString());
        }, 3000);
    } else if (type == 2) {
        appendToMessageList('发送并订阅', input);
        clientSession.sendAndSubscribe("/demo", new StringEntity(input), reply => {
            console.log('reply', reply);
            if(reply.isEnd()){
                appendToMessageList('答复结束', reply.dataAsString());
            }else{
                appendToMessageList('答复', reply.dataAsString());
            }
        }, 3000);
    } else {
        appendToMessageList('发送', input);
        clientSession.send("/demo", new StringEntity(input));
    }
}

function appendToMessageList(hint, msg) {
    let ele = document.getElementById("message");
    ele.value = `[${dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss.SSS')}] ${hint}：${msg}\n` + ele.value;

}

function main() {
    require(['socketd/SocketD','socketd/transport/core/Entity','socketd/transport/core/Listener'], (sd,en,ls) => {
        window.SocketD = sd.SocketD;
        window.StringEntity = en.StringEntity;
        window.EventListener = ls.EventListener;
        mainDo();
    });
}

function mainDo(){
    let openBtn = document.getElementById("openBtn");
    openBtn.addEventListener("click", function () {
        if (isOpen) {
            close(() => {
                openBtn.innerHTML = '连接';
                isOpen = false;
            });
        } else {
            open(() => {
                openBtn.innerHTML = '关闭';
                isOpen = true;
            });
        }

    });
    let send0 = document.getElementById("send");
    let send1 = document.getElementById("sendAndRequest");
    let send2 = document.getElementById("sendAndSubscribe");
    send0.addEventListener("click", () => {
        if (isOpen) {
            send(0);
        }
    });

    send1.addEventListener("click", () => {
        if (isOpen) {
            send(1);
        }
    });

    send2.addEventListener("click", () => {
        if (isOpen) {
            send(2);
        }
    });


    let push = document.getElementById("push");
    let unpush = document.getElementById("unpush");

    push.addEventListener("click", () => {
        if (isOpen) {
            clientSession.send("/push", new StringEntity(""));
        }
    });

    unpush.addEventListener("click", () => {
        if (isOpen) {
            clientSession.send("/unpush", new StringEntity(""))
        }
    });
}

// 日期格式化
function dateFormat(date,fmt) {
    // 默认格式
    fmt = fmt ? fmt : 'yyyy-MM-dd hh:mm:ss';

    var o = {
        "M+" : date.getMonth()+1,                 // 月份
        "d+" : date.getDate(),                    // 日
        "h+" : date.getHours(),                   // 小时
        "m+" : date.getMinutes(),                 // 分
        "s+" : date.getSeconds(),                 // 秒
        "q+" : Math.floor((date.getMonth()+3)/3), // 季度
        "S+"  : date.getMilliseconds()             // 毫秒
    };
    if(/(y+)/.test(fmt)) {
        fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) :
                RegExp.$1.length==2 ? (("00"+ o[k]).substr((""+ o[k]).length)) : (("000"+ o[k]).substr((""+ o[k]).length))
            );
        }
    }
    return fmt;
}
