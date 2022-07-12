package com.lhzh.utils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Arrays;

public class WxTokenUtils {

    private static final  String Token="shxx";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static Logger logger = LoggerFactory.getLogger(com.lhzh.customerservice.utils.WxUtils.class);

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

    /**
     * 新增临时素材
     *
     * @param fileType
     * @param filePath
     * @return
     * @throws Exception
     */
    public static JSONObject UploadMeida(String fileType, String filePath) throws Exception {
        // 返回结果
        String result = null;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            logger.info("文件不存在");
            throw new IOException("文件不存在");
        }
        String token = getWxAccesstoken("wxc66d689cce7ffcc8","4d47a344c6e9aabf7f4ba269c85f4d34");
        if (token == null) {
            logger.info("未获取到token");
            throw new IOException("未获取到token");
        }
        String urlString = " https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE".replace("ACCESS_TOKEN", token).replace("TYPE", fileType);
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");// 以POST方式提交表单
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);// POST方式不能使用缓存
        // 设置请求头信息
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        // 设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        // 请求正文信息
        // 第一部分
        StringBuilder sb = new StringBuilder();
        sb.append("--");// 必须多两条道
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"media\"; filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
        logger.debug("sb:" + sb);

        // 获得输出流
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        // 输出表头
        out.write(sb.toString().getBytes("UTF-8"));
        // 文件正文部分
        // 把文件以流的方式 推送道URL中
        DataInputStream din = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] buffer = new byte[1024];
        while ((bytes = din.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
        }
        din.close();
        // 结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");// 定义数据最后分割线
        out.write(foot);
        out.flush();
        out.close();
        if (HttpsURLConnection.HTTP_OK == conn.getResponseCode()) {

            StringBuffer strbuffer = null;
            BufferedReader reader = null;
            try {
                strbuffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String lineString = null;
                while ((lineString = reader.readLine()) != null) {
                    strbuffer.append(lineString);

                }
                if (result == null) {
                    result = strbuffer.toString();
                    logger.info("result:" + result);
                }
            } catch (IOException e) {
                logger.error("发送POST请求出现异常！", e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;

    }

    public static String download(String urlString,int i)throws Exception {
        // 构造URL
        URL url =new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs =new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
//        String filename ="D:\\" + i +".jpg"; //下载路径及下载图片名称
//        File file =new File(filename);
        String property = System.getProperty("user.dir");
        //在根目录生成一个文件
        File file = new File(property+"/"+"aa.jpg");
        logger.info(property+i);

        FileOutputStream os =new FileOutputStream(file,true);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs,0, len);
        }
        System.out.println(i);
        // 完毕，关闭所有链接
        os.close();
        is.close();
        return file.getAbsolutePath();
    }

    public static void main(String[] args) throws Exception {

        String download = download("http://dev-sz.valueonline.cn/idm//formal/de66866d-db2b-47f5-8bee-3de6b4c54457.jpg", 2);
        System.out.println(download);
        JSONObject jsonObject = UploadMeida("image", download);
        System.out.println(jsonObject);
    }

}