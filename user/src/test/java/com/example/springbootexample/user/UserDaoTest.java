package com.example.springbootexample.user;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springbootexample.common.User;
import com.example.springbootexample.user.controller.UserDao;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserDaoTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private UserDao contractDao;
	
	
	@Test
	public void whenFoundByNameThenReturnCar() {
		
		//Create Car entity and persist it
		User user = new User("new user firstname", "new user lastname");
		entityManager.persist(user);
		entityManager.flush();
		
		//Find entity
		User found = contractDao.findById(user.getId());
		
		//Compare
		assertEquals(user.getFirstname(), found.getFirstname());
	}
}
