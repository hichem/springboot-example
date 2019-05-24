package com.example.springbootexample.insurance;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springbootexample.insurance.controller.ContractController;



@RunWith(SpringRunner.class)
@SpringBootTest
public class InsuranceApplicationTests {

	@Autowired
	private ContractController contractController;
	
	
	@Test
	public void contextLoads() {
		
		assertNotNull(contractController);
		
	}

}