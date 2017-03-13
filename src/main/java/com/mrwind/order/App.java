package com.mrwind.order;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
    
    public static String ORDER_MQ_3001="3001";  
    
    public static String ORDER_CREATE="create";    //未发单
    
    public static String ORDER_BEGIN="begin";   //已发单
    
    public static String ORDER_SENDING="sending";  //配送中
    
    public static String ORDER_END="end";  //已经结束
    
    public static String ORDER_COMPLETE="complete";  //完成订单
    
    public static String ORDER_ERROR_COMPLETE="pre_error_complete";  //异常妥投
    
    public static String ORDER_PRE_CREATED="pre_created";//待确认运费
    
    public static String ORDER_PRE_PRICED="pre_priced";//已确认运费
    
    public static String ORDER_PRE_PAY_PRICED="pre_pay_priced";//支付完成
    
    public static String ORDER_CANCEL="cancel";  //取消
    
    public static String ORDER_TYPE_AFTER="after";
    
    //支付的订单
    public static String RDKEY_PAY_ORDER="rdkey_pay_order";
    
    public static String RDKEY_AFTER_ORDER="rdkey_after_order";

    //妥投验证码
    public static String RDKEY_VERIFY_CODE="rdkey_verify_code_";

    public static String SESSION_KEY="sdf^#*(JFDK(@)#$TGJKEUID&*@#%";

    //根据用户id发送短信
    public static final String MSG_SEND_USERID = "WindCloud/msg/info/batch";

    //根据用户手机号码发送短信
    public static final String MSG_SEND_TEL = "/WindCloud/msg/record/new";

}
