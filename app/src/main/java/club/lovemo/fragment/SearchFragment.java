package club.lovemo.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import club.lovemo.entity.InJsonData;
import club.lovemo.entity.LrcsData;
import club.lovemo.lovemomusic.R;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.LrcUtils;
import club.lovemo.utils.bitmap.MyBitmapUtils;

@SuppressLint("SimpleDateFormat") public class SearchFragment extends BaseFragment {
	MyBitmapUtils mymap;
	InJsonData inJsonData;
	BangdanAdapter bangdanAdapter;
	private ListView listview;
	private MyAdapter adapter;
	private int state=0;
	private int topid;
	private TextView tv_shownodata;
	private ImageView imagezhen;
	private ImageButton imageSearch;
	private EditText et_sousuo;
	private List<InJsonData.songlist> Jsondata;
	private String url;
	//3=欧美 5=内地 6=港台 16=韩国 17=日本 18=民谣 19=摇滚 23=销量 26=热歌
	private String [] playlist=new String[]{"欧美","内地","港台","韩国","日本","民谣","摇滚","销量","热歌"};
	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_search, null);
		x.view().inject(this,view);
		listview=(ListView) view.findViewById(R.id.listview_search);
		et_sousuo=(EditText) view.findViewById(R.id.et_sousuo);
		tv_shownodata=(TextView) view.findViewById(R.id.tv_shownodata);
		imagezhen=(ImageView) view.findViewById(R.id.zhendonghua);
		imageSearch=(ImageButton) view.findViewById(R.id.imagefragment_search);
		return view;
	}
	@Override
	public void initData() {
		super.initData();
		mymap=new MyBitmapUtils();
		showbangdanlist();
//		showsonglist();
		imageSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String text=et_sousuo.getText().toString().trim();
				if(TextUtils.isEmpty(text)){
					CustomToast.showToast(mActivity, "请输入内容后在进行搜索！！！");
				}else{
					InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					Search_Activity(text);
				}
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				if(state==0){
					//关闭软键盘
//					InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					listview.requestFocus();
					switch (position) {
						case 0:
							topid=3;
							break;
						case 1:
							topid=5;
							break;
						case 2:
							topid=6;
							break;
						case 3:
							topid=16;
							break;
						case 4:
							topid=17;
							break;
						case 5:
							topid=18;
							break;
						case 6:
							topid=19;
							break;
						case 7:
							topid=23;
							break;
						case 8:
							topid=26;
							break;
						default:
							break;
					}
					SearchList_Activity(position, topid);
//					showsonglist();
				}else{
					CustomToast.showToast(mActivity, "你点击了："+position);
				}
			}

		});
	}
	public void Search_Activity(String text){
		Intent intent=new Intent(getActivity(),club.lovemo.lovemomusic.Search_Activity.class);
		intent.putExtra("keyword", text);
		mActivity.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.pop_menu, R.anim.pop_menu_main);
	}
	public void SearchList_Activity(int position,int topid){
		Intent intent=new Intent(getActivity(),club.lovemo.lovemomusic.Searchlist_Activity.class);
		intent.putExtra("bangdanname", playlist[position]);
		intent.putExtra("topid", topid);
		mActivity.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.none, R.anim.none);
	}
	public void showsonglist(){
		url=AppConstants.SEARCHLYRICS1+"4833285"+AppConstants.SEARCHLYRICS2+getCurrentTime()+AppConstants.SEARCHLYRICS3;
		getDataFromServer();
		state=1;
	}
	public void showbangdanlist(){
		bangdanAdapter=new BangdanAdapter();
		listview.setAdapter(bangdanAdapter);
		listview.setVisibility(View.VISIBLE);
		tv_shownodata.setVisibility(View.INVISIBLE);
		state=0;
	}
	/**
	 * 获取当前时间
	 */
	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(new Date());
	}
	/**
	 * 从服务器获取数据
	 */
	private void getDataFromServer() {
		RequestParams params = new RequestParams(url);
		imagezhen.setVisibility(View.VISIBLE);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//解析result
				LogUtils.print("TEST", "返回结果:" + result);
				parseData(result);
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				CustomToast.showToast(mActivity, "msg");
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
		LrcsData data = gson.fromJson(result, LrcsData.class);
		String lrc=data.getShowapi_res_body().getLyric();
		try {
			LrcUtils.convert("海阔天空", lrc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class MyAdapter  extends BaseAdapter{

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
				convertView = View.inflate(mActivity, R.layout.search_item,
						null);
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
			String name=Jsondata.get(position).getSingername();
			if(name==null){
				holder.searchname.setText("未知艺术家");
			}else{
				holder.searchname.setText(name);
			}
			holder.searchsong.setText(Jsondata.get(position).getSongname());
			String imageurl=Jsondata.get(position).getAlbumpic_small();
			if(imageurl==null){
				holder.searchimage.setImageResource(R.mipmap.playlistbangdanimage);
			}else{
				mymap.display(holder.searchimage,imageurl );
			}
			return convertView;
		}

		class ViewHolder {
			TextView searchname;
			TextView searchsong;
			ImageView searchimage;
		}
	}
	class BangdanAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return playlist.length;
		}

		@Override
		public Object getItem(int position) {
			return playlist[position];
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
				convertView = View.inflate(mActivity, R.layout.left_menu_item,
						null);
				holder = new ViewHolder();
				holder.tv_palylistname = (TextView) convertView
						.findViewById(R.id.left_menulistview_tvname);
				holder.palylistimage = (ImageView) convertView
						.findViewById(R.id.left_menulistview_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_palylistname.setText(playlist[position]);
			holder.palylistimage.setImageResource(R.mipmap.playlistbangdanimage);
			return convertView;
		}

		class ViewHolder {
			TextView tv_palylistname;
			ImageView palylistimage;
		}
	}
}
