package com.mrwind.order.service;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.Remind;
import com.mrwind.order.entity.User;
import com.mrwind.order.repositories.RemindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 *     author : huanghaikai
 *     e-mail : hakanhuang@gmail.com
 *     time   : 2017/04/08
 *     desc   : 催派
 *     version: 1.0
 * </pre>
 */
@Service
public class RemindService {

    @Autowired
    RemindRepository remindRepository;

    @Autowired
    ExpressService expressService;

    public Remind findRemind(String expressNo) {
        return remindRepository.findByExpressNo(expressNo);
    }

    public JSONObject remindUser(Express express) {
        User receiver = express.getReceiver();
        List<Line> lines = express.getLines();
        int currentLine = express.getCurrentLine();
        if (lines == null || currentLine < 1) {
            return JSONFactory.getfailJSON("没有执行人信息");
        }
        if(currentLine > lines.size()){
            return JSONFactory.getfailJSON("订单轨迹有错，无法找到当前执行人");
        }
        User executorUser = lines.get(currentLine - 1).getExecutorUser();
        String content = executorUser.getName() + "你好,收件人" + receiver.getName() + receiver.getTel() + "向你发起了催派,请尽快送达或联系收件人";
        HttpUtil.sendSMSToUserTel(content, executorUser.getTel());
        boolean remindResult = expressService.updateExpressReminded(express.getExpressNo());
        if (remindResult) {
            Remind remind = new Remind(express);
            remindRepository.save(remind);
            return JSONFactory.getSuccessJSON();
        }
        return JSONFactory.getfailJSON("催派失败");
    }
}
