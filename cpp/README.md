# socket.d for C/C++

## Dependencies
* libhv - https://github.com/ithewei/libhv
* sds - https://github.com/antirez/sds
* uuid4 - https://github.com/rxi/uuid4

## Environment

Only for Linux.

## Git clone

* Cloning main project
```
git clone -b dev https://github.com/necyber/socket.d.git
```
* Initialize the local configuration file by registering the paths of the submodules.
```
git submodule init
```
* Pull all data and check out the appropriate submodule commit.
```
git submodule update
```

## Build
* Build libhv
Ref: https://github.com/ithewei/libhv
```
cd socket.d/cpp/3rd/libhv
./configure
make
sudo make install
```


* Build 
```
cd socket.d/cpp
make clean
make
```

## Bug reports

Feel free to use the issue tracker on github.

**If you are reporting a security bug** please contact a maintainer privately.
We follow responsible disclosure: we handle reports privately, prepare a
patch, allow notifications to vendor lists. Then we push a fix release and your
bug can be posted publicly with credit in our release notes and commit
history.

## Website

* https://socketd.noear.org/

## Contributing
