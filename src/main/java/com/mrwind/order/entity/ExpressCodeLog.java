package com.mrwind.order.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by hakan on 3/6/17.
 */
@Document
public class ExpressCodeLog {

    @Id
    private String id;

    private String expressNo;

    private Date createTime;

    /*
    * 操作类型
    * Send 发送
    * Verify 验证
    */
    private String type;


    //验证码
    private String verifyCode;

    public ExpressCodeLog(){}

    public ExpressCodeLog(String expressNo, Date createTime, String type, String verifyCode) {
        this.expressNo = expressNo;
        this.createTime = createTime;
        this.type = type;
        this.verifyCode = verifyCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String code) {
        this.verifyCode = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public static class TypeConstant{
        public static String TYPE_SEND = "type_send";
        public static String TYPE_VERIFY = "type_verify";
    }

}
