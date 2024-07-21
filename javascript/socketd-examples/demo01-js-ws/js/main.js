let isOpen = false;

window.onload = mainDo;

async function open(callback) {
    let serverUrl = document.getElementById("serverUrl").value;
    if (!serverUrl) {
        alert('serverUrl不能为空!');
        return;
    }
    await SocketD.createClient(serverUrl.trim())
        .config(c => c
            .useSubprotocols(false)
            .heartbeatInterval(1000*5)
            .fragmentSize(1024 * 1024)
            .metaPut("test","1"))
        .connectHandler(c=> {
            console.log("connect begin...");
            c.getConfig().metaPut("test","1");
            return c.connect();
        })
        .listen(SocketD.newEventListener()
            .doOnOpen(s=>{
                window.clientSession = s;
                console.log("outmeta: test=" + s.handshake().param("test"))
            })
            .doOnMessage((s, m) => {
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
        clientSession.sendAndRequest("/demo", SocketD.newEntity(input)).thenReply(reply => {
            console.log('reply', reply);
            appendToMessageList('答复', reply.dataAsString());
        });
    } else if (type == 2) {
        appendToMessageList('发送并订阅', input);
        clientSession.sendAndSubscribe("/demo", SocketD.newEntity(input)
            .metaPut(SocketD.EntityMetas.META_RANGE_SIZE,"3")).thenReply(reply => {
            console.log('reply', reply);
            if(reply.isEnd()){
                appendToMessageList('答复结束', reply.dataAsString());
            }else{
                appendToMessageList('答复', reply.dataAsString());
            }
        });
    } else {
        appendToMessageList('发送', input);
        clientSession.send("/demo", SocketD.newEntity(input));
    }
}

function appendToMessageList(hint, msg) {
    let ele = document.getElementById("message");
    ele.value = `[${dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss.SSS')}] ${hint}：${msg}\n` + ele.value;

}



function mainDo() {
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
            clientSession.send("/push", SocketD.newEntity());
        }
    });

    unpush.addEventListener("click", () => {
        if (isOpen) {
            clientSession.send("/unpush", SocketD.newEntity())
        }
    });


    let uploadFile = document.getElementById("uploadFile");
    let downloadFile = document.getElementById("downloadFile");
    let uploadData = document.getElementById("uploadData");
    uploadFile.addEventListener("click", async () => {
        if (isOpen) {
            const files = document.getElementById("file").files;
            if (!files || files.length == 0) {
                alert("请选择文件");
                return;
            }

            const file1 = document.getElementById("file").files[0];

            appendToMessageList('发送文件并请求', file1.name);
            clientSession.sendAndRequest("/upload", SocketD.newEntity(file1), 100_000).thenReply(reply => {
                console.log('reply', reply);
                appendToMessageList('答复', reply.dataAsString());
            }).thenProgress((isSend, val, max) => {
                if (isSend) {
                    appendToMessageList('发送进度', val + "/" + max);
                }
            });
        }
    });

    downloadFile.addEventListener("click", async () => {
        if (isOpen) {
            appendToMessageList('下载文件', "...");
            clientSession.sendAndRequest("/download", SocketD.newEntity()).thenReply(reply => {
                console.log('reply', reply);

                const fileName = reply.meta(SocketD.EntityMetas.META_DATA_DISPOSITION_FILENAME);
                if (fileName) {
                    appendToMessageList('下载文件', "file=" + fileName + ", size=" + reply.dataSize());
                } else {
                    appendToMessageList('下载文件', "没有收到文件:(");
                }
            }).thenProgress((isSend,val,max)=>{
                if(!isSend){
                    appendToMessageList('下载进度', val + "/" + max);
                }
            });
        }
    });

    uploadData.addEventListener("click", async () => {
        if (isOpen) {
            const strSize = 1024*1024*10;
            let str = "";
            while (str.length < strSize) {
                str += "1234567890"
            }

            appendToMessageList('提交大文本块10M', "...");
            clientSession.sendAndRequest("/upload", SocketD.newEntity(str)).thenReply(reply => {
                console.log('reply', reply);
                appendToMessageList('答复', reply.dataAsString());
            }).thenProgress((isSend, val,max)=> {
                if(isSend){
                    appendToMessageList('提交进度', val + "/" + max);
                }
            });
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
