package com.netty.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间操作工具类
 * 
 * @author lucher
 */
public class TimeUtil {

	/**
	 * 显示格式如 "yyyy-MM-dd HH:mm:ss" 以指定格式获取当前时间
	 * 
	 * @param type
	 *            显示的格式
	 * 
	 */
	public static String getCurrentTime(String type) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(type, Locale.CHINA);
		return format.format(date);
	}

}
