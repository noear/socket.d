此规范为跨语言迁移提供参考

### 方法规范（争对设置，添加，获取）

* 普通设置：setXxx():void
* 链式设置：xxxSet():self
* 普通添加：putXxx():void, addXxx():void
* 连式添加：xxxPut():self, xxxAdd():self
* 普通获取：getXxx()
* 简化获取：xxx() //用在面向用户体验时（目前主要用在"实体"、"消息"、"帧"身上）

### 命名风格（争对不同语言）

* 小大写风格：getXxxYyy (一般的语言，都是这种)
* 下划线风格：get_xxx_yyy