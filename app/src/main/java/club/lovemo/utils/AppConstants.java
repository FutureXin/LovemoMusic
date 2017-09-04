package club.lovemo.utils;

public class AppConstants {
	public static final String LOG_STR="Test";
	public static final int VERSION = 1;
	public static final String SONGURL1 = "https://route.showapi.com/213-4?showapi_appid=19967&showapi_timestamp=";
	public static final String SONGURL2 = "&topid=";
	public static final String SONGURL3 = "&showapi_sign=b662dbb9e3c74438acfafa0082632b86";
	public static final String SEARCHURL1 = "https://route.showapi.com/213-1?keyword=";
	public static final String SEARCHURL2 = "&page=1&showapi_appid=19967&showapi_timestamp=";
	public static final String SEARCHURL3 = "&showapi_sign=b662dbb9e3c74438acfafa0082632b86";
	public static final String SEARCHLYRICS1 = "https://route.showapi.com/213-2?musicid=";
	public static final String SEARCHLYRICS2 = "&showapi_appid=19967&showapi_timestamp=";
	public static final String SEARCHLYRICS3 = "&showapi_sign=b662dbb9e3c74438acfafa0082632b86";
	public static final String LOGINURL = "http://192.168.8.12:8888/LovemoMusicServer/servlet/LoginServlet.login";
	public static final String REGISTEREDURL = "http://192.168.8.12:8888/LovemoMusicServer/servlet/RegisterServlet.register";
	public static final String GETENCRYPTEDURL = "http://192.168.8.12:8888/LovemoMusicServer/servlet/AccessEncryptedServlet.access";
	public static final String RESETPASSWORDURL = "http://192.168.8.12:8888/LovemoMusicServer/servlet/VerifyAnswerServlet.verify";

	public static final String SONGJSONLISTKEY = "SongJsonList";
	public static final String CTL_ACTION = "club.lovemo.lovemomusic.CTL_ACTION";
	public static final String UPDATE_ACTION = "club.lovemo.fragment.UPDATE_ACTION";
	public static final String UPDATE_PLATLIST = "club.lovemo.fragment.UPDATE_PLATLIST";
	public static final String UPDATEPLARUI_ACTION = "club.lovemo.fragment.UPDATEPLAYUI_ACTION";
	public final static String ACTION_BUTTON = "club.lovemo.lovemomusic.ButtonClick";
	public final static String UPDATE_LOGIN = "club.lovemo.fragment.UPDATE_LOGIN";
	// 播放状态
	public static final int STATUS_PLAY = 0x002; // 播放状态
	public static final int STATUS_PAUSE = 0x001; // 暂停状态
	// 播放模式
	public static final int PLAYMODE_REPEATSINGLE = 11;// 重复播放模式单一
	public static final int PLAYMODE_SEQUENCE = -1;// 顺序播放
	public static final int PLAYMODE_RANDOM = 12;// 随机播放
	public static int model;
	// service名
	public static final String SERVICE_CLASS = "club.lovemo.service.MusicService";
}
