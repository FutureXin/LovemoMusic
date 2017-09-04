package club.lovemo.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import club.lovemo.lovemomusic.LoginSuccess_Activity;
import club.lovemo.lovemomusic.Login_Activity;
import club.lovemo.lovemomusic.MusicHome_Activity;
import club.lovemo.lovemomusic.R;
import club.lovemo.lovemomusic.R.string;
import club.lovemo.servise.MusicService;
import club.lovemo.utils.AppConstants;
import club.lovemo.utils.CustomToast;
import club.lovemo.utils.PrefUtils;
import club.lovemo.utils.SetBackgroundImage;

/**
 * 侧边栏
 *
 * @author
 *
 */
public class LeftMenuFragment extends BaseFragment implements android.view.View.OnClickListener{
    //其实注解find id比直接find id效率要低
    @ViewInject(R.id.left_menu_listview)
    private ListView lvList;

    private Button btn_top1;
    private Button btn_top2;
    private Button btn_top3;
    @ViewInject(R.id.left_menulistview_tvname)
    private Button left_menulistview_tvname;

    @ViewInject(R.id.left_menulistview_image)
    private ImageButton Imagebtn;

    private MenuAdapter mAdapter;
    private AlertDialog dlog;
    private AlertDialog volumedlog;
    private AlertDialog timedlog;
    View volumeview;
    private RelativeLayout leftlayout;
    ContentFragment contentFragment;
    View myview;
    private leftMenuReceiver mReceiver;
    private SeekBar volumeseekbar;
    private int timeminutes;
    private String bg;
    AudioManager am = null;
    Login_Activity activity=new Login_Activity();
    private String [] leftmenu=new String [] {"搜索歌曲","定时关闭","调整音量","更换皮肤","听歌识曲","本地音乐","返回首页","退出"};
    private int [] leftmenuimage=new int []{R.mipmap.leftmenu1,R.mipmap.leftmenu2,
            R.mipmap.leftmenu3,R.mipmap.leftmenu4,R.mipmap.leftmenu5,
            R.mipmap.leftmenu6,R.mipmap.leftmenu7,R.mipmap.leftmenu8};
    static final String[] str=new String[]{"关闭","10分钟","15分钟","20分钟","30分钟","一小时"};
    private int chose;
    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        x.view().inject(this, view);
        TextView textView=(TextView) view.findViewById(R.id.left_text_view);
        if (MusicHome_Activity.isMarshmallow()){
            textView.setVisibility(View.INVISIBLE);
            //设置控件高度
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            //获取当前控件的布局对象
            params.height = MusicHome_Activity.status_he;//设置当前控件布局的高度
            textView.setLayoutParams(params);//将设置好的布局参数应用到控件中

        }
        contentFragment=new ContentFragment();
        leftlayout=(RelativeLayout) view.findViewById(R.id.leftfragment);
        myview=LayoutInflater.from(mActivity).inflate(R.layout.topic_activity, null);
        volumeview=LayoutInflater.from(mActivity).inflate(R.layout.volume_activity, null);
        volumeseekbar=(SeekBar) volumeview.findViewById(R.id.volumseekBar);
        am = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        btn_top1=(Button) myview.findViewById(R.id.btn_topic1);
        btn_top2=(Button) myview.findViewById(R.id.btn_topic2);
        btn_top3=(Button) myview.findViewById(R.id.btn_topic3);
        btn_top1.setOnClickListener(this);
        btn_top2.setOnClickListener(this);
        btn_top3.setOnClickListener(this);
        Dlog();
        VolumeDlog();
        TimeDlog();
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mReceiver=new leftMenuReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(AppConstants.UPDATE_LOGIN);
        getActivity().registerReceiver(mReceiver, filter);
        Intent intent = new Intent(getActivity(),MusicService.class);
        getActivity().startService(intent);
    }
    public void VolumeDlog(){
        volumedlog=new AlertDialog.Builder(mActivity)
                .setIcon(R.mipmap.playlistimage)
                .setTitle("音量设置")
                .setView(volumeview)
                .setNegativeButton(string.back, null)
                .create();
    }
    public void Dlog(){
        dlog = new AlertDialog.Builder(mActivity)
                .setIcon(R.mipmap.leftmenu4)
                .setTitle(string.themesettings)
                .setView(myview)
                .setCancelable(false)
                .setPositiveButton(string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String bga=PrefUtils.getString(mActivity, "topic", "bg1");
                        if(!bg.equals(bga)){
                            PrefUtils.setString(mActivity, "topic", bg);
                        }
                    }
                }).setNeutralButton(string.cancel, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String getbg=PrefUtils.getString(mActivity, "topic", "bg1");
                        SetBackgroundImage.setBackGround(leftlayout, getbg);
                        ChangeTopic(getbg);
                    }
                }).create();
    }
    public void TimeDlog(){

        timedlog = new AlertDialog.Builder(mActivity)
                .setIcon(R.mipmap.dlogtime)
                .setTitle(string.timingclosure)
                .setSingleChoiceItems(str,chose, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                TimeCancel();
                                CustomToast.showToast(mActivity,getResources().getString(R.string.timingclosure));
                                break;
                            case 1:
                                timeminutes=1;//为了测试改为了一分钟
                                SetAlarmManager();
                                CustomToast.showToast(mActivity, "10分钟后关闭");
                                break;
                            case 2:
                                timeminutes=15;
                                SetAlarmManager();
                                CustomToast.showToast(mActivity, "15分钟后关闭");
                                break;
                            case 3:
                                timeminutes=20;
                                SetAlarmManager();
                                CustomToast.showToast(mActivity, "20分钟后关闭");
                                break;
                            case 4:
                                timeminutes=30;
                                SetAlarmManager();
                                CustomToast.showToast(mActivity, "30分钟后关闭");
                                break;
                            case 5:
                                timeminutes=60;
                                SetAlarmManager();
                                CustomToast.showToast(mActivity, "一小时后关闭");
                                break;
                            default:
                                break;
                        }
                    }
                }).setNegativeButton(string.back,null).create();
    }
    /**
     * 关闭闹钟
     */
    public void TimeCancel(){
        MusicHome_Activity homeActivity = (MusicHome_Activity) mActivity;
        Intent intent = new Intent(AppConstants.UPDATE_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(mActivity, 0, intent, 0);
        AlarmManager am = (AlarmManager) homeActivity.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }
    @Override
    public void initData() {
        mAdapter=new MenuAdapter();
        lvList.setAdapter(mAdapter);
        String allbg=PrefUtils.getString(mActivity, "topic", "bg1");
        SetBackgroundImage.setBackGround( leftlayout, allbg);
        lvList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        Search_Activity();
                        break;
                    case 1:
                        if(!ContentFragment.isDown){
                            chose=0;
                            TimeDlog();
                        }
                        timedlog.show();
                        break;
                    case 2:
                        volumedlog.show();
                        break;
                    case 3:
                        dlog.show();
                        break;
                    case 4:
                        CustomToast.showToast(mActivity, "该功能尚未开发！！！");
                        break;
                    case 5:
                        toggleSlidingMenu();
                        MusicHome_Activity homeActivity = (MusicHome_Activity) mActivity;
                        homeActivity.getContentFragment().SettingsPage();
                        break;
                    case 6:
                        toggleSlidingMenu();
                        break;
                    case 7:
                        Intent stopIntent = new Intent(mActivity, MusicService.class);
                        mActivity.stopService(stopIntent);
                        mActivity.finish();
                        break;
                    default:
                        break;
                }
            }
        });
        volumeseekbar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeseekbar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeseekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress ,AudioManager.FLAG_VIBRATE);
            }
        });
        left_menulistview_tvname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String username=PrefUtils.getString(mActivity, "username","");
                if(!username.equals("")){
                    Intent intent2=new Intent(mActivity,LoginSuccess_Activity.class);
                    intent2.putExtra("username",username);
                    mActivity.startActivity(intent2);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }else{
                    Intent intent=new Intent(mActivity,Login_Activity.class);
                    mActivity.startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            }
        });

    }

    /**
     * 切换SlidingMenu的状态
     *
     * @param
     */
    protected void toggleSlidingMenu() {
        MusicHome_Activity homeActivity = (MusicHome_Activity) mActivity;
        SlidingMenu slidingMenu = homeActivity.getSlidingMenu();
        slidingMenu.toggle();// 切换状态, 显示时隐藏, 隐藏时显示
    }
    public void Search_Activity(){
        Intent intent=new Intent(mActivity,club.lovemo.lovemomusic.Search_Activity.class);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    /**
     * 设置主题
     *
     *
     */
    protected void ChangeTopic(String bg) {
        Intent intent= new Intent(AppConstants.UPDATE_ACTION);
        intent.putExtra("topic", bg);
        mActivity.sendBroadcast(intent);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_topic1:
                bg="bg1";
                SetBackgroundImage.setBackGround( leftlayout, bg);
                ChangeTopic(bg);
                break;
            case R.id.btn_topic2:
                bg="bg2";
                SetBackgroundImage.setBackGround( leftlayout, bg);
                ChangeTopic(bg);
                break;
            case R.id.btn_topic3:
                bg="bg3";
                SetBackgroundImage.setBackGround( leftlayout, bg);
                ChangeTopic(bg);
                break;
            default:
                break;
        }
    }

    /**
     * 侧边栏数据适配器
     *
     * @author
     *
     */
    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return leftmenu.length;
        }

        @Override
        public Object getItem(int position) {
            return leftmenu[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.left_menu_item,
                        null);
                holder = new ViewHolder();
                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.left_menulistview_tvname);
                holder.leftlistviewimage = (ImageView) convertView
                        .findViewById(R.id.left_menulistview_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvTitle.setText(leftmenu[position]);
            holder.leftlistviewimage.setImageResource(leftmenuimage[position]);
            return convertView;
        }

        class ViewHolder {
            TextView tvTitle;
            ImageView leftlistviewimage;
        }

    }

    public void SetAlarmManager(){
        ContentFragment.isDown=true;
        MusicHome_Activity homeActivity = (MusicHome_Activity) mActivity;
        // 创建Intent对象，action指向广播接收类
        Intent intent = new Intent(AppConstants.UPDATE_ACTION);
        intent.putExtra("msg", "快关掉");
        // 创建PendingIntent对象封装Intent，由于是使用广播，注意使用getBroadcast方法
        PendingIntent pi = PendingIntent.getBroadcast(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 获取AlarmManager对象
        AlarmManager am = (AlarmManager)homeActivity.getSystemService(Context.ALARM_SERVICE);
//		ALARM_SERVICE
        am.cancel(pi);
        long time=System.currentTimeMillis()+timeminutes*1000*60-30*1000;
        am.set(AlarmManager.RTC_WAKEUP, time, pi);
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        PrefUtils.setString(mActivity, "username", "");
        getActivity().unregisterReceiver(mReceiver);
    }
    public class leftMenuReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(AppConstants.UPDATE_LOGIN)){
                String username=intent.getStringExtra("username");
                left_menulistview_tvname.setText(username);
                PrefUtils.setString(mActivity, "username", username);
            }
        }

    }
}
