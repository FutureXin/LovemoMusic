package club.lovemo.lovemomusic;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.List;

import club.lovemo.fragment.ContentFragment;
import club.lovemo.fragment.LeftMenuFragment;
import club.lovemo.utils.AndroidBug54971Workaround;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.Tools;

public class MusicHome_Activity extends AppCompatActivity {
	private static final String FRAGMENT_LEFT_MENU = "fragment_left_menu";
	private static final String FRAGMENT_CONTENT = "fragment_content";
	public SlidingMenu slidingMenu;
	public static boolean isStatrService;
	public static int status_he;
	@SuppressLint("NewApi") @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isMarshmallow()){
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			//透明状态栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		//获取状态栏高度——方法1
		//获取status_bar_height资源的ID
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			status_he = getResources().getDimensionPixelSize(resourceId);
		}
		LogUtils.print(AppConstants.LOG_STR, "状态栏高度:" + status_he);
		setContentView(R.layout.activity_musichome);
		AndroidBug54971Workaround.assistActivity(findViewById(R.id.home_relative));
		//获取系统所有正在运行的服务，检测是否存在本软件的服务，如果不存在则启动并且发送初始化mediaplayer的请求
		ActivityManager mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(100);
		if(!MusicServiceIsStart(mServiceList, AppConstants.SERVICE_CLASS)){
			isStatrService=true;
		}else{
			isStatrService=false;
		}
		slidingMenu=new SlidingMenu(this);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffset(Tools.SCREEN_WIDTH*2/5);
		slidingMenu.setFadeDegree(0.35f);//SlidingMenu滑动时的渐变程度
		slidingMenu.setBackgroundResource(R.mipmap.left);
		slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT,true);
		//为侧滑菜单设置布局
		slidingMenu.setMenu(R.layout.activity_leftmenu);
		initFragment();
	}
	public SlidingMenu getSlidingMenu(){
		return slidingMenu;
	}
	public static boolean isMarshmallow() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}
	/**
	 * 初始化fragment, 将fragment数据填充给布局文件
	 */
	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();// 开启事务

		transaction.replace(R.id.fl_leftmenu, new LeftMenuFragment(),
				FRAGMENT_LEFT_MENU);// 用fragment替换framelayout
		transaction.replace(R.id.fl_content, new ContentFragment(),
				FRAGMENT_CONTENT);

		transaction.commit();
	}
	// 获取侧边栏fragment
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fm = getSupportFragmentManager();
		LeftMenuFragment fragment = (LeftMenuFragment) fm
				.findFragmentByTag(FRAGMENT_LEFT_MENU);

		return fragment;
	}

	// 获取主页面fragment
	public ContentFragment getContentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		ContentFragment fragment = (ContentFragment) fm
				.findFragmentByTag(FRAGMENT_CONTENT);

		return fragment;
	}
	public static boolean MusicServiceIsStart(List<RunningServiceInfo> mServiceList,
											  String serviceClass) //遍历服务项、检测本程序服务是否开启。
	{
		for (int i = 0; i < mServiceList.size(); i++)
		{
			if (serviceClass.equals(mServiceList.get(i).service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	long exitTime=0;
	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
            CustomToast.showToast(MusicHome_Activity.this, "再按一次退出应用");
			exitTime = System.currentTimeMillis();
		} else {
			CustomToast.showToast(MusicHome_Activity.this, "退出成功");
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
	}
}
