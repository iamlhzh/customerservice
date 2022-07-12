package com.lhzh.wechatsubscription;

import com.lhzh.customerservice.utils.WxUtils;
import org.springframework.beans.factory.annotation.Value;

public class TestSend {
    private static String appid="wxd9147a38db8835af";

    private static String appsecret="22da4882b1b1782389140a3ee0c5ed8b";

    public static void main(String[] args) {
        String accessToken = WxUtils.getWxAccesstoken(appid,appsecret);
        System.out.println(accessToken);
    }

}