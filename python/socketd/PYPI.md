### 开发指南

* 以 setup.py 所在目录，为开发目录
* 安装依赖

```
pip install -r reqeusts.txt

pip install -r reqeusts.txt -i https://pypi.tuna.tsinghua.edu.cn/simple/
```

### 发布

注册 https://pypi.org/ 账号。然后：

1. 安装twine:

```
pip install twine
pip show twine 
```

找到安装的地址 `pip show twine`。之后，要把 twine 添加到 PATH 里（.bash_profile）

```
# twine
export PATH=/Users/noear/Library/Python/3.9/lib/python/site-packages:$PATH
export PATH=/Users/noear/Library/Python/3.9/bin:$PATH
# twine END
```

2. 在命令行中运行以下命令来打包你的项目：

```
python3 setup.py build sdist
```

3. 使用twine上传包到PyPI（需要在账号设置创建 Api Token）：

```
twine upload --repository-url https://upload.pypi.org/legacy/ dist/*
```

### 安装使用

```
pip install socket.d
```