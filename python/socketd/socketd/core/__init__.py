import os
import sys

jd_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "."))  # 将当前路径加入到包扫描中
sys.path.insert(0, jd_dir)

