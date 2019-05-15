package com.example.springbootexample.carapp;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springbootexample.carapp.web.controller.CarDao;
import com.example.springbootexample.common.Car;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CarDaoTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private CarDao carDao;
	
	
	@Test
	public void whenFoundByNameThenReturnCar() {
		
		//Create Car entity and persist it
		Car car = new Car("new_model", "new_serial_number");
		entityManager.persist(car);
		entityManager.flush();
		
		//Find entity
		Car found = carDao.findById(car.getId());
		
		//Compare
		//assertEquals(car.getModel(), car.getModel());
		assertEquals(car.getSerialNumber(), found.getSerialNumber());
	}
}
