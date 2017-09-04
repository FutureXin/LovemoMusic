package club.lovemo.entity;

public class LrcsData {
	private showapi_res_body showapi_res_body;
	
	@Override
	public String toString() {
		return "LrcsData [showapi_res_body=" + showapi_res_body + "]";
	}

	public LrcsData(
			club.lovemo.entity.LrcsData.showapi_res_body showapi_res_body) {
		super();
		this.showapi_res_body = showapi_res_body;
	}

	public showapi_res_body getShowapi_res_body() {
		return showapi_res_body;
	}

	public void setShowapi_res_body(showapi_res_body showapi_res_body) {
		this.showapi_res_body = showapi_res_body;
	}

	public static class showapi_res_body{
		private String lyric;
		private String lyric_txt;
		public String getLyric() {
			return lyric;
		}
		public void setLyric(String lyric) {
			this.lyric = lyric;
		}
		public String getLyric_txt() {
			return lyric_txt;
		}
		public void setLyric_txt(String lyric_txt) {
			this.lyric_txt = lyric_txt;
		}
		public showapi_res_body(String lyric, String lyric_txt) {
			super();
			this.lyric = lyric;
			this.lyric_txt = lyric_txt;
		}
		@Override
		public String toString() {
			return "showapi_res_body [lyric=" + lyric + ", lyric_txt="
					+ lyric_txt + "]";
		}
		
	}
	
}
