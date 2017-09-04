package club.lovemo.utils;

import android.app.Application;
import android.content.Context;

import org.xutils.x;

public class LovemoApplication extends Application {
	private static Context context;
	public static Context getContext(){
		return context;

	}
	@Override
	public void onCreate() {
		super.onCreate();
		x.Ext.init(this);
		context=getApplicationContext();
		LogUtils.print("TEST","Application onCreate");
	}

}
