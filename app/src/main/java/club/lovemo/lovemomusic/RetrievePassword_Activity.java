package club.lovemo.lovemomusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.MD5Encoder;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;
import club.lovemo.utils.Tools;

public class RetrievePassword_Activity extends Activity {
	private RelativeLayout retrieve_layout;
	private Button btn_ok;
	private EditText et_answer,et_newpsw;
	private TextView tv_showproblem;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retrievepassword);
		init();
		Intent intent=getIntent();
		String problem=intent.getStringExtra("problem");
		username=intent.getStringExtra("username");
		tv_showproblem.setText(problem);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String answer=et_answer.getText().toString().trim();
				String newpsw=et_newpsw.getText().toString().trim();
				if(!TextUtils.isEmpty(answer)&&!TextUtils.isEmpty(newpsw)){
					if(Tools.isNetworkAvailable(RetrievePassword_Activity.this)){
						try {
							String Myurl=AppConstants.RESETPASSWORDURL+"?name="+ MD5Encoder.encode(username)+"&password="+MD5Encoder.encode(newpsw)+"&answer="+MD5Encoder.encode(answer);
							getData(Myurl);
						}catch (Exception e){
						}
					}else{
						CustomToast.showToast(RetrievePassword_Activity.this, "网络不可用！！！");
					}
				}else{
					CustomToast.showToast(RetrievePassword_Activity.this,"请正确输入答案和密码！！！");
				}
			}
		});
	}
	private void init() {
		retrieve_layout=(RelativeLayout) findViewById(R.id.retrievlayout);
		String bg = PrefUtils.getString(this, "topic", "bg1");
		SetBackgroundImage.setBackGround(retrieve_layout, bg);
		btn_ok=(Button) findViewById(R.id.btn_ok);
		et_answer=(EditText) findViewById(R.id.et_retranswer);
		et_newpsw=(EditText) findViewById(R.id.et_newpsw);
		tv_showproblem=(TextView) findViewById(R.id.tv_showproblem);
	}
	public void getData(String myurl) {
		RequestParams params = new RequestParams(myurl);
//		params.addBodyParameter("username","abc");
//		params.addParameter("password","123");
//		params.addHeader("head","android"); //为当前请求添加一个头
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//解析result
				LogUtils.print("TEST", "返回结果:" + result);
				if (result.contains("success")) {
					CustomToast.showToast(RetrievePassword_Activity.this, "密码重置成功！！！");
				} else if (result.contains("answer error")) {
					CustomToast.showToast(RetrievePassword_Activity.this,
							"密保答案错误！！！");
				} else if (result.contains("reset failure")) {
					CustomToast.showToast(RetrievePassword_Activity.this, "密码重置失败！！！");
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				CustomToast.showToast(RetrievePassword_Activity.this, "访问失败...");
			}
			@Override
			public void onCancelled(Callback.CancelledException cex) {
			}
			@Override
			public void onFinished() {
			}
		});
	}
}
