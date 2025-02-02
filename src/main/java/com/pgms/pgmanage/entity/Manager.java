package com.pgms.pgmanage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Manager {

	@Id
	private int id = 1;
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String password;
	private String managerName;

	public int getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	@Override
	public String toString() {
		return "Manager [id=" + id + ", email=" + email + ", password=" + password + ", managerName=" + managerName
				+ "]";
	}

}
