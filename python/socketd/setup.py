#! /usr/bin/env python
# -*- coding: utf-8 -*_
from setuptools import setup,find_packages

setup(
    name='socket.d',
    version='2.5.11',
    description='@noear/socket.d python project',
    author='noear,bai',
    url='https://socketd.noear.org/',
    packages=find_packages(exclude=['*test*']),   # 包内不需要引用的文件夹
    install_requires=[                          # 依赖包
        'loguru==0.7.2',
        'websockets==12.0'
    ],
    classifiers=[
        'Development Status :: 5 - Production/Stable',
        'Intended Audience :: Developers',
        'License :: OSI Approved',
        'Operating System :: OS Independent',
        'Programming Language :: Python',
        'Programming Language :: Python :: 3.10',
        'Topic :: Software Development :: Libraries'
    ],
    zip_safe=True,
    python_requires='>=3.10', # 建议使用3.10及以上
)
