package club.lovemo.entity;

public class User {
	private int id;
	private String username;
	private String userpassword;
	private String encrypted;
	private String answer;
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", userpassword="
				+ userpassword + ", encrypted=" + encrypted + ", answer="
				+ answer + "]";
	}
	public User() {
		super();
	}
	public User(int id, String username, String userpassword, String encrypted,
			String answer) {
		super();
		this.id = id;
		this.username = username;
		this.userpassword = userpassword;
		this.encrypted = encrypted;
		this.answer = answer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpassword() {
		return userpassword;
	}
	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}
	public String getEncrypted() {
		return encrypted;
	}
	public void setEncrypted(String encrypted) {
		this.encrypted = encrypted;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
