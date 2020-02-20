# Thrift 学习

## 安装

1. mac 安装 0.9.3 版本

`brew install https://gist.githubusercontent.com/chrislusf/8b4e7c19551ba220232f037b43c0eaf3/raw/01465b867b8ef9af7c7c3fa830c83666c825122d/thrift.rb`

## 学习

### hello-world

1. 运行 maven 插件，生成 java 代码。
2. 分别运行 Server 和 Client 代码。

### impala-beeswax

0. 注意
* 这里容易混乱的是 ImpalaHiveServer2Service, ImpalaService, BeeswaxService 三者之间的关系，
具体可以参考[官方文档](https://cwiki.apache.org/confluence/display/IMPALA/Impala+Connectors)中的介绍。

* impala 里的 beeswax 指的是早期的 thrift 协议，即 21000 端口的版本，
后来才支持了 hiveserver2 thrift 协议，于是用新的端口 21050 进行通信。
而 hue 里也有个 beeswax，查看 1.0 之前的 hue 源码，只看到一个 beeswax.thrift，猜测当时只支持 impala，
并且当时 impala 也只有 beeswax 接口，所以将用 beeswax 来表示用 impala 查询 hive 元数据。
而后来有了 impala，于是 beeswax 就专门指代 hive 了。（这里的 beeswax 意为蜂巢，估计想表达 impala 是 hive 的归宿）

1. thrift 文件

* beeswax.thrift, ExecStats.thrift, ImpalaService.thrift, Status.thrift, Types.thrift ->
    来自于 https://github.com/cloudera/Impala/tree/cdh5-2.12.0_5.16.1/common/thrift

* hive_metastore.thrift, fb303.thrift ->
    来自于 https://github.com/cloudera/hue/tree/cdh5-3.9.0_5.16.1/apps/impala/thrift/include
    当然也可以从 hive 的源码 https://github.com/cloudera/hive/tree/cdh5-1.1.0_5.16.1/metastore/if 获取

* TCLIService.thrift ->
    来自于 https://github.com/cloudera/hue/tree/cdh5-3.9.0_5.16.1/apps/impala/thrift
    当然也可以从 hive 的源码 https://github.com/cloudera/hive/tree/cdh5-1.1.0_5.16.1/service/if 获取

* ErrorCodes.thrift ->
    来自于 https://github.com/cloudera/hue/tree/cdh5-3.9.0_5.16.1/apps/impala/thrift

2. 运行

* 运行 maven 插件，生成 java 代码。
* 直接运行 main 方法。

3. 参考

* [通过Impala thrift API接口进行Impala查询](https://blog.csdn.net/maydaysar/article/details/85236197)
* [impala-client-example](https://github.com/terry-chelsea/impala-client-example)