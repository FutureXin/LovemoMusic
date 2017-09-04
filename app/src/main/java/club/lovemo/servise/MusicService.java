package club.lovemo.servise;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Random;

import club.lovemo.fragment.ContentFragment;
import club.lovemo.lovemomusic.MusicHome_Activity;
import club.lovemo.lovemomusic.R;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.BaseTools;
import club.lovemo.utils.LogUtils;
import club.lovemo.utils.PrefUtils;

public class MusicService extends Service implements OnBufferingUpdateListener,MediaPlayer.OnPreparedListener{
	// Notification管理
	public NotificationManager mNotificationManager;
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	public final static int BUTTON_PREV_ID = 3;
	//播放/暂停 按钮点击 ID
	public final static int BUTTON_PALY_ID = 1;
	//下一首 按钮点击
	public final static int BUTTON_NEXT_ID = 2;
	//退出按钮点击
	public final static int BUTTON_DOWN_ID = 4;
	Notification notify;
	String title="爱陌音乐";
	private String  name="";
	MyReceiver ServiceReceiver;
	MediaPlayer mPlayer;
	int status= 0x001;
	public int current = ContentFragment.current;
	int musiclong;
	boolean flag;
	boolean isChanging=false;
	String path;
	int seek_progress;
	private static int SecondaryProgress;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private Thread thread=new Thread(){//异步线程实时更新进度条和歌词
		public void run() {
			while(flag){
				if(status==0x002){
					try {
						Thread.sleep(100);
						if(mPlayer!=null){
							Intent sendIntent=new Intent(AppConstants.UPDATE_ACTION);
							int musiccurrent=mPlayer.getCurrentPosition();
							sendIntent.putExtra("musiccurrentDuration", musiccurrent);
							sendIntent.putExtra("musiclong", musiclong);
							sendIntent.putExtra("setSecondaryProgress", SecondaryProgress);
							sendIntent.putExtra("current", current);
							sendIntent.putExtra("update", status);
							Intent sendIntentUI=new Intent(AppConstants.UPDATEPLARUI_ACTION);
							sendIntentUI.putExtra("musiccurrentDuration",musiccurrent);
							sendIntentUI.putExtra("musiclong", musiclong);
							sendIntentUI.putExtra("current", current);
							sendIntentUI.putExtra("update", status);
//							sendIntentUI.putExtra("setSecondaryProgress", SecondaryProgress);
							sendBroadcast(sendIntent);
							sendBroadcast(sendIntentUI);

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	};
	/**
	 * 设置为上次关闭时的进度
	 */
	public void setSongSeek(){
		Intent sendIntent=new Intent(AppConstants.UPDATE_ACTION);
		Intent sendIntentUI=new Intent(AppConstants.UPDATEPLARUI_ACTION);
		ContentFragment cf=new ContentFragment();
		sendIntent.putExtra("musiclong",cf.getSongtime());
		sendIntent.putExtra("current", cf.getCurrent());
		sendIntent.putExtra("update", status);

		LogUtils.print("TEST", "getmusiccurrent"+cf.getMusiccurrent());

		sendIntent.putExtra("musiccurrentDuration", cf.getMusiccurrent());
		sendIntentUI.putExtra("musiclong", cf.getSongtime());
		sendIntentUI.putExtra("current",cf.getCurrent());
		sendIntentUI.putExtra("update", status);
		sendIntentUI.putExtra("musiccurrentDuration", cf.getMusiccurrent());
		sendBroadcast(sendIntent);
		sendBroadcast(sendIntentUI);
		Intent sendplaylistintent= new Intent(AppConstants.UPDATE_PLATLIST);
		sendplaylistintent.putExtra("playlist", cf.getCurrent());
		sendBroadcast(sendplaylistintent);
		try {
			String songname=ContentFragment.Playlist.get(current).getTitle();
			if((ContentFragment.Playlist.get(current).getArtist().equals("<unknown>")||ContentFragment.Playlist.get(current).getArtist()==null)&&songname.indexOf("-")!=-1){
				title=songname.split("-", 2)[1];
				name=songname.split("-", 2)[0];
			}else{
				title=songname;
				if(ContentFragment.Playlist.get(current).getArtist()==null||ContentFragment.Playlist.get(current).getArtist().equals("<unknown>")){
					name="未知艺术家";
				}else{
					name=ContentFragment.Playlist.get(current).getArtist();
				}
			}
		} catch (Exception e) {
		}
		showButtonNotify();
		if(cf.getMusiccurrent()!=0){
			prepareAndPlay(ContentFragment.Playlist.get(cf.getCurrent()).getUrl());
			mPlayer.seekTo(cf.getMusiccurrent());
			mPlayer.pause();
			isChanging=true;
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		initService();
		showButtonNotify();
		ServiceReceiver=new MyReceiver();
		IntentFilter filter= new IntentFilter();
		filter.addAction(AppConstants.CTL_ACTION);
		this.registerReceiver(ServiceReceiver, filter);
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
		mPlayer.setOnBufferingUpdateListener(this);
		mPlayer.setOnPreparedListener(this);
		flag=true;
		thread.start();
		LogUtils.print("TEST", "service启动了");
		setSongSeek();//设置为上次关闭时的进度

		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
//				int model=PrefUtils.getInt(LovemoApplication.getContext(), "model",-1);
				int size=ContentFragment.Playlist.size();
				switch (AppConstants.model) {
					case -1://顺序播放
						current++;
						if(current>=size){
							current=0;
						}
						break;
					case 11://随机播放
						Random random=new Random();
						current=random.nextInt(size);
						break;
					case 12://单曲循环
						break;
					default:
						break;
				}
				if(dowplay){
					DowUrl=null;
					dowplay=false;
				}
				SecondaryProgress=0;
				prepareAndPlay(ContentFragment.Playlist.get(current).getUrl());
				Intent sendIntent = new Intent(AppConstants.UPDATE_ACTION);
				sendIntent.putExtra("current", current);
				sendIntent.putExtra("update", status);
				sendBroadcast(sendIntent);
				Intent sendIntent2 = new Intent(AppConstants.CTL_ACTION);
				sendIntent2.putExtra("current", current);
				sendIntent2.putExtra("update", status);
				sendBroadcast(sendIntent2);
				Intent sendplaylistintent= new Intent(AppConstants.UPDATE_PLATLIST);
				sendplaylistintent.putExtra("playlist", current);
				sendBroadcast(sendplaylistintent);
			}
		});
	}
	private String DowUrl;
	public static boolean dowplay=false;//是否在播放网络歌曲
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(final Context context, Intent intent) {
			int control=intent.getIntExtra("control", -1);
			DowUrl=intent.getStringExtra("path");
			if(DowUrl==null){
				current=intent.getIntExtra("current", 0);
			}
			LogUtils.print("TEST", "ButtonBroadcastReceiver");
			int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
			if(buttonId!=0){
				control=buttonId;
			}
			switch (control) {
				case 1:
					if(status==0x001){
						if(!isChanging){
							prepareAndPlay(ContentFragment.Playlist.get(current).getUrl());
							isChanging=true;
						}else{
							mPlayer.start();
						}
						status=0x002;
					}else if(status==0x002){
						mPlayer.pause();
						status=0x001;
					}
					break;
				case 2:
					dowplay=false;
					if(status==0x002){
						current++;
						mPlayer.stop();
						if(current>=ContentFragment.Playlist.size()){
							current=0;
						}
						prepareAndPlay(ContentFragment.Playlist.get(current).getUrl());
					}else if(status==0x001){
						current++;
						if(current>=ContentFragment.Playlist.size()){
							current=0;
						}
						prepareAndPlay(ContentFragment.Playlist.get(current).getUrl());
						status=0x002;
					}
					SecondaryProgress=0;
					break;
				case 3:
					dowplay=false;
					if(status==0x002){
						current--;
						mPlayer.stop();
						if(current<0){
							current=ContentFragment.Playlist.size()-1;
						}
						prepareAndPlay(ContentFragment.Playlist.get(current).getUrl());

					}else if(status==0x001){
						current--;
						if(current<0){
							current=ContentFragment.Playlist.size()-1;
						}
						prepareAndPlay(ContentFragment.Playlist.get(current).getUrl());
						status=0x002;
					}
					SecondaryProgress=0;
					break;
				case 4:
					DowUrl=null;
					dowplay=false;
					path=intent.getStringExtra("path");
					mPlayer.stop();
					prepareAndPlay(path);
					status=0x002;
					current=intent.getIntExtra("current", 0);
					SecondaryProgress=0;
					break;
				case 5:
					seek_progress=intent.getIntExtra("seek_progress", 0);
					if(status==0x002){
						mPlayer.seekTo(seek_progress*100);
					}else if(status==0x001){
						mPlayer.seekTo(seek_progress*100);
						mPlayer.pause();
						Intent sendIntentUI=new Intent(AppConstants.UPDATEPLARUI_ACTION);
						sendIntentUI.putExtra("musiccurrentDuration",mPlayer.getCurrentPosition());
						sendIntentUI.putExtra("musiclong", musiclong);
						sendIntentUI.putExtra("current", current);
						sendIntentUI.putExtra("update", status);
						sendBroadcast(sendIntentUI);
					}
					current=intent.getIntExtra("current", 0);
					break;
				case 6:
					dowplay=true;
					LogUtils.print("TEST", "播放网络歌曲");
					if(status==0x002){
						mPlayer.stop();
						prepareAndPlay(DowUrl);
					}else if(status==0x001){
						prepareAndPlay(DowUrl);
						status=0x002;
					}
					SecondaryProgress=0;
					break;
			}
			Intent sendIntent= new Intent(AppConstants.UPDATE_ACTION);
			sendIntent.putExtra("current", current);
			sendIntent.putExtra("update", status);
			sendBroadcast(sendIntent);
			Intent sendplaylistintent= new Intent(AppConstants.UPDATE_PLATLIST);
			sendplaylistintent.putExtra("playlist", current);
			sendBroadcast(sendplaylistintent);
			String play_status = "";
			if(status==0x002){
				play_status = "开始播放";
			}else if(status==0x001){
				play_status = "已暂停";
			}
			try {//尝试设置歌名和歌手
				if(DowUrl!=null){
					title=intent.getStringExtra("urlsongname");
					name=intent.getStringExtra("aname");
					DowUrl=null;
					dowplay=true;
				}else if(!dowplay){
					String songname=ContentFragment.Playlist.get(current).getTitle();
					if((ContentFragment.Playlist.get(current).getArtist().equals("<unknown>")||ContentFragment.Playlist.get(current).getArtist()==null)&&songname.indexOf("-")!=-1){
						title=songname.split("-", 2)[1];
						name=songname.split("-", 2)[0];
					}else{
						title=songname;
						if(ContentFragment.Playlist.get(current).getArtist()==null||ContentFragment.Playlist.get(current).getArtist().equals("<unknown>")){
							name="未知艺术家";
						}else{
							name=ContentFragment.Playlist.get(current).getArtist();
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			showButtonNotify();
			LogUtils.print("TEST" , play_status);
		}
	}

	private void prepareAndPlay(String path){
		try {
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepare();
//			mPlayer.prepareAsync();
			musiclong=mPlayer.getDuration();
			if(DowUrl==null){
				mPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		SecondaryProgress=percent;
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(dowplay){
			mp.start();
		}
		LogUtils.print("TEST", "onPrepared");
	}

	/**
	 * 带按钮的通知栏
	 */
	public void showButtonNotify(){
		NotificationCompat.Builder mBuilder = new Builder(this);
		RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);
		mRemoteViews.setImageViewResource(R.id.custom_song_icon, R.mipmap.dh_);
		//API3.0 以上的时候显示按钮，否则消失
		mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, title);
		mRemoteViews.setTextViewText(R.id.tv_custom_song_name,name);
		//如果版本号低于（3。0），那么不显示按钮
		if(BaseTools.getSystemVersion() <= 9){
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
		}else{
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
			//
			if(status==0x002){
				mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.mipmap.suspended);
			}else if(status==0x001){
				mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.mipmap.play);
			}
		}
		//点击的事件处理
		Intent buttonIntent = new Intent(AppConstants.CTL_ACTION);
		Intent buttonIntent1 = new Intent(AppConstants.UPDATE_ACTION);
		// 上一首按钮
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
		buttonIntent.putExtra("current", current);
		//这里加了广播，所及INTENT的必须用getBroadcast方法
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		//播放/暂停  按钮
		buttonIntent.putExtra("current", current);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
		PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		//下一首 按钮
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		buttonIntent.putExtra("current", current);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);
		//退出按钮
		buttonIntent1.putExtra(INTENT_BUTTONID_TAG, BUTTON_DOWN_ID);
		PendingIntent intent_down = PendingIntent.getBroadcast(this, 4, buttonIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_down, intent_down);

		mBuilder.setContent(mRemoteViews)
				.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker("爱陌音乐")
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(true)
				.setSmallIcon(R.mipmap.leftmenu5);
		notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
//		notify.flags=Notification.FLAG_AUTO_CANCEL;
		Intent resultIntent = new Intent(this, MusicHome_Activity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(100, mBuilder.build());
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		PrefUtils.setInt(this, "musiccurrent", mPlayer.getCurrentPosition());
		LogUtils.print("TEST", "存储的进度"+ mPlayer.getCurrentPosition());
		clearAllNotify();
		flag=false;
		status=0x001;
		if(mPlayer!=null)
		{
			mPlayer.stop();
			mPlayer.release();
			mPlayer=null;
		}
		this.unregisterReceiver(ServiceReceiver);
		Intent stopIntent = new Intent(getApplication(), MusicService.class);
		getApplication().stopService(stopIntent);
		LogUtils.print("TEST", "serviceONDestroy");

	}
	/**
	 * 初始化要用到的系统服务
	 */
	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	/**
	 * 清除当前创建的通知栏
	 */
	public void clearNotify(int notifyId){
		mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
	}

	/**
	 * 清除所有通知栏
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// 删除你发的所有通知
	}

	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
	 * 点击去除： Notification.FLAG_AUTO_CANCEL
	 */
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}
}
