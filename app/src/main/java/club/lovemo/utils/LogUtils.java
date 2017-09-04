package club.lovemo.utils;

import android.util.Log;


public class LogUtils {
	private static final int VERSION =1;
	private static final int Level =1;
	public static void print(String tag,String msg){
		if(Level<=1){
			Log.i(tag, msg);
		}
	}

}
