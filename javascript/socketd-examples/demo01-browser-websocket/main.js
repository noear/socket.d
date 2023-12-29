// 连接是否开启
let isOpen = false;
// sd模块
let sd = null;
let entity = null;
let clientSession = null;

async function open(callback) {
    let serverUrl =  document.getElementById("serverUrl").value;
    if (!serverUrl) {
        alert('serverUrl不能为空!');
        return;
    }
    clientSession = await sd.SocketD.createClient(serverUrl).open();
    console.log('session', clientSession);
    if (callback) callback();
}

function close(callback) {
    clientSession.close();
    if (callback) callback();
}

function send() {
    let input = document.getElementById("input").value;
    if (!input) {
        alert("输入消息不能为空!");
        return;
    }
    appendToMessageList('发送', input);
    clientSession.sendAndSubscribe("/demo", new entity.StringEntity(input), reply=> {
        console.log('reply', reply);
        appendToMessageList('响应', reply.dataAsString());
    });
}

function appendToMessageList(hint, msg) {
    let ele = document.getElementById("message");
    ele.value += `[${dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss.SSS')}] ${hint}：${msg}\n`;
    ele.scrollTop = ele.scrollHeight;
}

function main() {
    require(['socketd/SocketD', 'socketd/transport/core/Entity'], function (_sd, _entity) {
        sd = _sd;
        entity = _entity;
        document.getElementById("openBtn").addEventListener("click", function(){
            if (isOpen) {
                close(() => {
                    document.getElementById("openBtn").innerHTML = '连接';
                    isOpen = false;
                });
            } else {
                open(() => {
                    document.getElementById("openBtn").innerHTML = '关闭';
                    isOpen = true;
                });
            }

        });
        document.getElementById("send").addEventListener("click", function(){
            if (isOpen) {
                send();
            }
        });
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
