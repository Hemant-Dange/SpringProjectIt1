package com.pgms.pgmanage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TenantDto {

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String tMail;

	@NotNull(message = "Phone number is required")
	@Min(value = 6000000000L, message = "Phone number must be exactly 10 digits")
	@Max(value = 9999999999L, message = "Phone number must be exactly 10 digits")
	private Long phNumber;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotBlank(message = "Confirm Password is required")
	private String conPassword;

	public TenantDto() {
	}

	public TenantDto(String username, String tMail, Long phNumber, String password, String conPassword) {
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

	public Long getPhNumber() {
		return phNumber;
	}

	public void setPhNumber(Long phNumber) {
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
