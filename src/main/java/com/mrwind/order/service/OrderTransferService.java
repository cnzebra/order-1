package com.mrwind.order.service;

import com.alibaba.fastjson.JSONArray;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.App;
import com.mrwind.order.dao.OrderTransferDao;
import com.mrwind.order.entity.*;
import com.mrwind.order.repositories.OrderTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
public class OrderTransferService {

    @Autowired
    OrderTransferRepository orderTransferRepository;

    @Autowired
    OrderTransferDao orderTransferDao;

    private final static String PICK = "PICK";
    private final static String PICK_TURN = "PICK_TURN";
    private final static String TURN_PICK = "TURN_PICK";
    private final static String TURN_SEND = "TURN_SEND";
    private final static String SEND = "SEND";

    public void save(Express express) {
        String status = express.getStatus();
        Date nowDate = new Date();
        List<Line> lines = express.getLines();


        switch (status) {
            case App.ORDER_PICK:

                Line line = lines.get(0);
                User user = line.getExecutorUser();
                ShopUser shopUser = express.getShop();
                Category category = express.getCategory();
                OrderTransfer orderTransfer = new OrderTransfer(express.getExpressNo(), user.getId(), user.getLat(), user.getLng(), user.getAddress(), nowDate, shopUser.getId(), shopUser.getLat(), shopUser.getLng(), shopUser.getAddress(), PICK);
                orderTransfer.setWeight(new BigDecimal(category.getWeight()));
                //计算距离和重量

                break;
            case App.ORDER_SENDING:
                saveOrderTransfer(express);
                break;
            case App.ORDER_TRANSFER:
                saveOrderTransfer(express);
                break;
            case App.ORDER_WAIT_COMPLETE:
                completeOrderTransfer(express);
                break;
            case App.ORDER_COMPLETE:
                break;
            default:
                break;

        }
    }

    /**
     * 获取交通工具的配置
     *
     * @param userId
     */
    private void getToolPrice(String userId) {
        JSONArray jsonArray = HttpUtil.findConveyanceTool(userId);
        if(jsonArray != null){

        }
    }

    private void completeOrderTransfer(Express express) {
        String expressNo = express.getExpressNo();
        Date nowDate = new Date();
        List<Line> lines = express.getLines();
        int size = lines.size();
        Line last = lines.get(size - 1);
        Line preLast = lines.get(size - 2);
        User excutor = last.getExecutorUser();
        User preExcutor = preLast.getExecutorUser();
        Category category = express.getCategory();
        BigDecimal weight = new BigDecimal(category.getWeight());
        Double nowLat = excutor.getLat();
        Double nowLng = excutor.getLng();
        String nowAddr = excutor.getAddress();
        OrderTransfer orderTransfer;
        if (excutor.getId().equals(preExcutor.getId())) {//直派
            orderTransfer = new OrderTransfer(expressNo, excutor.getId(), nowLat, nowLng, nowAddr, nowDate, preExcutor.getId(), preExcutor.getLat(), preExcutor.getLng(), preExcutor.getAddress(), SEND);
        } else {//转派
            if (size - 3 > 0) {
                Line prePLast = lines.get(size - 3);
                User prePExcutor = prePLast.getExecutorUser();
                orderTransfer = new OrderTransfer(expressNo, preExcutor.getId(), nowLat, nowLng, nowAddr, nowDate, prePExcutor.getId(), prePExcutor.getLat(), prePExcutor.getLng(), prePExcutor.getAddress(), PICK_TURN);
            } else {
                ShopUser shopUser = express.getShop();
                 orderTransfer = new OrderTransfer(expressNo, preExcutor.getId(), nowLat, nowLng, nowAddr, nowDate, shopUser.getId(), shopUser.getLat(), shopUser.getLng(), shopUser.getAddress(), PICK_TURN);
            }
            OrderTransfer orderTransferNext = new OrderTransfer(expressNo, excutor.getId(), nowLat, nowLng, nowAddr, nowDate, preExcutor.getId(), preExcutor.getLat(), preExcutor.getLng(), preExcutor.getAddress(), TURN_SEND);
            orderTransferNext.setWeight(weight);
            orderTransferRepository.save(orderTransferNext);
        }
        if(orderTransfer != null){
            orderTransfer.setWeight(weight);
            orderTransferRepository.save(orderTransfer);
        }

    }

    private void saveOrderTransfer(Express express) {
        String expressNo = express.getExpressNo();
        Date nowDate = new Date();
        List<Line> lines = express.getLines();
        int size = lines.size();
        Line last = lines.get(size - 1);
        Line preLast = lines.get(size - 2);

        Category category = express.getCategory();
        BigDecimal weight = new BigDecimal(category.getWeight());

        User excutor = last.getExecutorUser();
        User preExcutor = preLast.getExecutorUser();

        Double nowLat = excutor.getLat();
        Double nowLng = excutor.getLng();
        String nowAddr = excutor.getAddress();
        OrderTransfer orderTransfer;
        if (size - 3 > 0) {
            Line prePLast = lines.get(size - 3);
            User prePExcutor = prePLast.getExecutorUser();
            orderTransfer = new OrderTransfer(expressNo, preExcutor.getId(), nowLat, nowLng, nowAddr, nowDate, prePExcutor.getId(), prePExcutor.getLat(), prePExcutor.getLng(), prePExcutor.getAddress(), PICK_TURN);
            orderTransfer.setWeight(weight);
        } else {
            ShopUser shopUser = express.getShop();
            orderTransfer = new OrderTransfer(expressNo, preExcutor.getId(), nowLat, nowLng, nowAddr, nowDate, shopUser.getId(), shopUser.getLat(), shopUser.getLng(), shopUser.getAddress(), PICK_TURN);
            orderTransfer.setWeight(weight);
        }

        OrderTransfer orderTransferNext = new OrderTransfer(expressNo, excutor.getId(), nowLat, nowLng, nowAddr, nowDate, preExcutor.getId(), preExcutor.getLat(), preExcutor.getLng(), preExcutor.getAddress(), TURN_PICK);
        orderTransferNext.setWeight(weight);
        orderTransferRepository.save(orderTransfer);
        orderTransferRepository.save(orderTransferNext);
        //计算距离和重量
    }

}
