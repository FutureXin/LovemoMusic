package club.lovemo.utils;

import android.content.Context;


/**
 * 缓存工具类
 *
 * @author Kevin
 *
 */
public class CacheUtils {

	/**
	 * 设置缓存 , value是json
	 */
	public static void setCache(Context cex,String key, String value) {
		PrefUtils.setString(cex,key, value);
		//可以将缓存放在文件中, 文件名就是Md5(url), 文件内容是json
	}

	/**
	 * 获取缓存
	 */
	public static String getCache(Context cex,String key) {
		return PrefUtils.getString(cex, key, null);
	}
}
