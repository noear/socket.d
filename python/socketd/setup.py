#! /usr/bin/env python
# -*- coding: utf-8 -*_
import setuptools
with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

setuptools.setup(
    name='socketD',  # 包的名字
    version='0.0.1',  # 版本号
    description=long_description,  # 描述
    author='bai,noear',  # 作者
    author_email='loserbai@foxmail.com,9979331@qq.com',  # 你的邮箱**
    url='https://socketd.noear.org/',  # 可以写github上的地址，或者其他地址
    packages=setuptools.find_packages(exclude=['test']),  # 包内不需要引用的文件夹

    # 依赖包
    install_requires=[
        'loguru',
        'websockets'
    ],
    classifiers=[
        'Development Status :: 4 - Beta',
        'Operating System :: Microsoft'  # 你的操作系统
        'Intended Audience :: Developers',
        'License :: OSI Approved :: BSD License',  # BSD认证
        'Programming Language :: Python',  # 支持的语言
        'Programming Language :: Python :: 3',  # python版本 。。。
        'Programming Language :: Python :: 3.10',
        'Topic :: Software Development :: Libraries'
    ],
    zip_safe=True,
    python_requires='>=3.10', # 建议使用3.12及以上
)
