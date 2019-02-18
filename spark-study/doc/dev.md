# 开发相关

## Windows本地开发注意
这里推荐Intellij IDEA

### 报错处理
1. 依赖包警告:
```
WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable".
ERROR Shell: Failed to locate the winutils binary in the hadoop binary path
```
意思是找不到```Hadoop```相关的包，**可以不处理且不影响程序结果**。

处理方式：[下载](https://github.com/steveloughran/winutils)相关文件，配置系统环境变量```HADOOP_HOME```指向对应Hadoop版本目录。
本程序使用的是```hadoop-2.7.1```，重启IDEA生效（这里最好是双击桌面启动，实际发现如果使用了Listary快速启动会导致环境变量读取不了的情况）

## 其他设置

1. 日志输出设置

将```spark-core```包中的```org\apache\spark```下的```log4j-defaults.properties```拷贝到```src/main/resource```，
然后就可以方便设置日志级别啦。