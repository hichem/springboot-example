package com.example.springbootexample.common;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class User {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String firstname;
	private String lastname;
	
	
	public User() {
		
	}
	
	public User(int id, String firstname, String lastname) {
		this.id = id;
		this.firstname = new String(firstname);
		this.lastname = new String(lastname);
	}
	
	public User(String firstname, String lastname) {
		this.firstname = new String(firstname);
		this.lastname = new String(lastname);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	
	
}
