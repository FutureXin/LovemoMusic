package club.lovemo.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import club.lovemo.entity.Mp3Info;
import club.lovemo.lovemomusic.MusicHome_Activity;
import club.lovemo.lovemomusic.R;
import club.lovemo.lovemomusic.R.string;
import club.lovemo.servise.MusicService;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;
import club.lovemo.utils.getMusic;

//import android.widget.Toast;

/**
 * 主页内容
 *
 * @author
 *
 */
public class ContentFragment extends BaseFragment implements
		android.view.View.OnClickListener {
	private LinearLayout layout, layout2;
	private int count;
	private LinearLayout linelayout;
	private int status = 0x001;
	private int musiclong = 0;
	private ImageButton btn_play, btn_next, btn_ona, btn_love, btn_sliding,
			btn_model, btn_more;
	private TextView tv_showsong, tv_showname, tv_showtime, tv_showtotatime,
			tv_timetiao;
	private SeekBar sk_progress;
	private TextView[] texts, texts2;
	private ArrayList<Fragment> mPagerList;
	private ViewPager mViewPager;
	private SlidingMenu slidingMenu;
	private MusicUpdateUI musicUpdateUI;
	public static int current = 0;
	private static int musiccurrent;
	private boolean isChange = true;// 是否在拖动进度条
	public static List<Mp3Info> Playlist;// 获取到的音频数据
	private int seek_progress;
	private AlertDialog countdowndlog;
	private static int songtime;
	private static int getmusiccurrent;
	private TextView tv_dlog;
	MusicHome_Activity homeActivity;
	public static boolean isDown = true;
	private boolean flag = true;
	View dlogview;
	private int time = 30;
	public Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			time--;
			tv_dlog.setText(time + "秒后应用将关闭");
			if (time == 0) {
				flag = false;
				time = 30;
				if (isDown) {
					Timingclosure();// 定时关闭
				}
			}
		};
	};

	/**
	 * 返回Sp存储的歌曲ID
	 *
	 * @return
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * 返回歌曲长度
	 *
	 * @return
	 */
	public int getSongtime() {
		return songtime;
	}

	/**
	 *
	 * @return Sp存储的歌曲进度
	 */
	public int getMusiccurrent() {
		return getmusiccurrent;
	}

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_conten, null);
		x.view().inject(this, view);
		TextView textView=(TextView) view.findViewById(R.id.home_text_view);
		if (MusicHome_Activity.isMarshmallow()){
			textView.setVisibility(View.INVISIBLE);
			//设置控件高度
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
			//获取当前控件的布局对象
			params.height = MusicHome_Activity.status_he;//设置当前控件布局的高度
			textView.setLayoutParams(params);//将设置好的布局参数应用到控件中

		}
		getPlayList();
		linelayout = (LinearLayout) view.findViewById(R.id.contentfragment);
		String bg = PrefUtils.getString(mActivity, "topic", "bg1");
		SetBackgroundImage.setBackGround(linelayout, bg);
		dlogview = LayoutInflater.from(mActivity).inflate(
				R.layout.canceltiming_activity, null);
		tv_dlog = (TextView) dlogview.findViewById(R.id.tv_dlog);
		homeActivity = (MusicHome_Activity) mActivity;
		slidingMenu = homeActivity.getSlidingMenu();
		init(view);
		String allbg = PrefUtils.getString(mActivity, "topic", "gb1");
		ChangeTopic(allbg);
		Dlog();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (MusicHome_Activity.isStatrService) {
			musicUpdateUI = new MusicUpdateUI();
			IntentFilter filter = new IntentFilter();
			filter.addAction(AppConstants.UPDATE_ACTION);
			getActivity().registerReceiver(musicUpdateUI, filter);
			Intent intent = new Intent(getActivity(), MusicService.class);
			getActivity().startService(intent);
			LogUtils.print("TEST", "startservice执行了");
		}

	}

	public void Dlog() {
		countdowndlog = new AlertDialog.Builder(mActivity)
				.setIcon(R.mipmap.dlogtime)
				.setView(dlogview)
				.setTitle(string.timingclosure)
				.setNeutralButton("取消定时关闭",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								// TODO Auto-generated method stub
								isDown = false;
							}
						}).setPositiveButton("知道了", null).create();
	}

	/**
	 * 分解酷狗歌曲标题得到歌曲名和歌手名
	 */
	public void setSongnameAndAname() {
		if(getMusic.getMp3Infos(mActivity).size()>0){
			String title = Playlist.get(current).getTitle();
			if ((Playlist.get(current).getArtist().equals("<unknown>") || Playlist
					.get(current).getArtist() == null) && title.indexOf("-") != -1) {
				tv_showsong.setText(title.split("-", 2)[1]);
				tv_showname.setText(title.split("-", 2)[0]);
			} else {
				tv_showsong.setText(title);
				if (Playlist.get(current).getArtist() == null
						|| Playlist.get(current).getArtist().equals("<unknown>")) {
					tv_showname.setText("未知艺术家");
				} else {
					tv_showname.setText(Playlist.get(current).getArtist());
				}
			}
		}
	}

	@SuppressLint("UseValueOf")
	@Override
	public void initData() {
		AppConstants.model = PrefUtils.getInt(mActivity, "model", -1);
		LogUtils.print("TEST", "model=" + AppConstants.model);
		switch (AppConstants.model) {
			case -1:
				btn_model.setImageResource(R.mipmap.shunxu);
				break;
			case 11:
				btn_model.setImageResource(R.mipmap.suiji);
				break;
			case 12:
				btn_model.setImageResource(R.mipmap.xunhuan);
				break;

			default:
				break;
		}
		try {// 尝试在启动时设置歌名
			current = PrefUtils.getInt(mActivity, "id", 0);// 获取Sp里存储的歌曲ID
			LogUtils.print("TEST", Playlist.size() + "palysize" + current);
			songtime = new Long(Long.valueOf(Playlist.get(current).getDuration())).intValue();// 根据歌曲ID查出歌曲时间
			tv_showtotatime.setText(getMusic.Format(songtime));
			setSongnameAndAname();
		} catch (Exception e) {
			// TODO: handle exception
		}
		getmusiccurrent = PrefUtils.getInt(mActivity, "musiccurrent", 0);// 获取上次退出时存储的歌曲进度；
		LogUtils.print("TEST", "上次的进度" + getmusiccurrent);
		initTitleView();
		/**
		 * 进度条监听，当用户按下时让线程暂停设置进度，放开后发送广播给Service进行播放
		 */
		sk_progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Intent intent = new Intent(AppConstants.CTL_ACTION);
				isChange = true;
				intent.putExtra("seek_progress", seek_progress);
				intent.putExtra("control", 5);
				intent.putExtra("current", current);
				mActivity.sendBroadcast(intent);
				if (status == 0x001) {
					tv_timetiao.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isChange = false;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				seek_progress = progress;
				if (fromUser) {
					String time = getMusic.Format(100 * progress);
					tv_timetiao.setText(time);
					tv_timetiao.setVisibility(View.VISIBLE);
				} else {
					tv_timetiao.setVisibility(View.INVISIBLE);
				}
				if (status == 0x001) {
					String time = getMusic.Format(100 * progress);
					tv_showtime.setText(time);
				}
			}
		});
		mViewPager.setOffscreenPageLimit(2);
		mPagerList = new ArrayList<Fragment>();
		mViewPager.setAdapter(new ContentAdapter(getFragmentManager()));
		/**
		 * 对ViewPager监听 使侧滑栏在第一页是才能拖出 使标签跟随滑动改变
		 */
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						for (int i = 0; i < texts.length; i++) {
							texts[i].setEnabled(true);
							texts2[i].setEnabled(true);
						}
						texts[arg0].setEnabled(false);
						texts2[arg0].setEnabled(false);
						switch (arg0) {
							case 0:
								slidingMenu
										.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
								break;
							default:
								slidingMenu
										.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
								break;
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});
		mViewPager.setCurrentItem(0);// 刚进入时设置ViewPager显示第一页
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	/**
	 * 关闭时记录歌曲播放进度，并注销广播监听
	 *
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// PrefUtils.setInt(mActivity, "musiccurrent", musiccurrent);

		// LogUtils.print("TEST", "存储的进度"+musiccurrent);

		Intent stopIntent = new Intent(getActivity(), MusicService.class);
		getActivity().stopService(stopIntent);
		getActivity().unregisterReceiver(musicUpdateUI);
		LogUtils.print("TEST", "contentFragmentdestroy");
	}

	/**
	 * 获取android系统数据库音频文件数据。
	 */
	public void getPlayList() {
		Playlist = new ArrayList<Mp3Info>();
		Playlist = getMusic.getMp3Infos(mActivity);
		LogUtils.print("TEST", Playlist.size()+"playlist.size");
	}

	public void init(View v) {
		btn_love = (ImageButton) v.findViewById(R.id.imagebtn_love);
		btn_love.setOnClickListener(this);
		btn_more = (ImageButton) v.findViewById(R.id.imagebtn_more);
		btn_more.setOnClickListener(this);
		btn_next = (ImageButton) v.findViewById(R.id.imagebtn_next);
		btn_next.setOnClickListener(this);
		btn_ona = (ImageButton) v.findViewById(R.id.imagebtn_ona);
		btn_ona.setOnClickListener(this);
		btn_play = (ImageButton) v.findViewById(R.id.imagebtn_play);
		btn_play.setOnClickListener(this);
		btn_sliding = (ImageButton) v.findViewById(R.id.imagebtn_sliding);
		btn_sliding.setOnClickListener(this);
		btn_model = (ImageButton) v.findViewById(R.id.imagebtn_model);
		btn_model.setOnClickListener(this);
		tv_showname = (TextView) v.findViewById(R.id.tv_showname);
		tv_showsong = (TextView) v.findViewById(R.id.tv_showsong);
		tv_showtime = (TextView) v.findViewById(R.id.tv_time);
		tv_showtotatime = (TextView) v.findViewById(R.id.tv_totaltime);
		sk_progress = (SeekBar) v.findViewById(R.id.seekBar);
		mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
		layout = (LinearLayout) v.findViewById(R.id.titleLayout);
		layout2 = (LinearLayout) v.findViewById(R.id.layoutbar);
		tv_timetiao = (TextView) v.findViewById(R.id.tv_timetiao);
	}

	class ContentAdapter extends FragmentPagerAdapter {

		public ContentAdapter(FragmentManager fm) {
			super(fm);
			mPagerList = new ArrayList<Fragment>();
			mPagerList.add(new PlayFragment());
			mPagerList.add(new PlaylistFragment());
			mPagerList.add(new SearchFragment());
		}

		@Override
		public Fragment getItem(int arg0) {
			return mPagerList.get(arg0);
		}

		@Override
		public int getCount() {
			return mPagerList.size();
		}
	}

	/**
	 * 设置标签页
	 */
	private void initTitleView() {
		MyClick listener = new MyClick();
		count = layout.getChildCount();
		texts = new TextView[count];
		texts2 = new TextView[count];
		for (int i = 0; i < count; i++) {
			texts[i] = (TextView) layout.getChildAt(i);
			texts[i].setEnabled(true);
			texts[i].setOnClickListener(listener);
			texts[i].setTag(i);
			texts2[i] = (TextView) layout2.getChildAt(i);
			texts2[i].setEnabled(true);
		}
		texts[0].setEnabled(false);
		texts2[0].setEnabled(false);
	}

	/**
	 * 监听标签点击
	 *
	 * @author John
	 *
	 */
	class MyClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int item = (Integer) v.getTag();
			mViewPager.setCurrentItem(item);
			for (int i = 0; i < texts.length; i++) {
				texts[i].setEnabled(true);
			}
			v.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(AppConstants.CTL_ACTION);
		switch (v.getId()) {
			case R.id.imagebtn_play:
				intent.putExtra("control", 1);
				break;
			case R.id.imagebtn_next:
				intent.putExtra("control", 2);
				break;
			case R.id.imagebtn_love:
				CustomToast.showToast(mActivity, "该功能尚未开发");
				break;
			case R.id.imagebtn_more:
				CustomToast.showToast(mActivity, "该功能尚未开发");
				break;
			case R.id.imagebtn_ona:
				intent.putExtra("control", 3);
				break;
			case R.id.imagebtn_sliding:
				slidingMenu.toggle();
				break;
			case R.id.imagebtn_model:
				setModel();
				break;
			default:
				break;
		}
		intent.putExtra("current", current);
		mActivity.sendBroadcast(intent);
	}

	/**
	 * 播放模式设置
	 */
	public void setModel() {
		int model = PrefUtils.getInt(mActivity, "model", -1);
		switch (model) {
			case -1:
				btn_model.setImageResource(R.mipmap.suiji);
				CustomToast.showToast(mActivity, "随机播放");
				model = 11;
				break;
			case 11:
				btn_model.setImageResource(R.mipmap.xunhuan);
				CustomToast.showToast(mActivity, "单曲循环");
				model = 12;
				break;
			case 12:
				btn_model.setImageResource(R.mipmap.shunxu);
				CustomToast.showToast(mActivity, "顺序播放");
				model = -1;
				break;
			default:
				break;
		}
		PrefUtils.setInt(mActivity, "model", model);
		AppConstants.model = model;
	}

	/**
	 * 接收更新进度、歌名、歌手名等
	 *
	 * @author John
	 *
	 */
	public class MusicUpdateUI extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int ButtonId=intent.getIntExtra(MusicService.INTENT_BUTTONID_TAG, 0);
			if(ButtonId==4){
				Timingclosure();
			}
			String timingc = intent.getStringExtra("msg");
			if (timingc != null) {
				if (timingc.equals("快关掉")) {
					flag = true;
					countdowndlog.show();
					new Thread() {
						@Override
						public void run() {
							while (flag) {
								Message msg = Message.obtain();
								msg.what = 0;
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									mHandler.sendMessage(msg);
								}
							}

						};
					}.start();

					return;
				}
			}
			String bg = intent.getStringExtra("topic");
			if (bg != null) {
				ChangeTopic(bg);
				LogUtils.print("TEST", "bg" + bg);
				return;
			}
			int update = intent.getIntExtra("update", -1);
			int setSecondaryProgress = intent.getIntExtra(
					"setSecondaryProgress", -1);

			int id = current;
			String urlsongname = intent.getStringExtra("urlsongname");
			// LogUtils.print("TEST", "urlsongname"+urlsongname);
			if (urlsongname == null) {
				current = intent.getIntExtra("current", 0);
			}
			if (current != id) {
				PrefUtils.setInt(mActivity, "id", current);
				LogUtils.print("TEST", "setcurrent" + current);
			}
			musiclong = intent.getIntExtra("musiclong", -1);
			if (setSecondaryProgress != -1) {
				if (MusicService.dowplay) {
					sk_progress.setSecondaryProgress(setSecondaryProgress
							* musiclong / 100 / 100);
					LogUtils.print("TEST", "setSecondaryProgress"
							+ setSecondaryProgress + "mulong/100" + musiclong
							/ 100);
				} else {
					sk_progress.setSecondaryProgress(0);
				}
			}
			if (musiclong != -1) {
				sk_progress.setMax(musiclong / 100);
				tv_showtotatime.setText(getMusic.Format(musiclong));
			}
			musiccurrent = intent.getIntExtra("musiccurrentDuration", 0);
			if (isChange && musiccurrent != 0) {
				sk_progress.setProgress(musiccurrent / 100);
				tv_showtime.setText(getMusic.Format(musiccurrent));
			}
			if (urlsongname != null) {
				tv_showsong.setText(urlsongname);
				tv_showname.setText(intent.getStringExtra("aname"));
			} else if (!MusicService.dowplay) {
				setSongnameAndAname();
			}
			if (update != -1) {
				switch (update) {
					case 0x001:// 代表暂停
						btn_play.setImageResource(R.mipmap.play);
						status = 0x001;
						break;
					case 0x002:// 代表播放
						btn_play.setImageResource(R.mipmap.suspended);
						status = 0x002;
						break;
					default:
						CustomToast.showToast(mActivity, "出错了没有这个选项");// 测试用
						break;
				}
			}
		}
	}

	public void Timingclosure() {
		Intent stopIntent = new Intent(mActivity, MusicService.class);
		mActivity.stopService(stopIntent);
		mActivity.finish();
	}

	public void ChangeTopic(String bg) {
		SetBackgroundImage.setBackGround(linelayout, bg);
	}

	public void SettingsPage() {
		mViewPager.setCurrentItem(1);
	}
}
