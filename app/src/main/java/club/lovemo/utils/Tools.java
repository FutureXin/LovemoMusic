package club.lovemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGH;

	public static boolean isSdCardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	public static boolean isNetWorkAvailableByPing(final Context context) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process pingProcess = runtime.exec("/system/bin/ping -c 1 www.baidu.com");
			int exitCode = pingProcess.waitFor();
			return (exitCode == 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isNetWorkAvailableByGet(final Context context,String Myurl) {
		boolean flag=false;
		try {
//			URL url = new URL("http://www.baidu.com");
			URL url = new URL(Myurl);
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setConnectTimeout(3000);
			urlc.connect();
			if (urlc.getResponseCode() == 200) {
				flag= true;
			}else {
				flag= false;
			}
		}catch (Exception e){
			flag=false;
		}
		return flag;
	}
	/**
	 *
	 * 获取是否有网络连接方法
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(final Context context) {
		boolean hasWifoCon = false;
		boolean hasMobileCon = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfos = cm.getAllNetworkInfo();
		for (NetworkInfo net : netInfos) {

			String type = net.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				LogUtils.print("TEST","get Wifi connection");
				if (net.isConnected()) {
					hasWifoCon = true;
				}
			}

			if (type.equalsIgnoreCase("MOBILE")) {
				LogUtils.print("TEST","get Mobile connection");
				if (net.isConnected()) {
					hasMobileCon = true;
				}
			}
		}
		return hasWifoCon || hasMobileCon;

	}


	/**
	 *
	 * 判断sd卡是否可读写
	 *
	 * @return
	 */
	public static boolean isCanUseSdCard() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 判断文件是否存在
	public static boolean fileIsExists(String strFile) {
		try {
			File f = new File(strFile);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static ArrayList<String[]> getLyric(String lyricPath) {
		if (lyricPath == null) {
			return null;
		}
		ArrayList<String[]> mp3Lyric = new ArrayList<String[]>();
		File file = new File(lyricPath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedReader reader = null;
		if (file.exists()) {
			try {
				if (file.exists()) {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bis.mark(4);
					byte[] first3bytes = new byte[3];
					// System.out.println("");
					// 找到文档的前三个字节并自动判断文档类型。
					bis.read(first3bytes);
					bis.reset();
					if (first3bytes[0] == (byte) 0xEF
							&& first3bytes[1] == (byte) 0xBB
							&& first3bytes[2] == (byte) 0xBF) {// utf-8

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-8"));

					} else if (first3bytes[0] == (byte) 0xFF
							&& first3bytes[1] == (byte) 0xFE) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"unicode"));
					} else if (first3bytes[0] == (byte) 0xFE
							&& first3bytes[1] == (byte) 0xFF) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-16be"));
					} else if (first3bytes[0] == (byte) 0xFF
							&& first3bytes[1] == (byte) 0xFF) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-16le"));
					} else {

						reader = new BufferedReader(new InputStreamReader(bis,
								"GBK"));
					}

					String strTemp = "";
					while ((strTemp = reader.readLine()) != null) {
						String patternStr = "(.)*([0-9]{2}):([0-9]{2}).([0-9]{2})(.)*";
						Pattern p = Pattern.compile(patternStr);
						Matcher m = p.matcher(strTemp);
						if (m.matches()) {
							String lyric[] = { strTemp.substring(2, 3),
									strTemp.substring(4, 6),
									strTemp.substring(10, strTemp.length()) };
							mp3Lyric.add(lyric);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (bis != null) {
					try {
						bis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return mp3Lyric;
		} else {
			return null;
		}
	}
}
