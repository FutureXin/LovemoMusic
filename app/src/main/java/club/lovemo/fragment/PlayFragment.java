package club.lovemo.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import club.lovemo.lovemomusic.R;
import club.lovemo.servise.MusicService;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.Krctolrc;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.Tools;
import club.lovemo.view.LyricView;

public class PlayFragment extends BaseFragment {
	public MyPlayReceiver myPlayReceiver;
	int musiclong = 0;
	TextView lyricabove;
	TextView player_textview_default;
	ArrayList<String[]> lyric = new ArrayList<String[]>();
	TextView lyricbelow;
	LyricView lyricView;
	RelativeLayout rl_lyric_1;
	RelativeLayout rl_lyric_2;
	ImageView iv_lyric_1;
	ImageView iv_lyric_2;

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_play, null);
		x.view().inject(this, view);
		player_textview_default = (TextView) view
				.findViewById(R.id.player_textview_default);
		lyricabove = (TextView) view.findViewById(R.id.player_textview_above);
		lyricbelow = (TextView) view.findViewById(R.id.player_textview_below);
		lyricView = (LyricView) view.findViewById(R.id.lyricview);
		// 获取歌词界面引用
		rl_lyric_1 = (RelativeLayout) view
				.findViewById(R.id.player_relativelayout_change_1);
		rl_lyric_2 = (RelativeLayout) view
				.findViewById(R.id.player_relativelayout_change_2);
		iv_lyric_1 = (ImageView) view
				.findViewById(R.id.player_imageview_change_1);
		iv_lyric_2 = (ImageView) view
				.findViewById(R.id.player_imageview_change_2);

		return view;
	}

	@Override
	public void initData() {
		super.initData();
		int playmodel = PrefUtils.getInt(mActivity, "playmodel", 0);
		if (playmodel == 0) {
			rl_lyric_1.setVisibility(View.INVISIBLE);
			rl_lyric_2.setVisibility(View.VISIBLE);
		} else {
			rl_lyric_1.setVisibility(View.VISIBLE);
			rl_lyric_2.setVisibility(View.INVISIBLE);
		}
		// 切换歌词显示界面
		iv_lyric_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_lyric_1.setVisibility(View.INVISIBLE);
				rl_lyric_2.setVisibility(View.VISIBLE);
				PrefUtils.setInt(mActivity, "playmodel", 0);
			}
		});
		iv_lyric_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_lyric_1.setVisibility(View.VISIBLE);
				rl_lyric_2.setVisibility(View.INVISIBLE);
				PrefUtils.setInt(mActivity, "playmodel", 1);
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myPlayReceiver = new MyPlayReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.UPDATEPLARUI_ACTION);
		getActivity().registerReceiver(myPlayReceiver, filter);
		LogUtils.print("TEST", "myPlayReceiverstartservice执行了");
	}

	public class MyPlayReceiver extends BroadcastReceiver {
		int current = -1;
		int upcurrent;

		@SuppressLint("SdCardPath")
		@Override
		public void onReceive(Context context, Intent intent) {
			int update = intent.getIntExtra("update", -1);
			musiclong = intent.getIntExtra("musiclong", 0);
			upcurrent = current;
			current = intent.getIntExtra("current", 0);
			int musiccurrent = intent.getIntExtra("musiccurrentDuration", 0);
			// LogUtils.print("TEST",
			// "musiclong"+musiclong+"current"+current+"update"+update+"musiccurrent"+musiccurrent);
			// 如果播放id和记录的id不符时初始化歌词。目的减少计算量
			if (current != upcurrent) {
				player_textview_default.setVisibility(View.INVISIBLE);
				LogUtils.print("TEST", current+"fffff");
				String musicname = ContentFragment.Playlist.get(current)
						.getTitle();
				lyric = null;
				LogUtils.print("TEST", musicname + "musicname");
				String [] pathstr=ContentFragment.Playlist.get(current).getUrl().split("/");
				LogUtils.print("TEST", pathstr[pathstr.length-1]+"musicnamepath");
				String pathmusicname;
				if(pathstr[pathstr.length-1].indexOf(".")==-1&&!pathstr[pathstr.length-1].equals("无")){
					pathmusicname=pathstr[pathstr.length-1].split(".")[0];
					LogUtils.print("TEST", pathmusicname+"musicnamepath-lrc");
				}else{
					pathmusicname=musicname;
				}
				File file = new File(getSDPath()+"/lovemomusic_lyrics");
				if (!file.exists()) {// 如果文件夹不存在, 创建文件夹
					file.mkdirs();
				}
				if(!Tools.fileIsExists(getSDPath()+"/lovemomusic_lyrics/" + pathmusicname+ ".lrc")){
					Krctolrc kr=new Krctolrc(pathmusicname);
					kr.Krcto();
				}
				lyric = Tools.getLyric(getSDPath()+"/lovemomusic_lyrics/" + pathmusicname
						+ ".lrc");// 读取歌词数组
				if (MusicService.dowplay) {
					lyric = null;
				}
				if (lyric == null) {
					LogUtils.print("TEST", "lyric为空");
				} else {
					LogUtils.print("TEST", "lyric不为空" + lyric.size());
					LogUtils.print("TEST", "lyric不为空" + lyric.get(3)[2]);
				}
				if (ContentFragment.Playlist.get(current).getDisplayName() == null
						&& musicname.indexOf("-") != -1) {
					lyricabove.setText(musicname.split("-", 2)[1]);
					lyricbelow.setText(musicname.split("-", 2)[0]);
				} else {
					lyricabove.setText(musicname);
					if (ContentFragment.Playlist.get(current).getDisplayName() == null) {
						lyricbelow.setText("未知艺术家");
					} else {
						lyricbelow.setText(ContentFragment.Playlist
								.get(current).getDisplayName());
					}
				}
				lyricView.setLyric(getSDPath()+"/lovemomusic_lyrics/" + pathmusicname
						+ ".lrc");

			}
			lyricView.setMusictime(musiccurrent, musiclong);
			// 获取歌词控件。设置默认
			if (lyric == null) {
				lyricabove.setText("当前歌曲没有歌词");
				lyricbelow.setText("");
			} else {
				if (lyric.size() == 1) {
					String musicname = ContentFragment.Playlist.get(current)
							.getTitle();
					if (ContentFragment.Playlist.get(current).getDisplayName() == null
							&& musicname.indexOf("-") != -1) {
						lyricabove.setText(musicname.split("-", 2)[1]);
						lyricbelow.setText(musicname.split("-", 2)[0]);
					} else {
						lyricabove.setText(musicname);
						if (ContentFragment.Playlist.get(current)
								.getDisplayName() == null) {
							lyricbelow.setText("未知艺术家");
						} else {
							lyricbelow.setText(ContentFragment.Playlist.get(
									current).getDisplayName());
						}
					}
				}
				for (int i = 0; i < lyric.size(); i++) {
					// 计算当前时间歌词显示的内容以及下一句显示的内容
					String lyric1[] = lyric.get(i);
					int time1 = (Integer.parseInt(lyric1[0]) * 60 + Integer
							.parseInt(lyric1[1])) * 1000;
					String lyric2[] = { "", "", "" };// 当歌词播放到最后一句是。下一句为空
					int time2;

					if (i != lyric.size() - 1) {
						lyric2 = lyric.get(i + 1);
						time2 = (Integer.parseInt(lyric2[0]) * 60 + Integer
								.parseInt(lyric2[1])) * 1000;
					} else {
						time2 = musiclong;
					}

					int time = musiccurrent;
					if (time > time1 && time < time2) // 当本首歌的进度位于当前句和下一句之间时的显示
					{
						// 判断
						if (i % 2 == 0)// 控制歌词双行显示的上行只显示奇数句、下行只显示偶数句。
						{
							lyricabove.setText(lyric1[2]);
							lyricabove.setTextColor(Color.GREEN);
							lyricbelow.setText(lyric2[2]);
							lyricbelow.setTextColor(Color.WHITE);
						} else {
							lyricbelow.setText(lyric1[2]);
							lyricbelow.setTextColor(Color.GREEN);
							lyricabove.setText(lyric2[2]);
							lyricabove.setTextColor(Color.WHITE);
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent stopIntent = new Intent(getActivity(), MusicService.class);
		// stopIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		getActivity().stopService(stopIntent);
		getActivity().unregisterReceiver(myPlayReceiver);
		LogUtils.print("TEST", "myPlayReceiverdestroy");
	}
	public static String getSDPath(){
		File sdDir = null;
		File sdDir1 = null;
		File sdDir2 = null;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取根目录
			sdDir1 = Environment.getDataDirectory();
			sdDir2 =Environment.getRootDirectory();
		}
		System.out.println("getExternalStorageDirectory(): "+sdDir.toString());
		System.out.println("getExternalStorageDirectory():abso "+sdDir.getAbsolutePath().toString());
//			System.out.println("getDataDirectory(): "+sdDir1.toString());
//			System.out.println("getRootDirectory(): "+sdDir2.toString());

		if(sdDir.toString()!=null){
			return sdDir.getAbsolutePath().toString();
		}
		return null;
	}
}
