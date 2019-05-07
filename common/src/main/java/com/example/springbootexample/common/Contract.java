package com.example.springbootexample.common;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Contract {

	
	@Id
	@GeneratedValue
	private int id;
	
	private int userId;
	private int carId;
	
	
	public Contract() {
		
	}
	
	public Contract(int id, int userId, int carId) {
		this.id = id;
		this.userId = userId;
		this.carId = carId;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	
	
	
}
