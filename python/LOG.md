### 2.4.10
* 添加 preclose 和 prestop（简化二段式停止操作）
* 调整 通道关闭打印条件（避免多次打印）

### 2.4.9
* 添加 CLOSE2003_DISCONNECTION 关闭码
* 添加 Pressure 帧类型（预留做背压控制）
* 修复 当使用二段式关闭时，可能出现无法重连的问题（2.3.10 后出现的）
* 同步 ProcessorDefault 类代码（之前 on_open 那儿是错的）

### 2.4.8
* 添加 X-Hash 元信息支持