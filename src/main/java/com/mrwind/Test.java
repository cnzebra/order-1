package com.mrwind;

import com.mrwind.common.util.Md5Util;
import com.mrwind.order.App;

public class Test {

	public static void main(String[] args) {
		
		String token = Md5Util.string2MD5("1bbc3ba96d46490baa3ab423843c43e2"+App.SESSION_KEY);
		System.out.println(token);
	}

}
