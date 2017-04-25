package com.mrwind.order.controller.web;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.request.CallNewEX;
import com.mrwind.order.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.service.OrderService;

@Controller
@RequestMapping("web/order")
public class WebOrderController {

    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;

    @Autowired
    RedisCache redisCache;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public JSONObject create(@RequestBody JSONObject json) {
        Order order = JSONObject.toJavaObject(json, Order.class);
        if (order.getShop() == null || StringUtils.isEmpty(order.getShop().getId())) {
            return JSONFactory.getErrorJSON("商户信息不能为空");
        }
        if (order.getSender() == null) {
            return JSONFactory.getErrorJSON("寄件人信息不明，无法下单");
        }

        List<Express> result = orderService.initAndInsert(order);
        JSONObject successJSON = JSONFactory.getSuccessJSON();
        successJSON.put("content", result);
        return successJSON;
    }

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JSONObject add(@RequestBody CallNewEX callNewEX) {

        List<Express> result = orderService.initAndInsert(callNewEX);
        JSONObject successJSON = JSONFactory.getSuccessJSON();
        successJSON.put("content", result);
        return successJSON;
    }

    @ResponseBody
    @RequestMapping(value = "/create/list", method = RequestMethod.POST)
    public JSONObject createList(@RequestBody JSONObject json, HttpServletResponse response) {

        System.out.println(System.currentTimeMillis());
        JSONObject shopJson = json.getJSONObject("shop");
        if (shopJson == null) {
            return JSONFactory.getErrorJSON("商户信息不明，无法下单");
        }

        String shopId = shopJson.getString("id");
        JSONObject res = HttpUtil.findShopById(shopId);
        if (res == null) {
            response.setStatus(401);
            return JSONFactory.getErrorJSON("请重新登录！");
        }


        ShopUser shopUser = JSONObject.toJavaObject(shopJson, ShopUser.class);
        JSONObject senderJson = json.getJSONObject("sender");

        if (senderJson == null) {
            return JSONFactory.getErrorJSON("寄件人信息不明，无法下单");
        }

        User sender = JSONObject.toJavaObject(senderJson, User.class);

        JSONArray jsonArray = json.getJSONArray("expressList");
        System.out.println(System.currentTimeMillis());
        Iterator<Object> iterator = jsonArray.iterator();
        List<Order> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Order order = JSONObject.toJavaObject((JSONObject) iterator.next(), Order.class);
            order.setShop(shopUser);
            order.setSender(sender);
            list.add(order);
        }
        System.out.println(System.currentTimeMillis());
        orderService.initAndInsert(list);
        System.out.println(System.currentTimeMillis());
        return JSONFactory.getSuccessJSON();
    }

    @ResponseBody
    @RequestMapping(value = "/excel/parse", method = RequestMethod.POST)
    public JSONObject excelParse(@RequestParam("expressFile") CommonsMultipartFile file)
            throws IllegalStateException, IOException {

        if (file == null) {
            return JSONFactory.getErrorJSON("没有找到您上传的文件");
        }
        String fileFileName = file.getOriginalFilename();
        if (fileFileName == null || !fileFileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) {
            return JSONFactory.getErrorJSON("excel文件格式错误");
        }
        String fileType = "2003";
        if (fileFileName.matches("^.+\\.(?i)(xlsx)$")) {
            fileType = "2007";
        }
        Workbook wb = null;
        if (fileType.equals("2003")) {
            wb = new HSSFWorkbook(file.getInputStream());
        }
        if (fileType.equals("2007")) {
            wb = new XSSFWorkbook(file.getInputStream());
        }

        String sheetName = wb.getSheetName(0);
        Sheet sheet = null;
        if (sheetName == null || "".equals(sheetName)) {
            sheet = wb.getSheetAt(0);
        } else {
            sheet = wb.getSheet(sheetName);
        }

        List<JSONObject> list = new ArrayList<>();
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            JSONObject jsonObject = new JSONObject();
            Row row = sheet.getRow(i);
//			if (row.getCell(1) != null) {
//				try {
//					bindExpressNo = Double.valueOf(row.getCell(1).getStringCellValue());
//				} catch (Exception e) {
//					bindExpressNo = row.getCell(1).getNumericCellValue();
//				}
//			}
            Long pk = redisCache.getPK("express", 1);
            jsonObject.put("expressNo", pk.toString());
            if (row.getCell(1) != null) {
                String receiverUserName = row.getCell(1).getStringCellValue();
                jsonObject.put("receiverUserName", receiverUserName);
            }
            String receiverTel;
            if (row.getCell(2) != null) {
                try {
                    receiverTel = row.getCell(2).getStringCellValue();
                } catch (Exception e) {
                    DecimalFormat df = new DecimalFormat("0");
                    receiverTel = df.format(row.getCell(2).getNumericCellValue());
                }
                jsonObject.put("receiverTel", receiverTel);
            } else {
                continue;
            }
            if (row.getCell(3) != null) {
                String receiverAddress = row.getCell(3).getStringCellValue();
                jsonObject.put("receiverAddress", receiverAddress);
            } else {
                continue;
            }
            if (row.getCell(4) != null) {
                String remark = row.getCell(4).getStringCellValue();
                jsonObject.put("weight", remark);
            }
            if (row.getCell(5) != null) {
                String remark = row.getCell(5).getStringCellValue();
                jsonObject.put("remark", remark);
            }
            list.add(jsonObject);
        }

        JSONObject successJSON = JSONFactory.getSuccessJSON();
        successJSON.put("list", list);
        return successJSON;
    }

    @ResponseBody
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public Result select(@RequestBody Order order) {
        Order res = orderService.selectByOrder(order);
        return Result.success(res);
    }

    @ResponseBody
    @RequestMapping(value = "/select/all/{pageIndex}_{pageSize}", method = RequestMethod.POST)
    public Result selectAll(@RequestBody Order order, @PathVariable("pageIndex") Integer pageIndex,
                            @PathVariable("pageSize") Integer pageSize) {
        Page<Order> selectAllByOrder = orderService.selectAllByOrder(order, pageIndex - 1, pageSize);
        return Result.success(selectAllByOrder);
    }

    @ResponseBody
    @RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
    public JSONObject sendMessage(@RequestParam(value = "content") String content,
                                  @RequestParam(value = "tel") String tel, @RequestParam(value = "expressNo") String expressNo) {

        orderService.sendReceiveMsg(content, tel, expressNo);
        JSONObject json = JSONFactory.getSuccessJSON();
        return json;
    }

    /**
     * 我的货物
     * @param userId
     * @param condition
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getMyGoods", method = RequestMethod.GET)
    public Result testFind(@RequestParam(value = "userId") String userId,
                               @RequestParam(value = "condition", defaultValue = "") String condition,
                               @RequestParam(value = "fliterStatus", defaultValue = "cancel") String fliterStatus,
                               @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                               @RequestParam(value = "lat", defaultValue = "0") Double lat,
                               @RequestParam(value = "lng", defaultValue = "0") Double lng,
                               @RequestParam(value = "radius", defaultValue = "500") Double radius,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        JSONObject jsonObject = orderService.findExpress( userId, condition, pageNo - 1, pageSize, lat, lng, radius, fliterStatus);
        return Result.success(jsonObject);
    }

}
