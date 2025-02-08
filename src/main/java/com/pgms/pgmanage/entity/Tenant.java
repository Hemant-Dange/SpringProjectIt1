package com.pgms.pgmanage.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tenants", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "tMail"), @UniqueConstraint(columnNames = "phNumber") })
public class Tenant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "Username is required")
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Column(nullable = false, unique = true)
	private String tMail;

	@Column(nullable = false, unique = true)
	private long phNumber;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	@Column(nullable = false)
	private String password;

	@Transient
	private String conPassword;

	@OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Booking> bookings;

	public Tenant() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

}
