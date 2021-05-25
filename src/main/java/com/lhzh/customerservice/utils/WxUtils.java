package com.lhzh.customerservice.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lhzh.customerservice.controller.WeixinAppletsBatchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;

public class WxUtils {

    private static final  String Token="shxx";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Logger logger = LoggerFactory.getLogger(WxUtils.class);

    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={corpId}&secret={corpsecret}";



    public static boolean CheckSignature(String signature, String timestamp, String nonce) {

//		1）将token、timestamp、nonce三个参数进行字典序排序
//		2）将三个参数字符串拼接成一个字符串进行sha1加密
//		3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        String[] strs=new String[]{Token,timestamp,nonce};

        Arrays.sort(strs);

        String str=strs[0]+strs[1]+strs[2];

        String mysignature=sha1(str);
        return mysignature.equals(signature);
    }

    private static String sha1(String str) {


        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedText(byte[] bytes) {

        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }



    /**
     * 1.发起https请求并获取结果
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
//            TrustManager[] tm = { new MyX509TrustManager() };
//            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
//            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
//            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
//            logger.error("Weixin server connection timed out.");
        } catch (Exception e) {
//            logger.error("https request error:{}", e);
        }
        return jsonObject;
    }

    /**
     * 3.获取access_token
     *
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    public static String getWxAccesstoken(String appid, String appsecret) {
        String accessToken = null;

        String requestUrl = access_token_url.replace("{corpId}", appid).replace("{corpsecret}", appsecret);
        JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                accessToken = jsonObject.getString("access_token");
            } catch (JSONException e) {
                accessToken = null;
                // 获取token失败
//                logger.error("获取token失败 errcode:{} errmsg:{}", "", jsonObject.getString("errmsg"));
            }
        }
        return accessToken;
    }

}