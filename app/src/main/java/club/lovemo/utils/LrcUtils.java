package club.lovemo.utils;

import android.annotation.SuppressLint;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcUtils {
	@SuppressLint("SdCardPath") public static void convert(String name,String lrc) throws Exception {

		String final_lrc = lrc.replaceAll("<([^>]*)>", "").replaceAll(",([^]]*)]", "] ");
		/* 处理时间标签 */
		Pattern p = Pattern.compile("\\[\\d+?\\]");
		Matcher m = p.matcher(final_lrc);
		while (m.find()) {
			final_lrc = m.replaceFirst(m.group());
			m = p.matcher(final_lrc);
		}
//		String lrcFileName = fileName.replaceAll(".krc", ".lrc").replaceAll("\\s-\\s\\w+.lrc", ".lrc");
		FileOutputStream fos = new FileOutputStream("/mnt/sdcard/kugou/lyrics/"+name+".lrc");
		byte[] lrcbyte = final_lrc.getBytes();
		fos.write(lrcbyte);
		fos.close();
		System.out.println("文件保存为："+"/mnt/sdcard/kugou/lyrics/"+name+".lrc");
	}
}
