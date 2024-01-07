export const LOG_LEVEL = {
    all: {
        v: 0
    },
    debug: {
        v: 1,
        color: 'gray'
    },
    info: {
        v: 2,
        color: 'green'
    },
    warn: {
        v: 3,
        color: 'blue'
    },
    error: {
        v: 4,
        color: 'red'
    },
    off: {
        v: 5
    }
};

let _level = 'info';

export class Logger {
    private _name: string;
    constructor(name) {
        this._name = name;
    }

    private out(curLevel: string, msg: string[] | any[]) {
        if (LOG_LEVEL[curLevel].v < LOG_LEVEL[_level].v) {
            return;
        }
        let prefix = `%c [SocketD][${dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss.SSS')}][${this._name}][level:${curLevel}]`;
        let params: string[] | any[] = [];
        params.push(prefix);
        params.push(`color: ${LOG_LEVEL[curLevel].color}`);
        if (msg) {
            for(let i = 0; i < msg.length; i++){
                params.push(msg[i]);
            }
        }
        console.log.apply(this,params);
    }

    debug(...data: any[]) {
        this.out('debug', Array.prototype.slice.apply(arguments));
    }

    info(...data: any[]) {
        this.out('info', Array.prototype.slice.apply(arguments));
    }

    warn(...data: any[]) {
        this.out('warn', Array.prototype.slice.apply(arguments));
    }

    error(...data: any[]) {
        this.out('error', Array.prototype.slice.apply(arguments));
    }

    static setLevel(level: string) {
        if (!LOG_LEVEL[level]) {
            throw new Error("The level nonsupport: " + level);
        }
        _level = level;
    }
}

// 日期格式化
function dateFormat(date,fmt) {
    // 默认格式
    fmt = fmt ? fmt : 'yyyy-MM-dd hh:mm:ss';

    let o = {
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
    for(let k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) :
                RegExp.$1.length==2 ? (("00"+ o[k]).substr((""+ o[k]).length)) : (("000"+ o[k]).substr((""+ o[k]).length))
            );
        }
    }
    return fmt;
}
