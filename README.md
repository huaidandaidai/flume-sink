# 日志采集flume自定义sink

## mysql-jdbc-sink
`存在问题：项目在启动时创建一个数据库链接，如果长时间不进行数据库操作，mysql会报错数据库链接异常`

## mysql-mybatis-sink
    1. 结合mybatis和druid的连接池
## mysql-dubbo-sink
    1、引入dubbo，sink作为客户端，读取日志数据后，调用服务端，服务端将数据写入库

