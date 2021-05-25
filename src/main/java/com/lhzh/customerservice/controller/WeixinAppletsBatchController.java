package com.lhzh.customerservice.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.lhzh.customerservice.dto.WxCustomerReceiveMsgDto;
import com.lhzh.customerservice.dto.WxCustomerSendMsgDto;
import com.lhzh.customerservice.dto.WxMessageCustomSendResponseDto;
import com.lhzh.customerservice.dto.WxTextMsgDto;
import com.lhzh.customerservice.service.WeixinAppletsBatchService;
import com.lhzh.customerservice.utils.WXBizMsgCrypt;
import com.lhzh.customerservice.utils.WxUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@Controller
@RequestMapping("weixinAppletsBatch")
public class WeixinAppletsBatchController {

    @Autowired
    private WeixinAppletsBatchService weixinAppletsBatchService;
    //第三方平台APPID
    //private static final String APPID = "wxc3aecf6167f122b6";
    //第三方平台APPSECRET
    // private static final String APPSECRET = "e0e92d715c99dc60068fb2cd568b1227";

//    @Value("#{app['weixin.third.appid']}")
//    private String APPID;
//
//    @Value("#{app['weixin.third.appsecret']}")
//    private String APPSECRET;
//
//    @Value("#{app['file.viewPath']}")
//    private String viewPath ;

    //消息加解密Key
    private static final String KEY = "jd73k6n20z7x3n5m2dfcf239dk57dnf93l1hdfurt31";
    //消息校验Token
    private static final String VALIDATE_TOKEN = "jjzxtouzizheguanxi";

    private static final String COMPONENT_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
    //获取预授权码
    private static final String PRE_AUTH_CODE = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=";
    //用授权码获取公众号或小程序的接口调用凭据和授权信息
    private static final String API_QUERY_AUTH = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=";
    //用refreshToken获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
    private static final String API_AUTHORIZER_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=";
    //获取授权方的帐号基本信息
    private static final String API_GET_AUTHORIZER_INFO = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=";
    //为授权的小程序帐号上传小程序代码
    private static final String COMMIT = "https://api.weixin.qq.com/wxa/commit?access_token=";
    //获取授权小程序帐号已设置的类目
    private static final String GET_CATEGORY = "https://api.weixin.qq.com/wxa/get_category?access_token=";
    //获取小程序的第三方提交代码的页面配置（仅供第三方开发者代小程序调用）
    private static final String GET_PAGE = "https://api.weixin.qq.com/wxa/get_page?access_token=";
    //将第三方提交的代码包提交审核（仅供第三方开发者代小程序调用）
    private static final String SUBMIT_AUDIT = "https://api.weixin.qq.com/wxa/submit_audit?access_token=";
    //发布已通过审核的小程序（仅供第三方代小程序调用）
    private static final String RELEASE = "https://api.weixin.qq.com/wxa/release?access_token=";
    //设置小程序服务器域名
    private static final String MODIFY_DOMAIN = "https://api.weixin.qq.com/wxa/modify_domain?access_token=";
    //查询某个指定版本的审核状态（仅供第三方代小程序调用）
    private static final String GET_AUDITSTATUS = "https://api.weixin.qq.com/wxa/get_auditstatus?access_token=";
    //设置小程序业务域名（仅供第三方代小程序调用）
    private static final String SET_WEBVIEWDOMAIN = "https://api.weixin.qq.com/wxa/setwebviewdomain?access_token=";
    //获取代码模板列表
    private static final String GET_CODE_TEMPLATE_LIST = "https://api.weixin.qq.com/wxa/gettemplatelist?access_token=";
    //提审代码取消审核
    private static final String GET_UNDOCODEAUDIT = "https://api.weixin.qq.com/wxa/undocodeaudit?access_token=";
    //将草稿添加到代码模板库
    private static final String ADD_TOTEMPLATE = "https://api.weixin.qq.com/wxa/addtotemplate?access_token=";
    //删除指定代码模板
    private static final String DELETE_TEMPLATE = "https://api.weixin.qq.com/wxa/deletetemplate?access_token=";
//
//    @Value("#{app['cloud.baseUrl']}")
//    private String CLOUDURL;

    private static final String token = "shxx";

    private static final String aesKey = "FYhVENrwk8PTAbArwrNdsF7mZ8McXK1ecwGtgHtaMy8";

    private static final String appid = "wxc66d689cce7ffcc8";

