package com.example.springbootexample.insurance.model;

import com.example.springbootexample.common.Car;
import com.example.springbootexample.common.User;

public class Report {

	Car car;
	User user;

	public Report() {

	}

	public Report(Car car, User user) {
		this.car = car;
		this.user = user;
	}


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}




}
