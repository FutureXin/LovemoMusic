package club.lovemo.lovemomusic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import club.lovemo.entity.InJsonData;
import club.lovemo.entity.Search_Data;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;
import club.lovemo.utils.Tools;
import club.lovemo.utils.bitmap.MyBitmapUtils;

public class Search_Activity extends Activity {
    private ListView listview;
    InJsonData inJsonData;
    private MyAdapter adapter;
    private ImageView imagezhen;
    private ImageButton imagebtn_search;
    private TextView tv_shownodata;
    private String url;
    private String keyword;
    private EditText et_keyword;
    private Search_Data searchdate;
    private List<Search_Data.contentlist> contentlist;
    AnimationDrawable mLoadingAinm;
    private MyBitmapUtils mymap;
    private RelativeLayout search_relative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        search_relative = (RelativeLayout) findViewById(R.id.search_relative);
        String bg = PrefUtils.getString(this, "topic", "bg1");
        SetBackgroundImage.setBackGround(search_relative, bg);
        et_keyword = (EditText) findViewById(R.id.et_search);
        imagebtn_search = (ImageButton) findViewById(R.id.imagebtnsearch);
        listview = (ListView) findViewById(R.id.listview_search);
        tv_shownodata = (TextView) findViewById(R.id.tv_shownodata);
        imagezhen = (ImageView) findViewById(R.id.loding);
        imagezhen.setBackgroundResource(R.drawable.frameofanimation);
        mLoadingAinm = (AnimationDrawable) imagezhen.getBackground();
        mymap = new MyBitmapUtils();
        imagezhen.post(new Runnable() {
            public void run() {
                mLoadingAinm.start();
            }
        });
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        if (keyword != null) {
            // LogUtils.print("TEST",keyword+"keyword");
            imagezhen.setVisibility(View.VISIBLE);
            et_keyword.setText(keyword);
            et_keyword.setSelection(keyword.length());
            showsonglist(keyword);
        }

        imagebtn_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = et_keyword.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    CustomToast.showToast(Search_Activity.this, "请输入内容后在进行搜索！！！");
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    showsonglist(text);
                    imagezhen.setVisibility(View.VISIBLE);
                }
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (Tools.isNetworkAvailable(Search_Activity.this)) {
                    CustomToast.showToast(Search_Activity.this, "网络可用！！！");
                    //关闭软键盘
                    listview.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//				  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//				  imm.hideSoftInputFromWindow(et_keyword.getWindowToken(), 0);
                    String url = contentlist.get(position).getM4a();
                    if (url.length() == 0) {
                        CustomToast.showToast(Search_Activity.this, "播放地址缺失！");
                    } else {
                        Intent sendIntent = new Intent(AppConstants.UPDATE_ACTION);
                        Intent intent = new Intent(AppConstants.CTL_ACTION);
                        intent.putExtra("path", url);
                        intent.putExtra("control", 6);
                        sendIntent.putExtra("update", 0x002);
                        String songname = contentlist.get(position).getSongname();
                        if (songname.length() == 0) {
                            songname = "专辑：" + contentlist.get(position).getAlbumname();
                        }
                        String aname = contentlist.get(position).getSingername();
                        if (aname.length() == 0) {
                            aname = "未知艺术家";
                        }
                        LogUtils.print("TEST", url + "url");
                        sendIntent.putExtra("urlsongname", songname);
                        intent.putExtra("urlsongname", songname);
                        sendIntent.putExtra("aname", aname);
                        intent.putExtra("aname", aname);
                        sendBroadcast(intent);
                        sendBroadcast(sendIntent);
                    }
                } else {
                    CustomToast.showToast(Search_Activity.this, "网络不可用！！！");
                }
            }
        });
        //设置点击除输入框外地方软键盘收起
        search_relative.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				CustomToast.showToast(Search_Activity.this, "收起软键盘", Toast.LENGTH_SHORT);
//				 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//				 imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public void showsonglist(String keyword) {
        url = AppConstants.SEARCHURL1 + keyword + AppConstants.SEARCHURL2
                + getCurrentTime() + AppConstants.SEARCHURL3;
        getDataFromServer();
    }

    /**
     * 获取当前时间
     */
    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//		return format.format(new Date());
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
    }

    /**
     * 从服务器获取数据
     */
    private void getDataFromServer() {
        RequestParams params = new RequestParams(url);
//		params.addBodyParameter("username","abc");
//		params.addParameter("password","123");
//		params.addHeader("head","android"); //为当前请求添加一个头
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //解析result
                LogUtils.print("TEST", "返回结果:" + result);
                parseData(result);
                imagezhen.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CustomToast.showToast(Search_Activity.this, "搜索失败！！！");
                imagezhen.setVisibility(View.INVISIBLE);
                // TODO 在没有网时显示无网络textview
                listview.setVisibility(View.INVISIBLE);
                tv_shownodata.setText("加载失败，请检查网络!!!");
                tv_shownodata.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

    }

    /**
     * 解析网络数据
     *
     * @param result
     */
    protected void parseData(String result) {
        Gson gson = new Gson();
        searchdate = gson.fromJson(result, Search_Data.class);
        contentlist = searchdate.getShowapi_res_body().getPagebean()
                .getContentlist();
        if (contentlist != null) {
            adapter = new MyAdapter();
            listview.setAdapter(adapter);
            listview.setVisibility(View.VISIBLE);
            tv_shownodata.setVisibility(View.INVISIBLE);
        } else {
            tv_shownodata.setVisibility(View.VISIBLE);
            listview.setVisibility(View.INVISIBLE);
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contentlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return contentlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(Search_Activity.this,
                        R.layout.search_item, null);
                holder = new ViewHolder();
                holder.searchname = (TextView) convertView
                        .findViewById(R.id.search_tv_name);
                holder.searchsong = (TextView) convertView
                        .findViewById(R.id.search_tv_song);
                holder.searchimage = (ImageView) convertView
                        .findViewById(R.id.search_listimage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = contentlist.get(position).getSingername();
            if (name == null) {
                holder.searchname.setText("未知艺术家");
            } else {
                holder.searchname.setText(name);
            }
            String songname = contentlist.get(position).getSongname();
            if (songname == null) {
                holder.searchsong.setText("专辑：" + contentlist.get(position).getAlbumname());
            } else {
                holder.searchsong.setText(songname);
            }
            String imageurl = contentlist.get(position).getAlbumpic_small();
            if (imageurl == null) {
                holder.searchimage
                        .setImageResource(R.mipmap.playlistbangdanimage);
            } else {
                mymap.display(holder.searchimage, imageurl);
            }
            return convertView;
        }

        class ViewHolder {
            TextView searchname;
            TextView searchsong;
            ImageView searchimage;
        }
    }
}
