package club.lovemo.entity;

import java.util.List;

public class InJsonData {
	private String shoapi_res_code;
	private String showapi_res_error;
	private showapi_res_body showapi_res_body;
	
	public InJsonData() {
		super();
	}
	public InJsonData(String shoapi_res_code, String showapi_res_error,
			club.lovemo.entity.InJsonData.showapi_res_body showapi_res_body) {
		super();
		this.shoapi_res_code = shoapi_res_code;
		this.showapi_res_error = showapi_res_error;
		this.showapi_res_body = showapi_res_body;
	}
	public String getShoapi_res_code() {
		return shoapi_res_code;
	}
	public void setShoapi_res_code(String shoapi_res_code) {
		this.shoapi_res_code = shoapi_res_code;
	}
	public String getShowapi_res_error() {
		return showapi_res_error;
	}
	public void setShowapi_res_error(String showapi_res_error) {
		this.showapi_res_error = showapi_res_error;
	}
	public showapi_res_body getShowapi_res_body() {
		return showapi_res_body;
	}
	public void setShowapi_res_body(showapi_res_body showapi_res_body) {
		this.showapi_res_body = showapi_res_body;
	}
	public static class showapi_res_body{
		private String ret_code;
		private pagebean pagebean;
		
		public showapi_res_body() {
			super();
		}
		public showapi_res_body(String ret_code,
				club.lovemo.entity.InJsonData.pagebean pagebean) {
			super();
			this.ret_code = ret_code;
			this.pagebean = pagebean;
		}
		public String getRet_code() {
			return ret_code;
		}
		public void setRet_code(String ret_code) {
			this.ret_code = ret_code;
		}
		public pagebean getPagebean() {
			return pagebean;
		}
		public void setPagebean(pagebean pagebean) {
			this.pagebean = pagebean;
		}
		
	}
	public static class pagebean{
		private String currentPage;
		private String ret_code;
		private List<songlist> songlist;
		private String total_song_num;
		
		public pagebean() {
			super();
		}
		public pagebean(String currentPage, String ret_code,
				List<club.lovemo.entity.InJsonData.songlist> songlist,
				String total_song_num) {
			super();
			this.currentPage = currentPage;
			this.ret_code = ret_code;
			this.songlist = songlist;
			this.total_song_num = total_song_num;
		}
		public String getCurrentPage() {
			return currentPage;
		}
		public void setCurrentPage(String currentPage) {
			this.currentPage = currentPage;
		}
		public String getRet_code() {
			return ret_code;
		}
		public void setRet_code(String ret_code) {
			this.ret_code = ret_code;
		}
		public List<songlist> getSonglist() {
			return songlist;
		}
		public void setSonglist(List<songlist> songlist) {
			this.songlist = songlist;
		}
		public String getTotal_song_num() {
			return total_song_num;
		}
		public void setTotal_song_num(String total_song_num) {
			this.total_song_num = total_song_num;
		}
		
	}
	public static class songlist{
		private String albumid;
		private String albummid;
		private String albumpic_big;
		private String albumpic_small;
		private String downUrl;
		private String seconds;
		private String singerid;
		private String singername;
		private String songid;
		private String songname; 
		private String url;
		
		public songlist() {
			super();
		}
		public songlist(String albumid, String albummid, String albumpic_big,
				String albumpic_small, String downUrl, String seconds,
				String singerid, String singername, String songid,
				String songname, String url) {
			super();
			this.albumid = albumid;
			this.albummid = albummid;
			this.albumpic_big = albumpic_big;
			this.albumpic_small = albumpic_small;
			this.downUrl = downUrl;
			this.seconds = seconds;
			this.singerid = singerid;
			this.singername = singername;
			this.songid = songid;
			this.songname = songname;
			this.url = url;
		}
		public String getAlbumid() {
			return albumid;
		}
		public void setAlbumid(String albumid) {
			this.albumid = albumid;
		}
		public String getAlbummid() {
			return albummid;
		}
		public void setAlbummid(String albummid) {
			this.albummid = albummid;
		}
		public String getAlbumpic_big() {
			return albumpic_big;
		}
		public void setAlbumpic_big(String albumpic_big) {
			this.albumpic_big = albumpic_big;
		}
		public String getAlbumpic_small() {
			return albumpic_small;
		}
		public void setAlbumpic_small(String albumpic_small) {
			this.albumpic_small = albumpic_small;
		}
		public String getDownUrl() {
			return downUrl;
		}
		public void setDownUrl(String downUrl) {
			this.downUrl = downUrl;
		}
		public String getSeconds() {
			return seconds;
		}
		public void setSeconds(String seconds) {
			this.seconds = seconds;
		}
		public String getSingerid() {
			return singerid;
		}
		public void setSingerid(String singerid) {
			this.singerid = singerid;
		}
		public String getSingername() {
			return singername;
		}
		public void setSingername(String singername) {
			this.singername = singername;
		}
		public String getSongid() {
			return songid;
		}
		public void setSongid(String songid) {
			this.songid = songid;
		}
		public String getSongname() {
			return songname;
		}
		public void setSongname(String songname) {
			this.songname = songname;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		} 
		
	}
}
