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
    
    public static String ORDER_BEGIN="begin";   //已经发单
    
    public static String ORDER_COMPLETE="complete";  //完成订单
    
    public static String ORDER_PRE_CREATED="pre_created";//待确认运费
    
    public static String ORDER_PRE_PRICED="pri_priced";//已确认运费
    
    public static String ORDER_SENDING="sending";  //配送中
    
    public static String ORDER_DELAY="delay"; //延迟
    
    public static String ORDER_FINISHED="finished";  //完美完成
    
    public static String ORDER_CANCLE="cancle";  //取消
    
    public static String ORDER_TRASH="trash";   //丢弃
    
    public static String ORDER_REFUSE="refuse";
    
    public static String ORDER_VALIDATING="validating";
    
    public static String ORDER_NO_CASH="no_cash";
    
    public static String ORDER_VALID="valid";
}
