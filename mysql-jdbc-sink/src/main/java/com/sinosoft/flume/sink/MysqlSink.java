package com.sinosoft.flume.sink;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.sinosoft.flume.bean.LogBean;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * @Title: mysql sink
 * @Author: lsh
 * @CreateDate: 2018/12/3 16:14
 */
public class MysqlSink extends AbstractSink implements Configurable {
    private Logger LOG = LoggerFactory.getLogger(MysqlSink.class);
    private String hostname;
    private String port;
    private String databaseName;
    private String tableName;
    private String user;
    private String password;
    private PreparedStatement preparedStatement;
    private Connection conn;
    private int batchSize;

    public MysqlSink() {
        LOG.info("MysqlSink start...");
    }

    public void configure(Context context) {
        hostname = context.getString("hostname");
        Preconditions.checkNotNull(hostname, "hostname must be set!!");
        port = context.getString("port");
        Preconditions.checkNotNull(port, "port must be set!!");
        databaseName = context.getString("databaseName");
        Preconditions.checkNotNull(databaseName, "databaseName must be set!!");
        tableName = context.getString("tableName");
        Preconditions.checkNotNull(tableName, "tableName must be set!!");
        user = context.getString("user");
        Preconditions.checkNotNull(user, "user must be set!!");
        password = context.getString("password");
        Preconditions.checkNotNull(password, "password must be set!!");
        batchSize = context.getInteger("batchSize", 100);
        Preconditions.checkNotNull(batchSize > 0, "batchSize must be a positive number!!");
    }

    @Override
    public void start() {
        super.start();
        try {
            //调用Class.forName()方法加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + databaseName;
        //调用DriverManager对象的getConnection()方法，获得一个Connection对象

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            //创建一个Statement对象
            preparedStatement = conn.prepareStatement("insert into " + tableName +
                    " (user_id,user_name,operate_date,url,content) values (?,?,?,?,?)");

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public void stop() {
        super.stop();
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Status process() throws EventDeliveryException {
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        Event event;
        String content;
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );

        List<LogBean> logs = Lists.newArrayList();
        transaction.begin();
        try {
            for (int i = 0; i < batchSize; i++) {
                event = channel.take();
                if (event != null) {//对事件进行处理
                    //event 的 body 为   "exec tail$i , abel"
                    content = new String(event.getBody());
                    LogBean log=new LogBean();
                    //处理event 将文本解析
                    String [] tmpLog=content.split("::");
                    if (tmpLog.length==5) {
                        //存储 event 的 content
                        log.setUserId(tmpLog[0]);
                        log.setUserName(tmpLog[1]);
                        log.setOperateDate(sdf.parse(tmpLog[2]));
                        log.setUrl(tmpLog[3]);
                        log.setContent(tmpLog[4]);
                    }else{
                        log.setContent(content);
                    }
                    logs.add(log);
                } else {
                    result = Status.BACKOFF;
                    break;
                }
            }

            if (logs.size() > 0) {
                preparedStatement.clearBatch();
                for (LogBean temp : logs) {
                    preparedStatement.setString(1, temp.getUserId());
                    preparedStatement.setString(2, temp.getUserName());
                    preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));
                    preparedStatement.setString(4, temp.getUrl());
                    preparedStatement.setString(5, temp.getContent());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();

                conn.commit();
            }
            transaction.commit();
        } catch (Exception e) {
            try {
                transaction.rollback();
            } catch (Exception e2) {
                LOG.error("Exception in rollback. Rollback might not have been" +
                        "successful.", e2);
            }
            LOG.error("Failed to commit transaction." +
                    "Transaction rolled back.", e);
            Throwables.propagate(e);
        } finally {
            transaction.close();
        }
        return result;
    }
}