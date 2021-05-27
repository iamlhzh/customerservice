package com.lhzh.customerservice.service;

import com.alibaba.fastjson.JSON;
import com.lhzh.customerservice.dto.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxAppService {

    @Value("${wxapp.myroad.appid}")
    private String appid;

    @Value("${wxapp.myroad.secret}")
    private String secret;

    private static final Logger logger = LoggerFactory.getLogger(WxAppService.class);

    /**
     * 通过code获取wx unionId
     */
    public JsonResult<Map<String, String>> getWxUnionIdByCode(String code) {
        logger.info("wxCode===" + code);
        JsonResult<Map<String, String>> response = new JsonResult<>();
        Map<String,Object>resMap = new HashMap<>();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        String result = sendGetReq(url);
        if (StringUtils.isNotEmpty(result)) {
            Map<String, String> map = JSON.parseObject(result, Map.class);
            if (map.get("errcode")==null||"0".equals(map.get("errcode"))) {
                response.setResult(map);
            }else{
                response.setResult(map);
                response.setErrorMsg("获取失败！");
            }
        }else{
            response.setErrorMsg("获取失败！");
        }
        return response;
    }

    public String sendGetReq(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置超时时间
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(30000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

}
