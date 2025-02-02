package com.pgms.pgmanage.dto;



public class TenantDto {

	private String username;
	private String tMail;
	private long phNumber;
	private String password;
	private String conPassword;

	public TenantDto() {

	}

	public TenantDto(String username, String tMail, long phNumber, String password, String conPassword) {
		super();
		this.username = username;
		this.tMail = tMail;
		this.phNumber = phNumber;
		this.password = password;
		this.conPassword = conPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String gettMail() {
		return tMail;
	}

	public void settMail(String tMail) {
		this.tMail = tMail;
	}

	public long getPhNumber() {
		return phNumber;
	}

	public void setPhNumber(long phNumber) {
		this.phNumber = phNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConPassword() {
		return conPassword;
	}

	public void setConPassword(String conPassword) {
		this.conPassword = conPassword;
	}

}
