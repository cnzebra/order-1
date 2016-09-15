package com.mrwind.common.wrapper;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2015/10/12 0012.
 */
public class JsonObjectWrapper  {

    private JSONObject jsonObject;

    public JsonObjectWrapper() {
    }

    public JsonObjectWrapper(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
