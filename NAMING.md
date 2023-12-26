# 此规范为跨语言迁移提供参考

## 一、规范

### 1、方法规范（争对设置、添加、获取方法）

* 普通设置：setXxx():void
* 链式设置：xxxSet():self
* 普通添加：putXxx():void, addXxx():void
* 连式添加：xxxPut():self, xxxAdd():self
* 普通获取：getXxx()
* 简化获取：xxx() //用在面向用户体验时（目前主要用在"实体"、"消息"、"帧"身上）

### 2、命名风格（争对不同语言）

* 小大写风格：getXxxYyy (一般的语言，都是这种)
* 下划线风格：get_xxx_yyy


##  二、规范定义缘由

原 java 里可以：

```java
public class Demo {
    String tag;

    Demo tag(String tag) { //这咱风格，在 Builder 模式里挺常见
        this.tag = tag;
        return this;
    }

    String tag() {
        return this.tag;
    }
}
```

 在 js 里就没办法用了，需改成：

```javascript
class Demo {
    _tag: string; //字段名不能与函数名同

    tagSet(tag: stirng): Demo { //不能有相同的函数名，故改成 tagSet，同时与普通设置风格区别开来
        this._tag = tag;
        return this;
    }

    tag(): string {
        return this._tag;
    }
}
```
