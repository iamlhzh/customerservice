package com.lhzh.customerservice.dto;

import java.io.Serializable;
import java.util.Date;

public class WxMessageCustomSendResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String errcode;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }
}
