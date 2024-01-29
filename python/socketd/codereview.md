### 2024-01-29:
* 增加 ClientSession 接口，客户端连接输出此类型
* 增加 实现"集群"客户端
* 增加 ClientBase::openOrThow 实现（包括 open 的，这块机制在 java 那儿有大调整）

### 2024-01-20:

* 把 SocketDAlarmException 用起来，它是由对端传过来的异常（是协议的一部分）
* 其它异常也要用起来
* Codec 体系加入 CodecReader，CodecWriter 
  * 用于对任何对象做编解码（尤其是同时处理 ws, tcp,upd 的适配，或不同框架）
  * 内核实现最常用的默认实现
* Session::send_* 几个函数有调整。可能有问题！