    private Logger logger = LoggerFactory.getLogger(WeixinAppletsBatchController.class);

    @RequestMapping(value = "/checkWxMsg")
    public void checkWxMsg(HttpServletRequest request, HttpServletResponse response) {
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        logger.info("请求方式：{}", isGet);
        PrintWriter out = null;
        if(isGet){
            		/*开发者提交信息后，微信服务器将发送GET请求到填写的服务器地址URL上，GET请求携带参数如下表所示：
		参数	描述
		signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
		timestamp	时间戳
		nonce	随机数
		echostr	随机字符串*/
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            System.out.println(signature);
            System.out.println(timestamp);
            System.out.println(nonce);
            System.out.println(echostr);
            try{
                out = response.getWriter();
                boolean checkSignature = WxUtils.CheckSignature(signature,timestamp,nonce);
                if(checkSignature){
                    out.print(echostr);
                    out.flush();
                }else{
                    out.print("");
                    out.flush();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                out.close();
            }
        }else{
            //接收到微信发送消息请求
            logger.info("接收到微信发送消息请求");
            try {
                out = response.getWriter();
                ServletInputStream inputStream = request.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = new String("");
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                logger.info(stringBuffer.toString());
                JSONObject jsonObject = JSON.parseObject(stringBuffer.toString());
                String encrypt = jsonObject.getString("Encrypt");
                logger.info(encrypt);
                WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(token,aesKey,appid);
                String decrypt = wxBizMsgCrypt.decrypt(encrypt);
                logger.info(decrypt);
                WxCustomerReceiveMsgDto callBackRequestDTO = JSONObject.parseObject(decrypt, WxCustomerReceiveMsgDto.class);
                logger.info(callBackRequestDTO.toString());
                String msgType = callBackRequestDTO.getMsgType();//用户的消息类型
//                String appId = callBackRequestDTO.getAppId();
                //这里处理不同消息的事件实现自己的业务即可，我这里直接全部事件统一处理了。
                if ("text".equals(msgType)
                        || "image".equals(msgType)
                        || "miniprogrampage".equals(msgType)
                        || "link".equals(msgType)) {
                    String openId = callBackRequestDTO.getFromUserName();
//                    if (StringUtils.isBlank(appId)) {
//                        String appIdKey = WECHAT_TOKEN + "-" + openId;
//                        appId = (String) redisCacheUtil.getCacheObject(appIdKey);
//                    }
                    WxCustomerSendMsgDto wxMessageCustomSendTextRequestDTO = new WxCustomerSendMsgDto();
//                    wxMessageCustomSendTextRequestDTO.setAppId(appId);
                    wxMessageCustomSendTextRequestDTO.setMsgtype("text");
                    wxMessageCustomSendTextRequestDTO.setTouser(openId);
                    WxTextMsgDto textDTO = new WxTextMsgDto();
                    textDTO.setContent("请添加客户经理企业微信为你服务");
                    wxMessageCustomSendTextRequestDTO.setText(textDTO);
                    logger.info("推送的消息：{}", JSON.toJSONString(wxMessageCustomSendTextRequestDTO));
                    //推送一条文本消息给用户
                    WxMessageCustomSendResponseDto responseTextDTO = weixinAppletsBatchService.sendCustomMessage(request,wxMessageCustomSendTextRequestDTO);
                    if (!"0".equals(responseTextDTO.getErrcode())) {
                        logger.error("发送客服消息接口返回失败！");
                        out.print("");
                        out.flush();
                    } else {
                        out.print("success");
                        out.flush();
                    }
//                    String mediaId = (String) redisCacheUtil.getCacheObject(openId);
//                    WxMessageCustomSendRequestDTO wxMessageCustomSendRequestDTO = new WxMessageCustomSendRequestDTO();
//                    wxMessageCustomSendRequestDTO.setAppId(appId);
//                    wxMessageCustomSendRequestDTO.setMsgtype("image");
//                    wxMessageCustomSendRequestDTO.setTouser(openId);
//                    ImageDTO imageDTO = new ImageDTO();
//                    imageDTO.setMedia_id(mediaId);
//                    wxMessageCustomSendRequestDTO.setImage(imageDTO);
//                    //推送一张图片给用户
//                    WxMessageCustomSendResponseDTO responseImageDTO = weixinService.messageCustomSend(wxMessageCustomSendRequestDTO);
//                    if (!"0".equals(responseImageDTO.getErrcode())) {
//                        logger.error("发送客服消息接口返回失败！");
//                        out.print("");
//                        out.flush();
//                    } else {
//                        out.print("success");
//                        out.flush();
//                    }
                } else {
                    out.print("success");
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                out.close();
            }
        }

    }




    @RequestMapping(value = "/receiveMsg")
    @ResponseBody
    public String receiveMsg(String arguments) {
        System.out.println(arguments);
//        HttpServletRequest req = getRequest();
//        InputStream inputStream;
//        StringBuffer sb = new StringBuffer();
//        try {
//            inputStream = getRequest().getInputStream();
//            String s;
//            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            while ((s = in.readLine()) != null) {
//                sb.append(s);
//            }
//            in.close();
//            inputStream.close();
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        WeixinEncryptAndDecrypt decrypt = new WeixinEncryptAndDecrypt(APPID, KEY, VALIDATE_TOKEN);
//        String result = "";
//        try {
//            result = decrypt.decrypt(req.getParameter("timestamp"), sb.toString(), req.getParameter("nonce"), req.getParameter("msg_signature"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////    	Cache cache = cacheManager.getCache(Constant.DEFAULT_CACHE);
//        logger.info("receiveMsg解密==" + result);
//        //获取信息型
//        String InfoType = getElement(result, "InfoType");
//
//        if ("component_verify_ticket".equals(InfoType)) {
//            //接收TICKET
//            String ComponentVerifyTicket = (String) redisDao.getObject("wx_cloud_component_verify_ticket");//getElement(result, "ComponentVerifyTicket");
//            String component_access_token = (String) redisDao.getObject("wx_cloud_component_access_token");//获取缓存的数据
//            //用TICKET获取component_access_token 存入缓存
//            if(StringUtils.isBlank(ComponentVerifyTicket)){
//                ComponentVerifyTicket = getElement(result, "ComponentVerifyTicket");
//                redisDao.setObjectWithExpire("wx_cloud_component_verify_ticket",ComponentVerifyTicket,1000*10800);//3小时刷新一次
//            }
//            logger.info("获取ComponentVerifyTicket：" + ComponentVerifyTicket+ " component_access_token:"+component_access_token);
//            if (!"".equals(ComponentVerifyTicket)&&StringUtils.isBlank(component_access_token)) {
//                Map paramMap = new HashMap<String, String>();
//                paramMap.put("component_appid", APPID);
//                paramMap.put("component_appsecret", APPSECRET);
//                paramMap.put("component_verify_ticket", ComponentVerifyTicket);
//                String componetAcc = HttpClientUtil.doPost(COMPONENT_ACCESS_TOKEN, JsonUtil.toJson(paramMap), "utf-8");
//                Map resultMap = JsonUtil.fromJson(componetAcc, HashMap.class);
//                if (StringUtils.isNotEmpty((String) resultMap.get("errmsg"))) {
//                    logger.info("==============获取 component_access_token=================错误提示===================" + resultMap.get("errmsg"));
//                }
//                logger.info("component_access_token==" + componetAcc);
//                String comAcessToken = (String) resultMap.get("component_access_token");
//                if (StringUtils.isNotEmpty(comAcessToken)) {
////					cache.put("component_access_token",comAcessToken );
//                    //储存component_access_token 到数据库
//                    WeixinThirdPlatformExample ex = new WeixinThirdPlatformExample();
//                    ex.createCriteria().andIdEqualTo("1");
//                    WeixinThirdPlatform third = new WeixinThirdPlatform();
//                    third.setComponentAccessToken(comAcessToken);
//                    int i = weixinThirdPlatformMapper.updateByExampleSelective(third, ex);
//                    if (i > 0) {
//                        logger.info("==============插入component_access_token到数据库成功=================");
//                        //一个半小时失效
//                        redisDao.setObjectWithExpire("wx_cloud_component_access_token",comAcessToken,1000*6400);
//                        logger.info("存储wx_cloud_component_access_token:"+comAcessToken);
//                    }
//                } else {
//                    logger.error("component_access_token erro ===" + resultMap.get("errmsg"));
//                }
//            }
//        } else if ("authorized".equals(InfoType)) {
//            logger.info("==============进入授权流程=================");
//            //授权   在数据库记录小程序信息
//            WeixinApplets wx = new WeixinApplets();
////    		String CreateTime =  getElement(result,"CreateTime");
//            wx.setAuthorizationTime(new Date());
//            //授权方appid
//            String authorizerAppid = getElement(result, "AuthorizerAppid");
//            //授权码
//            String authorizationCode = getElement(result, "AuthorizationCode");
//            wx.setAuthorizationCode(authorizationCode);
//            wx.setAuthorizerAppid(authorizerAppid);
////    		String component_access_token = cache.get("component_access_token",String.class);
////    		if(StringUtils.isEmpty(component_access_token)){
//            String component_access_token = weixinThirdPlatformMapper.selectByPrimaryKey("1").getComponentAccessToken();
////    		}
//            logger.info("228==============获取第三方平台component_access_token码==============" + component_access_token);
//            Map paramMap = new HashMap<String, String>();
//            paramMap.put("component_appid", APPID);
//            paramMap.put("authorization_code", getElement(result, "AuthorizationCode"));
//            String auth = HttpClientUtil.doPost(API_QUERY_AUTH + component_access_token, JsonUtil.toJson(paramMap), "utf-8");
//            Map resultMap = JsonUtil.fromJson(auth, HashMap.class);
//            Map infomap = (Map) resultMap.get("authorization_info");
//            //refreshToken
//            String authorizerRefreshToken = "";
//            if (infomap != null && infomap.get("authorizer_refresh_token") != null) {
//                authorizerRefreshToken = (String) infomap.get("authorizer_refresh_token");
//            }
//            wx.setAuthorizerRefreshToken(authorizerRefreshToken);
////    		Map<String, String> tokenMap = getToken(authorizerAppid,authorizerRefreshToken);
////    		String token = tokenMap.get("authorizer_access_token");
//            paramMap.clear();
//            paramMap.put("component_appid", APPID);
//            paramMap.put("authorizer_appid", authorizerAppid);
//            String info = HttpClientUtil.doPost(API_GET_AUTHORIZER_INFO + component_access_token, JsonUtil.toJson(paramMap), "utf-8");
//            resultMap = JsonUtil.fromJson(info, HashMap.class);
//            logger.info("=============小程序信息 ================" + info);
//            infomap = (Map) resultMap.get("authorizer_info");
//            wx.setNickName((String) infomap.get("nick_name"));
//            wx.setHeadImg((String) infomap.get("head_img"));
//            wx.setVerifyTypeInfo(((Map) infomap.get("verify_type_info")).get("id").toString());
//            wx.setUserName((String) infomap.get("user_name"));
//            wx.setSignature((String) infomap.get("signature"));
//            wx.setPrincipalName((String) infomap.get("principal_name"));
//            wx.setBusinessInfo(JsonUtil.toJson(infomap.get("business_info")));
//            wx.setQrcodeUrl((String) infomap.get("qrcode_url"));
////    		wx.setAuthorizationInfo(JsonUtil.toJson(infomap.get("authorization_info")));
////    		wx.setAuthorizerAppid((String) infomap.get("authorization_appid"));
////    		wx.setMiniProgramInfo((String) infomap.get("miniprograminfo"));
//            wx.setNetwork(JsonUtil.toJson((infomap.get("MiniProgramInfo") == null ? Maps.newHashMap() : ((Map) infomap.get("MiniProgramInfo")).get("network"))));//((Map) infomap.get("MiniProgramInfo")).get("network")
//            Map authorizationInfo = (Map) resultMap.get("authorization_info");
//            wx.setFuncInfo(JsonUtil.toJson(authorizationInfo.get("func_info")));
//            Map map = JsonUtil.fromJson(AppletsConfig.config, Map.class);
//            map.put("extAppid", authorizerAppid);
//            wx.setExtJson(JsonUtil.toJson(map));
//           /* wx.setRequestDomain(AppletsConfig.requestDomain);
//            wx.setDownloadDomain(AppletsConfig.downloadDomain);*/
//            wx.setAppletsStatus("1");
//            weixinAppletsMapper.insertSelective(wx);
//            logger.info("授权成功 ====" + authorizerAppid + "nick_name ==" + (String) infomap.get("nick_name"));
//        } else if ("unauthorized".equals(InfoType)) {
//            //解除授权   在数据库中删除小程序
//            WeixinAppletsExample ex = new WeixinAppletsExample();
//            ex.createCriteria().andAuthorizerAppidEqualTo(getElement(result, "AuthorizerAppid"));
//            weixinAppletsMapper.deleteByExample(ex);
//            logger.info("=======解绑成功 =====appid=========" + getElement(result, "AuthorizerAppid"));
//        }

        return "success";
    }

}
