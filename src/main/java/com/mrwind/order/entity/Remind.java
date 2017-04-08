package com.mrwind.order.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 *     author : huanghaikai
 *     e-mail : hakanhuang@gmail.com
 *     time   : 2017/04/08
 *     desc   : 催派信息
 *     version: 1.0
 * </pre>
 */
@Document
public class Remind {

    @Id
    private String id;

    @Indexed
    private String expressNo;

    /**
     * 催派发起人
     */
    private User remindFrom;

    /**
     * 被催派人
     */
    private User remindTo;

    private Date createTime;

    public Remind() {}

    public Remind(Express express) {
        this.expressNo = express.getExpressNo();
        this.remindFrom = express.getReceiver();
        this.remindTo = express.getLines().get(express.getCurrentLine() - 1).getExecutorUser();
        this.createTime = Calendar.getInstance().getTime();
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public User getRemindFrom() {
        return remindFrom;
    }

    public void setRemindFrom(User remindFrom) {
        this.remindFrom = remindFrom;
    }

    public User getRemindTo() {
        return remindTo;
    }

    public void setRemindTo(User remindTo) {
        this.remindTo = remindTo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
