package club.lovemo.utils;

import android.annotation.SuppressLint;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import club.lovemo.fragment.PlayFragment;

public class Krctolrc {
	private static final char key[] = { '@', 'G', 'a', 'w', '^', '2', 't', 'G', 'Q', '6', '1', '-', '\316', '\322','n', 'i' };
	static String name;
	@SuppressLint("SdCardPath")
	public Krctolrc(String name){
		Krctolrc.name=name;
	}
	@SuppressLint("SdCardPath")
	public void Krcto() {
		try {
			convert(PlayFragment.getSDPath()+"/kugou/lyrics/"+name+".krc");
		}
		catch (Exception e) {
			System.err.println("failure!没有该文件");
			e.printStackTrace();
			return;
		}
		System.out.println("success");
	}

	/*
	 * 参数：文件名 函数作用：解密转换
	 */
	public static void convert(String fileName) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(fileName, "r");
		byte[] content = new byte[(int) (raf.length() - 4)];
		raf.skipBytes(4);
		raf.read(content);
		raf.close();

		for (int i = 0, length = content.length; i < length; i++) {
			int j = i % 16; // 循环异或解密
			content[i] ^= key[j];
		}

		String lrc = null;

		lrc = new String(decompress(content), "utf-8"); // 解压为 utf8

		String final_lrc = lrc.replaceAll("<([^>]*)>", "").replaceAll(",([^]]*)]", "] ");
		/* 处理时间标签 */
		Pattern p = Pattern.compile("\\[\\d+?\\]");
		Matcher m = p.matcher(final_lrc);
		while (m.find()) {
			final_lrc = m.replaceFirst(toTime(m.group()));
			m = p.matcher(final_lrc);
		}
//		String lrcFileName = fileName.replaceAll(".krc", ".lrc").replaceAll("\\s-\\s\\w+.lrc", ".lrc");
		FileOutputStream fos = new FileOutputStream(PlayFragment.getSDPath()+"/lovemomusic_lyrics/"+name+".lrc");
		byte[] lrcbyte = final_lrc.getBytes();
		fos.write(lrcbyte);
		fos.close();
		System.out.println("文件保存为："+PlayFragment.getSDPath()+"/lovemomusic_lyrics/"+name+".lrc");
	}

	private static String toTime(String num) {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		long time = Long.parseLong(num.substring(1, num.length() - 1));
		return "[" + sdf.format(time) + "." + ((time % 1000) / 10) + "]";
	}

	/*
	 * 解压
	 */
	private static byte[] decompress(byte[] data) throws Exception {

		byte[] output = new byte[0];
		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);
		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		byte[] buf = new byte[1024];
		while (!decompresser.finished()) {
			int i = decompresser.inflate(buf);
			o.write(buf, 0, i);
		}
		output = o.toByteArray();
		o.close();
		decompresser.end();
		return output;
	}
}
