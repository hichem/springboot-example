package com.example.springbootexample.common;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Car {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String model;
	private String serialNumber;
	
	
	public Car() {
		
	}
	
	public Car(int id, String model, String serialNumber) {
		this.id = id;
		this.model = new String(model);
		this.serialNumber = new String(serialNumber);
	}
	
	public Car(String model, String serialNumber) {
		this.model = new String(model);
		this.serialNumber = new String(serialNumber);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	
}
