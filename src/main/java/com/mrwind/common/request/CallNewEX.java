package com.mrwind.common.request;

import java.util.Date;
import java.util.List;

/**
 * Created by feng on 2017/4/20.
 */
public class CallNewEX extends CallNew {

    private String id;
    private Date createTime ;        //  创建时间
    private Date updateTime ;        //  创建时间


    private List<PersonAddressOrder> receivers;

    public List<PersonAddressOrder> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<PersonAddressOrder> receivers) {
        this.receivers = receivers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
