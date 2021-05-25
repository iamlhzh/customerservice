package com.lhzh.customerservice.dto;

import java.io.Serializable;

public class WxImageMsgDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String media_id;


    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }
}
