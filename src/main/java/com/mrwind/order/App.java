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
    
    public static String CALL_CREATE="create";  
    
    public static String ORDER_BEGIN="begin";
    
    public static String ORDER_COMPLETE="complete";
    
    public static String ORDER_PRE_CREATED="pre_created";//待确认运费
    
    public static String ORDER_PRE_PRICED="pri_priced";//已确认运费
    
    public static String ORDER_SENDING="sending";
    
    public static String ORDER_DELAY="delay";
    
    public static String ORDER_FINISHED="finished";
    
    public static String ORDER_CANCLE="cancle";
    
    public static String ORDER_TRASH="trash";
    
    public static String ORDER_REFUSE="refuse";
    
    public static String ORDER_VALIDATING="validating";
    
    public static String ORDER_NO_CASH="no_cash";
    
    public static String ORDER_VALID="valid";
}
