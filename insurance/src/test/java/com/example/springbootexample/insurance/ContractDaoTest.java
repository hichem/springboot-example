package com.example.springbootexample.insurance;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springbootexample.common.Contract;
import com.example.springbootexample.insurance.model.ContractDao;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ContractDaoTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ContractDao contractDao;
	
	
	@Test
	public void whenFoundByNameThenReturnCar() {
		
		//Create Car entity and persist it
		Contract contract = new Contract(1000, 1000);
		entityManager.persist(contract);
		entityManager.flush();
		
		//Find entity
		Contract found = contractDao.findById(contract.getId());
		
		//Compare
		assertEquals(contract.getCarId(), found.getCarId());
	}
}
