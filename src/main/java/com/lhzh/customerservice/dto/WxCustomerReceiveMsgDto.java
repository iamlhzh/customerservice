package com.lhzh.customerservice.dto;

import java.io.Serializable;
import java.util.Date;

public class WxCustomerReceiveMsgDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ToUserName;

    private String FromUserName;

    private Date CreateTime;

    private String MsgType;

    private String Event;

    private String SessionFrom;

    private String Content;

    private String MsgId;

    private String PicUrl;

    private String MediaId;

    private String KfAccount;

    private String AppId;


    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getSessionFrom() {
        return SessionFrom;
    }

    public void setSessionFrom(String sessionFrom) {
        SessionFrom = sessionFrom;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getKfAccount() {
        return KfAccount;
    }

    public void setKfAccount(String kfAccount) {
        KfAccount = kfAccount;
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    @Override
    public String toString() {
        return "WxCustomerReceiveMsgDto{" +
                "ToUserName='" + ToUserName + '\'' +
                ", FromUserName='" + FromUserName + '\'' +
                ", CreateTime=" + CreateTime +
                ", MsgType='" + MsgType + '\'' +
                ", Event='" + Event + '\'' +
                ", SessionFrom='" + SessionFrom + '\'' +
                ", Content='" + Content + '\'' +
                ", MsgId='" + MsgId + '\'' +
                ", PicUrl='" + PicUrl + '\'' +
                ", MediaId='" + MediaId + '\'' +
                ", KfAccount='" + KfAccount + '\'' +
                ", AppId='" + AppId + '\'' +
                '}';
    }
}
