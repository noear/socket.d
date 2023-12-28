// 连接是否开启
let isOpen = false;
// sd模块
let sd = null;

async function open(callback) {
    let serverUrl =  document.getElementById("serverUrl").value;
    if (!serverUrl) {
        alert('serverUrl不能为空!');
        return;
    }
    let clientSession = await sd.SocketD.createClient(serverUrl).open();
    console.log('session', clientSession);
    if (callback) callback();
}

function close(callback) {

    if (callback) callback();
}

function main() {
    require(['socketd/SocketD'], function (_sd) {
        sd = _sd;
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
    });
}
