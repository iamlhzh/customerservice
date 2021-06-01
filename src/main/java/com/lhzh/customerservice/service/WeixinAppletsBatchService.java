package com.lhzh.customerservice.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lhzh.customerservice.controller.WeixinAppletsBatchController;
import com.lhzh.customerservice.dto.WxCustomerSendMsgDto;
import com.lhzh.customerservice.dto.WxMessageCustomSendResponseDto;
import com.lhzh.customerservice.utils.WxUtils;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Service
public class WeixinAppletsBatchService {

    private String appid ="wxc66d689cce7ffcc8";

    private String appsecret = "4d47a344c6e9aabf7f4ba269c85f4d34";

    private Logger logger = LoggerFactory.getLogger(WeixinAppletsBatchService.class);


    public static final String wxSendBaseUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send";

    public WxMessageCustomSendResponseDto sendCustomMessage(HttpServletRequest request, WxCustomerSendMsgDto wxMessageCustomSendTextRequestDTO) {
        String accessToken = WxUtils.getWxAccesstoken(appid,appsecret);
        String url = wxSendBaseUrl+"?access_token="+accessToken;
        String json = JSON.toJSONString(wxMessageCustomSendTextRequestDTO);
        JSONObject jsonObject = WxUtils.httpRequest(url, "POST", json);
        logger.info("推送结果：");
        logger.info(jsonObject.toJSONString());
        if (null != jsonObject) {
            if (0 != jsonObject.getIntValue("errcode")) {
                logger.error("消息发送失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"), jsonObject.getString("errmsg"));
            }
        }
        WxMessageCustomSendResponseDto wxMessageCustomSendResponseDto =new WxMessageCustomSendResponseDto();
        wxMessageCustomSendResponseDto.setErrcode("0");
        return wxMessageCustomSendResponseDto;
    }
}

