package com.mrwind;

import com.mrwind.common.util.Md5Util;
import com.mrwind.order.App;

public class Test {

	public static void main(String[] args) {
		
		String token = Md5Util.string2MD5("a1fa887f60c142c5b39db08dcee5c96b"+App.SESSION_KEY);
		System.out.println(token);
	}

}
