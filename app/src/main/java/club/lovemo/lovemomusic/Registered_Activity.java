package club.lovemo.lovemomusic;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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


public class Registered_Activity extends Activity implements OnClickListener {
    private RelativeLayout regis_relative;
    private EditText et_username, et_psw, et_pswtow, et_encrypted, et_answer;
    private Button btn_reg, btn_reempty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        init();
    }

    private void init() {
        regis_relative = (RelativeLayout) findViewById(R.id.registeredlayout);
        String bg = PrefUtils.getString(this, "topic", "bg1");
        SetBackgroundImage.setBackGround(regis_relative, bg);
        et_answer = (EditText) findViewById(R.id.et_answer);
        et_encrypted = (EditText) findViewById(R.id.et_reencrypted);
        et_psw = (EditText) findViewById(R.id.et_repsw);
        et_pswtow = (EditText) findViewById(R.id.et_repswtow);
        et_username = (EditText) findViewById(R.id.et_rename);
        btn_reempty = (Button) findViewById(R.id.btn_reempty);
        btn_reg = (Button) findViewById(R.id.btn_rereg);
        btn_reempty.setOnClickListener(this);
        btn_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_rereg) {
            String username = et_username.getText().toString().trim();
            String userpsw = et_psw.getText().toString().trim();
            String pswtow = et_pswtow.getText().toString().trim();
            String encrypted = et_encrypted.getText().toString().trim();
            String answer = et_answer.getText().toString().trim();
            if (!TextUtils.isEmpty(username)) {
                if (!TextUtils.isEmpty(userpsw)) {
                    if (!TextUtils.isEmpty(pswtow)) {
                        if (!TextUtils.isEmpty(encrypted)) {
                            if (!TextUtils.isEmpty(answer)) {
                                if (pswtow.equals(userpsw)) {
                                    if (Tools.isNetworkAvailable(Registered_Activity.this)) {
                                        try {
                                            getData(AppConstants.REGISTEREDURL
                                                    + "?name="
                                                    + MD5Encoder.encode(username)
                                                    + "&password="
                                                    + MD5Encoder.encode(pswtow)
                                                    + "&encrypted=" + encrypted
                                                    + "&answer="
                                                    + MD5Encoder.encode(answer));
                                            LogUtils.print("TEST", encrypted + "encrypted");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        CustomToast.showToast(this, "网络不可用！！！");
                                    }

                                } else {
                                    CustomToast.showToast(this, "两次输入的密码不一致！！！");
                                }
                            } else {
                                CustomToast.showToast(this, "密保答案不能为空！！！");
                            }
                        } else {
                            CustomToast.showToast(this, "密保不能为空！！！");
                        }
                    } else {
                        CustomToast.showToast(this, "请输入确认密码再试！！！");
                    }
                } else {
                    CustomToast.showToast(this, "请输入密码再试！！！");
                }
            } else {
                CustomToast.showToast(this, "请输入账号再试！！！");
            }
        } else if (v.getId() == R.id.btn_reempty) {

        }
    }

    public void getData(String myurl) {

        RequestParams params = new RequestParams(myurl);
//        params.addBodyParameter("username", "abc");
//        params.addParameter("password", "123");
        params.addHeader("head", "android"); //为当前请求添加一个头
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //解析result
//				String result = (String) responseInfo.result;
                LogUtils.print("TEST", "返回结果:" + result);
                if (result.contains("success")) {
                    CustomToast.showToast(Registered_Activity.this, "注册成功！！！");
                } else if (result.contains("change name")) {
                    CustomToast.showToast(Registered_Activity.this,
                            "注册失败,该用户名已经被注册了,请更换用户名再试！！！");
                } else if (result.contains("failure")) {
                    CustomToast.showToast(Registered_Activity.this, "注册失败！！！");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CustomToast.showToast(Registered_Activity.this, "访问失败...");
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
