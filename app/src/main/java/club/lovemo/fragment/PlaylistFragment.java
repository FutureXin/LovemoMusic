package club.lovemo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.x;

import club.lovemo.lovemomusic.R;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.getMusic;

public class PlaylistFragment extends BaseFragment {
	MyAdapter adapter;
	private ListView listview;
	private TextView tv_showno;
	private PlaylistReceiver listreceiver;
	private int oldcurrent=-1;
	private int current;
	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_playlist, null);
		x.view().inject(this, view);
		listview=(ListView) view.findViewById(R.id.listview_playlist);
		tv_showno=(TextView) view.findViewById(R.id.tv_shownoplaylist);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		listreceiver=new PlaylistReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(AppConstants.UPDATE_PLATLIST);
		getActivity().registerReceiver(listreceiver, filter);
	}
	@Override
	public void initData() {
		LogUtils.print("TEST",oldcurrent+"oldcurrent");
		if(ContentFragment.Playlist==null){
			listview.setVisibility(View.INVISIBLE);
			tv_showno.setVisibility(View.VISIBLE);
		}else{
			listview.setVisibility(View.VISIBLE);
			tv_showno.setVisibility(View.INVISIBLE);
		}
		adapter=new MyAdapter();
		listview.setAdapter(adapter);

		super.initData();
		/**
		 * 监听listview点击时间，发送广播播放点击项
		 */
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent= new Intent(AppConstants.CTL_ACTION);
				String path=ContentFragment.Playlist.get(position).getUrl();
				intent.putExtra("path", path);
				intent.putExtra("control",4);
				intent.putExtra("current", position);
				LogUtils.print("TEST", path+"路径");
				mActivity.sendBroadcast(intent);
				oldcurrent=current;
				current=position;
				Notify();
			}
		});
	}
	public void Notify(){
		adapter.notifyDataSetChanged();
	}
	class MyAdapter extends BaseAdapter{

		private ImageView palylistimage;
		private TextView tv_palylistsongname;
		private TextView tv_palylisttime;

		@Override
		public int getCount() {
			return ContentFragment.Playlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ContentFragment.Playlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;

//			if (convertView == null) {
//				convertView
			View view= View.inflate(mActivity, R.layout.playlist_item, null);
//				holder = new ViewHolder();
//				holder.
			tv_palylistsongname = (TextView) view
					.findViewById(R.id.tv_palylist_song);
//				holder.
			tv_palylisttime = (TextView) view
					.findViewById(R.id.tv_playlist_time);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			holder.
			tv_palylistsongname.setText(ContentFragment.Playlist.get(position).getTitle());
//			holder.
			tv_palylisttime.setText(getMusic.Format(ContentFragment.Playlist.get(position).getDuration()));
			palylistimage = (ImageView) view.findViewById(R.id.imagev_playlist);
			if(current==position){
				palylistimage.setImageResource(R.mipmap.playlistimage);
				tv_palylistsongname.setTextColor(Color.GREEN);
			}
			if(current!=oldcurrent){
				if(oldcurrent==position){
					palylistimage.setImageResource(R.mipmap.imagenoplay);
				}
			}
			return view;
		}

//		class ViewHolder {
//			public TextView tv_palylistsongname;
//			public TextView tv_palylisttime;
////			public ImageView palylistimage;
//		}

	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(listreceiver);
	}
	public class PlaylistReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			oldcurrent=current;
			current=intent.getIntExtra("playlist", -1);
			if(current!=-1&&oldcurrent!=current){
				Notify();
			}
		}

	}
}

