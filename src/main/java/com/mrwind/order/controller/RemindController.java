package com.mrwind.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Remind;
import com.mrwind.order.service.ExpressService;
import com.mrwind.order.service.RemindService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <pre>
 *     author : huanghaikai
 *     e-mail : hakanhuang@gmail.com
 *     time   : 2017/04/08
 *     desc   : 订单催派
 *     version: 1.0
 * </pre>
 */

@Controller
@RequestMapping("express/remind")
public class RemindController {

    @Autowired
    RemindService remindService;

    @Autowired
    ExpressService expressService;


    /**
     * 发起催派
     * <p>
     * 收件人向配送员发起催派
     *
     * @param expressNo 订单号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/expressNo", method = RequestMethod.GET)
    public JSONObject sendReceiveMessage(String expressNo) {
        Express express = expressService.selectByNo(expressNo);
        if (express == null) {
            return JSONFactory.getfailJSON("查找不到该订单");
        }
        if(express.isReminded()){
            return JSONFactory.getfailJSON("已发起催派,不要重复发起");
        }
       return remindService.remindUser(express);
    }

    @ResponseBody
    @RequestMapping(value = "/findByExpressNo", method = RequestMethod.GET)
    public JSONObject findRemindByExpressNo(String expressNo) {

        if (StringUtils.isBlank(expressNo)) {
            return JSONFactory.getfailJSON("订单号不能为空");
        }

        Remind remind = remindService.findRemind(expressNo);

        if (remind != null) {
            JSONObject result = JSONFactory.getSuccessJSON();
            result.put("content", remind);
            return result;
        }
        return JSONFactory.getfailJSON("未查询到数据");
    }



}
