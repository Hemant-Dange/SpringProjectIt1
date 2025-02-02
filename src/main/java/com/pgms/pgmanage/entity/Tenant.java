package com.pgms.pgmanage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tenant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String username;
	private String tMail;
	private long phNumber;
	private String password;
	private String conPassword;

	public long getId() {
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

	@Override
	public String toString() {
		return "Tenant [id=" + id + ", username=" + username + ", tMail=" + tMail + ", phNumber=" + phNumber
				+ ", password=" + password + ", conPassword=" + conPassword + "]";
	}
	
	

}
