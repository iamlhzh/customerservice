package com.lhzh.customerservice.controller;


import com.lhzh.customerservice.utils.WxUtils;
import com.lhzh.utils.XmlUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author sunmo
 * @date 2021.10.22
 * 说明: 活动预约, 及人员认证controller
 */
@RestController
@RequestMapping("valueReadApi")
public class ValueReadApiController  {

    private Logger logger = LoggerFactory.getLogger(ValueReadApiController.class);

//
//    /**
//     * 获取微信公众号用户列表
//     * @param
//     * @return
//     */
//    @RequestMapping(value = "/getweChatuserInfo", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonResponse<Map<String,Object>> getweChatuserInfo() {
//        JsonResponse<Map<String,Object>> jsonRes = new JsonResponse<>();
//        jsonRes = valueReadService.getweChatuserInfo();
//        return jsonRes;
//    }


    @RequestMapping(value = "/getFollowEvent")  //, produces = "text/xml;charset=UTF-8"
    public void getFollowEvent(HttpServletRequest request, HttpServletResponse response) {
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
        }else {
            StringBuffer reqXmlData = new StringBuffer();
            try {
                InputStream inputStream = request.getInputStream();
                String s;
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                while ((s = in.readLine()) != null) {
                    reqXmlData.append(s);
                }

                String callbackMessage = reqXmlData.toString();

                if (!ObjectUtils.isEmpty(callbackMessage)) {
                    String toUserName = XmlUtils.getElement(callbackMessage, "ToUserName");
                    String FromUserName = XmlUtils.getElement(callbackMessage, "FromUserName");
                    String CreateTime = XmlUtils.getElement(callbackMessage, "CreateTime");
                    String MsgType = XmlUtils.getElement(callbackMessage, "MsgType");
                    String Event = XmlUtils.getElement(callbackMessage, "Event");
                    if("subscribe".equals(Event)){
                        System.out.println(Event);
                    }
                    System.out.println(11);

                }
                in.close();
                inputStream.close();
            } catch (IOException e) {
                System.out.println("流解析xml数据异常!");
                e.printStackTrace();
            }
            //判断请求数据是否为空
            if (reqXmlData.length() <= 0) {
                System.out.println("请求数据为空!");
            }
        }
    }

 }
