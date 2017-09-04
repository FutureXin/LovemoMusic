package club.lovemo.lovemomusic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CacheUtils;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;
import club.lovemo.utils.Tools;
import club.lovemo.utils.bitmap.MyBitmapUtils;

public class Searchlist_Activity extends Activity {
    private ListView listview;
    MyBitmapUtils mymap;
    InJsonData inJsonData;
    private MyAdapter adapter;
    private int topid;
    private TextView tv_shownodata, tv_showbangdanname;
    private ImageView imagezhen;
    private String url;
    private String bangdanname;
    private List<InJsonData.songlist> Jsondata;
    AnimationDrawable mLoadingAinm;
    private RelativeLayout searchlist_relative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist_activity);
        searchlist_relative = (RelativeLayout) findViewById(R.id.searchlist_relative);
        String bg = PrefUtils.getString(this, "topic", "bg1");
        SetBackgroundImage.setBackGround(searchlist_relative, bg);
        listview = (ListView) findViewById(R.id.listview_search);
        tv_shownodata = (TextView) findViewById(R.id.tv_shownodata);
        imagezhen = (ImageView) findViewById(R.id.loding);
        tv_showbangdanname = (TextView) findViewById(R.id.tv_showleiming);
        imagezhen.setBackgroundResource(R.drawable.frameofanimation);
        mLoadingAinm = (AnimationDrawable) imagezhen.getBackground();
        imagezhen.post(new Runnable() {
            public void run() {
                mLoadingAinm.start();
            }
        });
        mymap = new MyBitmapUtils();
        Intent intent = getIntent();
        bangdanname = intent.getStringExtra("bangdanname");
        topid = intent.getIntExtra("topid", 3);
        if (bangdanname != null) {
            imagezhen.setVisibility(View.VISIBLE);
            tv_showbangdanname.setText(bangdanname);
            showsonglist();
        }
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                LogUtils.print("TEST", "arg2" + arg2);
                String path = Jsondata.get(arg2).getUrl();
                if (Tools.isNetworkAvailable(Searchlist_Activity.this)) {
                    CustomToast.showToast(Searchlist_Activity.this, "网络可用！！！");
                    if (path.length() == 0) {
                        CustomToast.showToast(Searchlist_Activity.this, "播放地址缺失！");
                    } else {
                        Intent intent = new Intent(AppConstants.CTL_ACTION);
                        intent.putExtra("path", path);
                        intent.putExtra("control", 6);
                        LogUtils.print("TEST", path + "路径");
                        Intent sendIntent = new Intent(AppConstants.UPDATE_ACTION);
                        String songname = Jsondata.get(arg2).getSongname();
                        if (songname.length() == 0) {
                            songname = Jsondata.get(arg2).getAlbummid();
                        }
                        String aname = Jsondata.get(arg2).getSingername();
                        if (aname.length() == 0) {
                            aname = "未知艺术家";
                        }
                        intent.putExtra("urlsongname", songname);
                        sendIntent.putExtra("urlsongname", songname);
                        intent.putExtra("aname", aname);
                        sendIntent.putExtra("aname", aname);
                        Searchlist_Activity.this.sendBroadcast(intent);
                        sendBroadcast(sendIntent);
                    }
                }else{
                    CustomToast.showToast(Searchlist_Activity.this, "网络不可用！！！");
                }
            }
        });
    }

    public void showsonglist() {
        url = AppConstants.SONGURL1 + getCurrentTime() + AppConstants.SONGURL2
                + topid + AppConstants.SONGURL3;
        getDataFromServer();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
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
                // 设置缓存
                CacheUtils.setCache(Searchlist_Activity.this,
                        AppConstants.SONGJSONLISTKEY + topid, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CustomToast.showToast(Searchlist_Activity.this, "msg");
                String result = CacheUtils.getCache(Searchlist_Activity.this,
                        AppConstants.SONGJSONLISTKEY + topid);
                imagezhen.setVisibility(View.INVISIBLE);
                if (result != null) {
                    parseData(result);
                } else {
                    // TODO 在没有网时显示无网络textview
                    tv_shownodata.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
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
        inJsonData = gson.fromJson(result, InJsonData.class);
        LogUtils.print("TEST", " Gson" + inJsonData.getShoapi_res_code());
        Jsondata = inJsonData.getShowapi_res_body().getPagebean().getSonglist();
        // bangdanAdapter =new BangdanAdapter();
        if (Jsondata != null) {
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
            return Jsondata.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return Jsondata.get(position);
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
                convertView = View.inflate(Searchlist_Activity.this,
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
            String name = Jsondata.get(position).getSingername();
            if (name == null) {
                holder.searchname.setText("未知艺术家");
            } else {
                holder.searchname.setText(name);
            }
            String songname = Jsondata.get(position).getSongname();
            if (songname != null) {
                holder.searchsong.setText(songname);
            }
            String imageurl = Jsondata.get(position).getAlbumpic_small();
            if (imageurl == null) {
                holder.searchimage
                        .setImageResource(R.mipmap.playlistbangdanimage);
            } else {
                mymap.display(holder.searchimage, imageurl);
            }
            return convertView;
        }

        class ViewHolder {
            public TextView searchname;
            public TextView searchsong;
            public ImageView searchimage;
        }
    }
}
