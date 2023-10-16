# socketd

Simple message protocol:

```
[len:int][flag:int][key:str][\n][resourceDescriptor:str][\n][header:str][\n][body:byte..]
```

uri example:

* smp:tcp://19.10.2.3:9812/path?a=1&b=1
* smp:ws://19.10.2.3:1023/path?a=1&b=1
* smp:resocket://19.10.2.3:1023/path?a=1&b=1


