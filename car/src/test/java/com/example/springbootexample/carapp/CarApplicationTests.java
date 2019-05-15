package com.example.springbootexample.carapp;


import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springbootexample.carapp.web.controller.CarController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarApplicationTests {

	@Autowired
	private CarController carController;
	
	
	@Test
	public void contextLoads() {
		
		assertNotNull(carController);
		
	}

}
