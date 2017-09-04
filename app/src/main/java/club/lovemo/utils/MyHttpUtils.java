package club.lovemo.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;

public class MyHttpUtils {
	public interface OnDownLoadListener{
		public void onDownLoad(String result);
	}
	OnDownLoadListener listener;
	public void setOnDownLoadListener(OnDownLoadListener listener){
		this.listener=listener;
	}
	public void requestPost(String url,Map<String, String> params){
		StringBuilder sb=new StringBuilder();
		Set<Map.Entry<String, String>> entrySet=params.entrySet();
		for(Map.Entry<String, String> entry:entrySet){
			sb.append(entry.getKey()+"="+entry.getValue()+"&");
		}
		sb.delete(sb.length()-1, sb.length());

		new PostAsyncTask(url).execute(sb.toString());
	}
	class PostAsyncTask extends AsyncTask<String, Void, String>{
		private String strUrl;
		public PostAsyncTask(String url){
			strUrl=url;
		}
		@Override
		protected String doInBackground(String... params) {
			String param=params[0];
			String response="";
			try {
				URL url=new URL(strUrl);
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5*1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.connect();

				//参数，以主体传
				DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
				dos.write(param.getBytes());
				dos.flush();
				dos.close();

				if(conn.getResponseCode()==200){
					InputStream is=conn.getInputStream();
					byte[] buffer=new byte[1024];
					int len=is.read(buffer,0,1024);
					response=new String(buffer,0,len);
				}
				System.out.println(response+"wwwwwwwwwww"+conn.getResponseCode());
				return response;

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				if(listener!=null){
					listener.onDownLoad(result);
				}
			}
			super.onPostExecute(result);
		}

	}
}
