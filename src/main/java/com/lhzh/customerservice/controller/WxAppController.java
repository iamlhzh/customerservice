package com.lhzh.customerservice.controller;


import com.lhzh.customerservice.dto.JsonResult;
import com.lhzh.customerservice.service.WxAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("wxApp")
public class WxAppController {

    @Autowired
    WxAppService wxAppService;
    
    /**
     * 通过code获取wx unionId
     *
     * @return
     */
    @RequestMapping(value = "/getWxUnionIdByCode", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult<Map<String, String>> getWxUnionIdByCode(@RequestParam("code") String code){
        JsonResult<Map<String, String>> response = wxAppService.getWxUnionIdByCode(code);
        return response;
    }

}
