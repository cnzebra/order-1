package com.mrwind;

import com.mrwind.common.util.DateUtils;
import com.mrwind.common.util.Md5Util;
import com.mrwind.order.App;

public class Test {

	public static void main(String[] args) {
		
		Long valueOf = Long.valueOf(DateUtils.getDate("yyyyMMdd")+"000000");
		System.out.println(valueOf);
	}

}
