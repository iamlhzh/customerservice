package com.lhzh.customerservice.dto;

import java.io.Serializable;
import java.util.Date;

public class WxCustomerSendMsgDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String touser;

    private String msgtype;

    private WxTextMsgDto text;

    private WxImageMsgDto image;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public WxTextMsgDto getText() {
        return text;
    }

    public void setText(WxTextMsgDto text) {
        this.text = text;
    }

    public WxImageMsgDto getImage() {
        return image;
    }

    public void setImage(WxImageMsgDto image) {
        this.image = image;
    }
}
