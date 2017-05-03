package com.mrwind.order.controller.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.mrwind.common.request.ClaimOrder;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.common.util.QueryDateUtils;
import com.mrwind.order.entity.vo.ResponseExpress;
import com.mrwind.order.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.DateUtils;
import com.mrwind.order.App;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.User;
import com.mrwind.order.entity.vo.ShopExpressVO;
import com.mrwind.order.service.ExpressBindService;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("app/express")
public class AppExpressController {

    @Autowired
    ExpressService expressService;

    @Autowired
    OrderService orderService;

    @Autowired
    ExpressBindService expressBindService;

    /***
     * 配送员加单
     *
     * @param expressJson
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public JSONObject create(@RequestBody JSONObject expressJson) {
        Express express = JSONObject.toJavaObject(expressJson, Express.class);
        express.setCreateTime(Calendar.getInstance().getTime());
        List<Line> lines = new ArrayList<>();
        User executorUser = JSONObject.toJavaObject(expressJson.getJSONObject("executor"), User.class);
        if (executorUser == null) {
            return JSONFactory.getErrorJSON("找不到配送员信息，无法加单！");
        }

        if (!expressBindService.checkBind(express.getBindExpressNo())) {
            return JSONFactory.getErrorJSON("该单号已经绑定过其它单号");
        }

        Line line = new Line();
        line.setPlanTime(express.getCreateTime());
        line.setExecutorUser(executorUser);
        line.setNode(express.getSender().getAddress());
        line.setIndex(1);
        lines.add(line);

        express.setLines(lines);

        express.setExcutorId(executorUser != null? executorUser.getId() : null);
        express.setOperaTime(new Date());

        Express initExpress;
        if (App.ORDER_TYPE_AFTER.equals(express.getType())) {
            initExpress = expressService.initAfterExpress(express);
        } else {
            initExpress = expressService.initExpress(express);
        }

        JSONObject successJSON = JSONFactory.getSuccessJSON();
        successJSON.put("data", initExpress);
        return successJSON;
    }

    @ResponseBody
    @RequestMapping(value = "/claimBatchOrder", method = RequestMethod.POST)
    public JSONObject claimBatchOrder(@RequestBody JSONObject json) {

        expressService.updateExpress(json);

        JSONObject successJSON = JSONFactory.getSuccessJSON();
//		successJSON.put("data", initExpress);
        return successJSON;
    }

    @ResponseBody
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public Result select(@RequestBody Express express) {
        Express res = expressService.selectByExpress(express);
        return Result.success(res);
    }

    @ResponseBody
    @RequestMapping(value = "/select/all/{pageIndex}_{pageSize}", method = RequestMethod.POST)
    public Result selectAll(@RequestBody Express express, @PathVariable("pageIndex") Integer pageIndex,
                            @PathVariable("pageSize") Integer pageSize) {
        Page<Express> selectAllByExpress = expressService.selectAllByExpress(express, pageIndex - 1, pageSize);
        return Result.success(selectAllByExpress.getContent());
    }

    @ResponseBody
    @RequestMapping(value = "/select/shop/{pageIndex}_{pageSize}", method = RequestMethod.GET)
    public Result selectShop(@RequestParam("shopId") String shopId, @PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize) {
        List<ShopExpressVO> shopExpress = expressService.selectShopExpress(shopId, pageIndex - 1, pageSize);
        return Result.success(shopExpress);
    }


    @ResponseBody
    @RequestMapping(value = "/select/param/{pageIndex}_{pageSize}", method = RequestMethod.GET)
    public Result selectParamAll(String param, String fenceName, String shopId, String mode, String status, String day, String dueTime,
                                 @PathVariable("pageIndex") Integer pageIndex, @PathVariable("pageSize") Integer pageSize) throws ParseException {
        Date dueTime2 = null;
        if (StringUtils.isNotBlank(dueTime)) {
            dueTime2 = DateUtils.parseDate(dueTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ");
        }
        List<Express> selectAll = expressService.selectAll(param, shopId, fenceName, mode, status, day, dueTime2, pageIndex - 1,
                pageSize);
        return Result.success(selectAll);
    }

    @ResponseBody
    @RequestMapping(value = "/select/app/{pageIndex}_{pageSize}", method = RequestMethod.GET)
    public Result selectParamAll(String param, String shopId, String time,
                                 @PathVariable("pageIndex") Integer pageIndex, @PathVariable("pageSize") Integer pageSize) throws ParseException {
        Date timeDate = null;
        if (StringUtils.isNotBlank(time)) {
            timeDate = DateUtils.parseDate(time, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ");

        }
        List<ResponseExpress> selectAll = expressService.selectApp(param, shopId,  timeDate,  pageIndex - 1, pageSize);
        return Result.success(selectAll);
    }

    /**
     * 发送验证码
     *
     * @param expressNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/send/code", method = RequestMethod.GET)
    public JSONObject sendCode(String expressNo) {
        return expressService.sendCode(expressNo);
    }

    @ResponseBody
    @RequestMapping(value = "/scan/v2", method = RequestMethod.GET)
    public Result scanV2(@RequestParam("userId") String userId,
                       @RequestParam(value = "lat",required = false,defaultValue = "0") double lat,
                       @RequestParam(value = "lng",required = false,defaultValue = "0") double lng) {

        return Result.success(orderService.getMyExpress( userId, 0, 50));
    }
}
