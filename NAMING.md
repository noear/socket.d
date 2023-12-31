Socket.D 的协议实现，涉及了很多语言与平台。比如 javascript ，不能有同名的属性和方法，不能有同名的方数；像 python，习惯上用 _ 作命名间隔。

所以定义和申明个规范，为跨语言实现尽量风格统一提供参考。也可以让用户了解情况。

### 一、规范定义

尽量少的定义，其它原则上参考第一个实现语言

#### 1、方法名规范（争对设置、增量设置、添加、获取方法）

* 普通设置：`setXxx():void` //指完全替换目标
* 链式设置：`xxxSet():self`
* 普通放置：`putXxx():void` //指部分替换目标
* 连式放置：`xxxPut():self`
* 增通添加：`addXxx():void`
* 连式添加：`xxxAdd():self`
* 普通获取：`getXxx()`
* 分组获取：`xxxGet()`
* 简化获取：`xxx()` //目前主要用在"实体"、"消息"、"帧"的接口上

#### 2、命名风格（争对不同语言）

* 小大写风格：`getXxxYyy`，`xxxYyyGet` (常见风格)
* 下划线风格：`get_xxx_yyy`，`xxx_yyy_get`（好像就 python 这风格）

#### 3、指导原则

* 外部用的，优先简洁
* 相同意义，风格优先相同

###  二、规范定义缘由（及调整案例）

#### 1、如果主接口是获取

原 java 里可以：

```java
public interface Demo{
    String tag();
}
public class DemoDefault implements Demo{
    String tag;

    public Demo tag(String tag) { //这种风格，在 Builder 模式里算常见
        this.tag = tag;
        return this;
    }

    public String tag() { //它是主接口（优先简洁）
        return this.tag;
    }
}
```

在 js 里就没办法用了，需要改成：

```javascript
//js
class DemoDefault {
    _tag: string; //字段名不能与函数名同

    tagSet(tag: stirng): Demo { //与主接口冲突，改成 tagSet 链式设置风格
        this._tag = tag;
        return this;
    }

    tag(): string { //它是主接口（优先简洁）
        return this._tag;
    }
}
```

```java
//java
public class DemoDefault implements Demo{
    String tag;

    public Demo tagSet(String tag) { //与主接口冲突，改成 tagSet 链式设置风格
        this.tag = tag;
        return this;
    }

    public String tag() { //它是主接口（优先简洁）
        return this.tag;
    }
}
```

#### 2、如果主接口是配置（或设置）

原 java 里可以：

```java
public interface Demo{
    Demo tag(String tag);
}
public class DemoDefault implements Demo{
    String tag;

    public Demo tag(String tag) { //它是主接口（优先简洁）
        this.tag = tag;
        return this;
    }

    public String tag() { //这种风格，在 Builder 模式里算常见
        return this.tag;
    }
}
```

在 js 里就没办法用了，需要改成：

```javascript
//js
class DemoDefault implements Demo {
    _tag: string; //字段名不能与函数名同

    tag(tag: stirng): Demo {  //它是主接口（优先简洁）
        this._tag = tag;
        return this;
    }

    getTag(): string { //与主接口冲突，改成 getTag 普通获取风格
        return this._tag;
    }
}
```

```java
//java
public class DemoDefault implements Demo{
    String tag;

    public Demo tag(String tag) { //它是主接口（优先简洁）
        this.tag = tag;
        return this;
    }
    
    public String getTag() { //与主接口冲突，改成 getTag 普通获取风格
        return this.tag;
    }
}
```
