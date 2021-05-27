package com.lhzh.customerservice.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 7705290087975145222L;
    private String errorCode;
    private String errorMsg;
    private T result;
    private boolean success;
    private long timestamp = System.currentTimeMillis();

    public JsonResult() {
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        if (StringUtils.isEmpty(this.errorCode) && StringUtils.isEmpty(this.errorMsg)) {
            this.success = Boolean.TRUE;
        }

        this.result = result;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        if (StringUtils.isNotEmpty(errorCode)) {
            this.success = Boolean.FALSE;
        }

        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        if (StringUtils.isNotEmpty(errorMsg)) {
            this.success = Boolean.FALSE;
        }

        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
