package club.lovemo.utils;

import android.view.View;
import club.lovemo.lovemomusic.R;

/**
 * 设置默认背景和更换背景
 * @author
 */
public class SetBackgroundImage {

//	public static void changeBackgroundImage(int i) {
//
//	}

	/**
	 * 设置背景图片
	 *
	 */
	public static void setBackGround(View view,String bg) {
		if (bg != null) {
			if ("bg1".equals(bg)) {
				view.setBackgroundResource(R.mipmap.meilizileft);
			}
			if ("bg2".equals(bg)) {
				view.setBackgroundResource(R.mipmap.zongseleft);
			}
			if ("bg3".equals(bg)) {
				view.setBackgroundResource(R.mipmap.left);
			}
		}else{
			view.setBackgroundResource(R.mipmap.meilizileft);
		}

	}
//
//	/**
//	 * 更改默认背景图片
//	 * @param activity
//	 * @param imageTag
//	 */
//	public static void saveBackground(Activity activity, String imageTag) {
//		SharedPreferences preferences = activity.getSharedPreferences("bg",
//				Activity.MODE_PRIVATE);
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString("background", imageTag);
//		editor.commit();
//
//	}
//
//	/**
//	 * 得到默认图片标识
//	 * @param activity
//	 * @return 
//	 */
//	private static String getBG(Activity activity) {
//		SharedPreferences preferences = activity.getSharedPreferences("bg",Activity.MODE_PRIVATE);
//		String bg = preferences.getString("background", null);
//		return bg;
//
//	}
}
