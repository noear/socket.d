# socketd for javascript




### 日志规范

* 运行时中的异常用 warn
* 关闭、停止时的异常用 debug
* 启动与连接成功用 info

### 部分变异

EventListener 的 onXxx(fun) 更名为：doOnXxx(fun)