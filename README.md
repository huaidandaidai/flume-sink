# 中科软日志采集flume自定义sink控件MysqlSink

## 日志表SQL(根据需求自行修改logBean和数据库表)
DROP TABLE IF EXISTS XXXX;
create table XXXX(
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '自增序列id',
    user_id varchar(500) DEFAULT NULL COMMENT '用户id',
    user_name varchar(500) DEFAULT NULL COMMENT '用户名' ,
    operate_date datetime DEFAULT NULL COMMENT '操作时间',
    url varchar(5000) DEFAULT NULL COMMENT '用户名' ,
    content text DEFAULT NULL COMMENT '操作内容',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

## Flume 配置文件(flume-conf.properties)
    见resource下sino-flume-conf.properties,将此文件复制到flume安装目录下的conf文件夹下

## 运行部署
1. 根据实际需求修改Flume配置文件中要监听的文件；
2. 根据实际需求修改Flume配置文件中的数据库配置；
3. 根据实际需求修改MysqlSink类中的sql插入语句；
4. 将flume部署到要采集日志的服务器上；
5. 将本工程打包成jar包 放置到flume安装目录的lib文件夹下
6. 运行以下命令启动flume
    1、在bin目录下:`./flume-ng agent -c ../conf -f ../conf/sino-flume-conf.properties -n agent -Dflume.root.logger=INFO,console`
    2、打开另一个终端生成数据:for i in {1..100};do echo "2018-12-03-$i::张$i::2018-12-03 17:51:55::https://www.baidu.com::哈哈哈访问了百度" >> /var/log/test1/example.log;sleep 1;done;
    3、flume后台脱机运行命令：
