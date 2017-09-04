package club.lovemo.lovemomusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.MD5Encoder;
import club.lovemo.utils.MyHttpUtils;
import club.lovemo.utils.MyHttpUtils.OnDownLoadListener;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;
import club.lovemo.utils.Tools;

public class Login_Activity extends AppCompatActivity implements OnClickListener {
	private RelativeLayout login_relative;
	private Button btn_psw, btn_login, btn_reg;
	private EditText et_name, et_pas;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		login_relative = (RelativeLayout) findViewById(R.id.loginlayout);
		String bg = PrefUtils.getString(this, "topic", "bg1");
		SetBackgroundImage.setBackGround(login_relative, bg);
		et_name = (EditText) findViewById(R.id.et_username);
		et_pas = (EditText) findViewById(R.id.et_userpsw);
		btn_psw = (Button) findViewById(R.id.btn_forgotpsw);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_reg = (Button) findViewById(R.id.btn_reg);
		btn_login.setOnClickListener(this);
		btn_reg.setOnClickListener(this);
		btn_psw.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_login:
				String name = et_name.getText().toString().trim();
				String psw = et_pas.getText().toString().trim();
				if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(psw)) {
					try {
						if (Tools.isNetworkAvailable(Login_Activity.this)) {
							getData(AppConstants.LOGINURL + "?name=" + MD5Encoder.encode(name) + "&password="
									+ MD5Encoder.encode(psw));
							CustomToast.showToast(Login_Activity.this,"点击了登录按钮，且网络可用！");
						} else {
							CustomToast.showToast(this, "网络不可用！！！");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
//					LogUtils.print("TEST", "登录返回结果:" + AppConstants.LOGINURL
//							+ "?name=" + name + "&password=" + psw);
					// getMydata();
				} else {
					CustomToast.showToast(this, "请输入用户名或密码！！！");
				}
				break;
			case R.id.btn_reg:
				Intent intent = new Intent(Login_Activity.this,
						Registered_Activity.class);
				this.startActivity(intent);
				break;
			case R.id.btn_forgotpsw:
				String username = et_name.getText().toString().trim();
				if (!TextUtils.isEmpty(username)) {
					try {
						if (Tools.isNetworkAvailable(Login_Activity.this)) {
							getData(AppConstants.GETENCRYPTEDURL + "?name="
									+ MD5Encoder.encode(username));
						} else {
							CustomToast.showToast(this, "网络不可用！！！");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					CustomToast.showToast(this, "请输入要找回密码的用户名！！！");
				}
				break;

			default:
				break;
		}
	}

	public void getData(String myurl) {
		RequestParams params = new RequestParams(myurl);
//		params.addBodyParameter("username","abc");
//		params.addParameter("password","123");
		params.addHeader("head","android"); //为当前请求添加一个头
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//解析result
				LogUtils.print("TEST", "返回结果:" + result);
				if (result.contains("success")) {
					CustomToast.showToast(Login_Activity.this, "登录成功！！！");
					Intent intent=new Intent(AppConstants.UPDATE_LOGIN);
					intent.putExtra("username",et_name.getText().toString().trim());
					sendBroadcast(intent);
					Intent intent2=new Intent(Login_Activity.this,LoginSuccess_Activity.class);
					intent2.putExtra("username", et_name.getText().toString().trim());
					startActivity(intent2);
					overridePendingTransition(R.anim.click_enter, R.anim.click_exit);
					Login_Activity.this.finish();
					overridePendingTransition(R.anim.click_enter, R.anim.click_exit);
				} else if (result.contains("failure")) {
					CustomToast.showToast(Login_Activity.this, "用户名或密码错误！！！");
				} else if (result.contains("Encryptedfailure")) {
					CustomToast.showToast(Login_Activity.this, "这个账号还未注册！！！");
				} else {
					LogUtils.print("TEST", "密保问题" + result);
					Intent intent=new Intent(Login_Activity.this,RetrievePassword_Activity.class);
					intent.putExtra("problem", result);
					intent.putExtra("username", et_name.getText().toString().trim());
					startActivity(intent);
					overridePendingTransition(R.anim.click_enter, R.anim.click_exit);
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				CustomToast.showToast(Login_Activity.this, "访问失败...");
			}
			@Override
			public void onCancelled(Callback.CancelledException cex) {
			}
			@Override
			public void onFinished() {
			}
		});
	}

	public void getMydata() {
		MyHttpUtils utils = new MyHttpUtils();
		utils.setOnDownLoadListener(new OnDownLoadListener() {

			@Override
			public void onDownLoad(String result) {
				LogUtils.print("TEST", "登录返回结果:" + result);
			}
		});
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "AAA");
		params.put("password", "111");
		utils.requestPost(AppConstants.LOGINURL, params);
	}
}
