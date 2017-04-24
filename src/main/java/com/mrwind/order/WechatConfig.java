package com.mrwind.order;

/**
 * Created by admin on 16/10/24.
 */
public class WechatConfig {

    public static  String appId = "wxeda2f6550280ac63";
    //微信统一下单地址
     public static String wx_order= "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信公众号APPID
    public static String mchappid = "wxeda2f6550280ac63";
    //微信商户后台设置的Key
    public static String wx_key = "e44073b3e8d2e9181781d8d46e1c4d77";
    public static String secret ="8eef8247a26a7887a35ee5e887f38c50";

    //微信公众号商户ID
    public static String mchid = "1373099902";

    //业务系统支付回调网址
    public static String wx_callback = "http://dev.api.gomrwind.com:5000/WindData/wechat/wechatNotify";

    public static String body = "客户充值";
    public static String ORDER_PAY = "订单支付";

    //wechat get auth token url
    public static String wechat_auth_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token";

    public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

}
