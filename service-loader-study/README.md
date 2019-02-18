# JAVA-SPI学习/类加载器ServiceLoader

## 内部方式
在resources下的META-INF/services下创建和接口（带包路径）同名的文件，将实现类按行写入

## 外部方式
引入对应的jar包依赖即可