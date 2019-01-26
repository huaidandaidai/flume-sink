package com.sinosoft.flume.bean;

import java.util.Date;

/**
 * @Title: 日志bean
 * @Author: lsh
 * @CreateDate: 2018/12/3 16:14
 */
public class LogBean {
    private String userId;
    private String userName;
    private Date operateDate;
    private String url;
    private String content;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LogBean{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", operateDate=" + operateDate +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}