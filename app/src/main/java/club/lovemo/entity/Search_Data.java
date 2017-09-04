package club.lovemo.entity;

import java.util.List;

public class Search_Data {
	private showapi_res_body showapi_res_body;
	private String showapi_res_code;
	private String showapi_res_error;
	
	@Override
	public String toString() {
		return "Search_Data [showapi_res_body=" + showapi_res_body
				+ ", showapi_res_code=" + showapi_res_code
				+ ", showapi_res_error=" + showapi_res_error + "]";
	}
	public Search_Data() {
		super();
	}
	public Search_Data(
			club.lovemo.entity.Search_Data.showapi_res_body showapi_res_body,
			String showapi_res_code, String showapi_res_error) {
		super();
		this.showapi_res_body = showapi_res_body;
		this.showapi_res_code = showapi_res_code;
		this.showapi_res_error = showapi_res_error;
	}
	public showapi_res_body getShowapi_res_body() {
		return showapi_res_body;
	}
	public void setShowapi_res_body(showapi_res_body showapi_res_body) {
		this.showapi_res_body = showapi_res_body;
	}
	public String getShowapi_res_code() {
		return showapi_res_code;
	}
	public void setShowapi_res_code(String showapi_res_code) {
		this.showapi_res_code = showapi_res_code;
	}
	public String getShowapi_res_error() {
		return showapi_res_error;
	}
	public void setShowapi_res_error(String showapi_res_error) {
		this.showapi_res_error = showapi_res_error;
	}
	public static class showapi_res_body{
		private pagebean pagebean;
		private String ret_code;
		public pagebean getPagebean() {
			return pagebean;
		}
		public void setPagebean(pagebean pagebean) {
			this.pagebean = pagebean;
		}
		public String getRet_code() {
			return ret_code;
		}
		public void setRet_code(String ret_code) {
			this.ret_code = ret_code;
		}
		public showapi_res_body(
				club.lovemo.entity.Search_Data.pagebean pagebean,
				String ret_code) {
			super();
			this.pagebean = pagebean;
			this.ret_code = ret_code;
		}
		public showapi_res_body() {
			super();
		}
		@Override
		public String toString() {
			return "showapi_res_body [pagebean=" + pagebean + ", ret_code="
					+ ret_code + "]";
		}
		
	}
	public static class pagebean{
		private int allNum;
		private int allPages;
		private List<contentlist> contentlist;
		private int currentPage;
		private int maxResult;
		private String motice;
		private String w;
		
		@Override
		public String toString() {
			return "pagebean [allNum=" + allNum + ", allPages=" + allPages
					+ ", contentlist=" + contentlist + ", currentPage="
					+ currentPage + ", maxResult=" + maxResult + ", motice="
					+ motice + ", w=" + w + "]";
		}
		public pagebean() {
			super();
		}
		public pagebean(int allNum, int allPages,
				List<club.lovemo.entity.Search_Data.contentlist> contentlist,
				int currentPage, int maxResult, String motice, String w) {
			super();
			this.allNum = allNum;
			this.allPages = allPages;
			this.contentlist = contentlist;
			this.currentPage = currentPage;
			this.maxResult = maxResult;
			this.motice = motice;
			this.w = w;
		}
		public int getAllNum() {
			return allNum;
		}
		public void setAllNum(int allNum) {
			this.allNum = allNum;
		}
		public int getAllPages() {
			return allPages;
		}
		public void setAllPages(int allPages) {
			this.allPages = allPages;
		}
		public List<contentlist> getContentlist() {
			return contentlist;
		}
		public void setContentlist(List<contentlist> contentlist) {
			this.contentlist = contentlist;
		}
		public int getCurrentPage() {
			return currentPage;
		}
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}
		public int getMaxResult() {
			return maxResult;
		}
		public void setMaxResult(int maxResult) {
			this.maxResult = maxResult;
		}
		public String getMotice() {
			return motice;
		}
		public void setMotice(String motice) {
			this.motice = motice;
		}
		public String getW() {
			return w;
		}
		public void setW(String w) {
			this.w = w;
		}
		
	}
	public static class contentlist{
		private String albumid;
		private String albummid;
		private String albumname;
		private String albumpic_big;
		private String albumpic_small;
		private String downUrl;
		private String m4a;
		private String media_mid;
		private String singerid;
		private String singername;
		private String songid;
		private String songmid;
		private String songname;
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
		public String getAlbumname() {
			return albumname;
		}
		public void setAlbumname(String albumname) {
			this.albumname = albumname;
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
		public String getM4a() {
			return m4a;
		}
		public void setM4a(String m4a) {
			this.m4a = m4a;
		}
		public String getMedia_mid() {
			return media_mid;
		}
		public void setMedia_mid(String media_mid) {
			this.media_mid = media_mid;
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
		public String getSongmid() {
			return songmid;
		}
		public void setSongmid(String songmid) {
			this.songmid = songmid;
		}
		public String getSongname() {
			return songname;
		}
		public void setSongname(String songname) {
			this.songname = songname;
		}
		public contentlist(String albumid, String albummid, String albumname,
				String albumpic_big, String albumpic_small, String downUrl,
				String m4a, String media_mid, String singerid,
				String singername, String songid, String songmid,
				String songname) {
			super();
			this.albumid = albumid;
			this.albummid = albummid;
			this.albumname = albumname;
			this.albumpic_big = albumpic_big;
			this.albumpic_small = albumpic_small;
			this.downUrl = downUrl;
			this.m4a = m4a;
			this.media_mid = media_mid;
			this.singerid = singerid;
			this.singername = singername;
			this.songid = songid;
			this.songmid = songmid;
			this.songname = songname;
		}
		@Override
		public String toString() {
			return "contentlist [albumid=" + albumid + ", albummid=" + albummid
					+ ", albumname=" + albumname + ", albumpic_big="
					+ albumpic_big + ", albumpic_small=" + albumpic_small
					+ ", downUrl=" + downUrl + ", m4a=" + m4a + ", media_mid="
					+ media_mid + ", singerid=" + singerid + ", singername="
					+ singername + ", songid=" + songid + ", songmid="
					+ songmid + ", songname=" + songname + "]";
		}
		public contentlist() {
			super();
		}
		
	}
}
