#! /usr/bin/env python
# -*- coding: utf-8 -*_
from setuptools import setup,find_packages

setup(
    name='socket.d',
    version='2.4.14',
    description='@noear/socket.d python project',
    author='noear,bai',
    url='https://socketd.noear.org/',
    packages=find_packages(exclude=['*test*']),   # 包内不需要引用的文件夹
    install_requires=[                          # 依赖包
        'loguru',
        'websockets'
    ],
    classifiers=[
        'Development Status :: 5 - Production/Stable',
        'Intended Audience :: Developers',
        'License :: OSI Approved',
        'Operating System :: OS Independent',
        'Programming Language :: Python',
        'Programming Language :: Python :: 3.12',
        'Topic :: Software Development :: Libraries'
    ],
    zip_safe=True,
    python_requires='>=3.12', # 建议使用3.12及以上
)
