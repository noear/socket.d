
### 发布

注册 pypi 账号。然后：

1. 安装twine:

```
pip3 install twine
```

2. 在命令行中运行以下命令来打包你的项目：

```
$ python3 -m pip config set global.index-url https://upload.pypi.org/legacy/
$ python3 -m pip config set global.username "your_username"
$ python3 -m pip config set global.password "your_password"

$ python3 -m pip install --user --upgrade setuptools wheel
$ python3 setup.py sdist bdist_wheel
$ python3 -m twine upload dist/*
```

```
python3 setup.py sdist build
```

3. 使用twine上传包到PyPI：

```
twine upload dist/*
```

### 安装使用

```
pip install your_package_name
```