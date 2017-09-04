package club.lovemo.lovemomusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;

public class LoginSuccess_Activity extends Activity {
	private RelativeLayout mRelativeLayout;
	private TextView tv_showusername;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginsuccessful);
		mRelativeLayout=(RelativeLayout) findViewById(R.id.loginsuccesslayout);
		String bg = PrefUtils.getString(this, "topic", "bg1");
		SetBackgroundImage.setBackGround(mRelativeLayout, bg);
		tv_showusername=(TextView) findViewById(R.id.tv_showusername);
		Intent intent=getIntent();
		String username=intent.getStringExtra("username");
		if(username!=null){
			tv_showusername.setText("欢迎"+username+"来到爱陌音乐");
		}
	}
}
