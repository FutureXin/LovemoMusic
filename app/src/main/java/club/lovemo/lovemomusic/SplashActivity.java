package club.lovemo.lovemomusic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

import club.lovemo.permission.PermissionListener;
import club.lovemo.permission.PermissionManager;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.Tools;


public class SplashActivity extends AppCompatActivity {
	RelativeLayout rlRoot;
	private static final int REQUEST_CODE_CAMERA=1;
	private PermissionManager helper;
	private CoordinatorLayout container;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setFullScreen();
		setContentView(R.layout.activity_splash);
		container=(CoordinatorLayout)findViewById(R.id.container);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Tools.SCREEN_WIDTH = metrics.widthPixels;
		Tools.SCREEN_HEIGH = metrics.heightPixels;
		rlRoot = (RelativeLayout)findViewById(R.id.rl_root);
		startAnim();

	}
//  设置全屏
 	public void setFullScreen() {
 		// 去除信息栏
// 		Window window = this.getWindow();
// 		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
// 				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
 	}
	/**
	 * 开启动画
	 */
	private void startAnim() {

		// 动画集合
		AnimationSet set = new AnimationSet(false);

//		// 旋转动画
//		RotateAnimation rotate = new RotateAnimation(0, 360,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		rotate.setDuration(1000);// 动画时间
//		rotate.setFillAfter(true);// 保持动画状态

		// 缩放动画
//		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		scale.setDuration(2000);// 动画时间
//		scale.setFillAfter(true);// 保持动画状态

		// 渐变动画
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(2000);// 动画时间
		alpha.setFillAfter(true);// 保持动画状态

//		set.addAnimation(rotate);
//		set.addAnimation(scale);
		set.addAnimation(alpha);

		// 设置动画监听
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			// 动画执行结束
			@Override
			public void onAnimationEnd(Animation animation) {
				if (isMarshmallow()){
					Permission_to_request();
				}else {
					jumpNextPage();
				}
			}
		});

		rlRoot.startAnimation(set);
	}
	/**
	 * 跳转下一个页面
	 */
	private void jumpNextPage() {
		// 判断之前有没有显示过新手引导
		boolean userGuide =PrefUtils.getBoolean(SplashActivity.this, "is_user_guide_showed", false);
		if (!userGuide) {
			// 跳转到新手引导页
			this.startActivity(new Intent(SplashActivity.this, GuideActivity.class));
			overridePendingTransition(R.anim.click_enter, R.anim.click_exit);
		} else {
			this.startActivity(new Intent(SplashActivity.this, MusicHome_Activity.class));
			overridePendingTransition(R.anim.click_enter, R.anim.click_exit);
		}

		finish();
	}
	private void Permission_to_request(){
		helper = PermissionManager.with(SplashActivity.this)
				//添加权限请求码
				.addRequestCode(SplashActivity.REQUEST_CODE_CAMERA)
				//设置权限，可以添加多个权限
				.permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
				//设置权限监听器
				.setPermissionsListener(new PermissionListener() {

					@Override
					public void onGranted() {
						//当权限被授予时调用
						CustomToast.showToast(SplashActivity.this,"获取权限成功");
						jumpNextPage();
					}

					@Override
					public void onDenied() {
						//用户拒绝该权限时调用
//                        Utils.showToast(SplashActivity.this,"权限被拒绝");
						openSettingActivity(SplashActivity.this,getResources().getString(R.string.text_splash_open_the_necessary_permissions));
					}

					@Override
					public void onShowRationale(String[] permissions) {
						//当用户拒绝某权限时并点击`不再提醒`的按钮时，下次应用再请求该权限时，需要给出合适的响应（比如,给个展示对话框来解释应用为什么需要该权限）
						Snackbar.make(container, R.string.text_splash_need_to_access,  Snackbar.LENGTH_INDEFINITE)
								.setAction(R.string.text_ok, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										//必须调用该`setIsPositive(true)`方法
										helper.setIsPositive(true);
										helper.request();
									}
								}).show();
					}
				})
				//请求权限
				.request();
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				helper.onPermissionResult(permissions, grantResults);
				break;
		}
	}

	private void showMessageOKCancel(final SplashActivity context, String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(context)
				.setMessage(message)
				.setPositiveButton(R.string.text_ok, okListener)
				.setNegativeButton(R.string.text_close, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						SplashActivity.this.finish();
					}
				}).create().show();

	}
	private void openSettingActivity(final SplashActivity activity, String message) {

		showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
				intent.setData(uri);
				activity.startActivity(intent);
				SplashActivity.this.finish();
			}
		});
	}
	private boolean isMarshmallow() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}
}
