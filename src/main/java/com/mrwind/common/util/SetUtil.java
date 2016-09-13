package com.mrwind.common.util;

import java.util.Iterator;
import java.util.Set;

public class SetUtil {

	public static String ParseToString(Set<String> set) {
		if (set == null)
			return "";
		StringBuffer sb = new StringBuffer();
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next() + ",");
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}

}
