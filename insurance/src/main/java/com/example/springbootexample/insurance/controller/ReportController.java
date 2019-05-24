package com.example.springbootexample.insurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.springbootexample.common.Car;
import com.example.springbootexample.common.Contract;
import com.example.springbootexample.common.User;
import com.example.springbootexample.insurance.model.Report;
import com.example.springbootexample.insurance.repository.ContractDao;

@RestController
public class ReportController {


	@Autowired
	private ContractDao _contractDao;


	//External micro-services dependencies
	@Value("${user.users.url}")
	private String usersUrl;

	@Value("${car.cars.url}")
	private String carsUrl;


	@GetMapping (value = "/reports/{id}")
	public Report getReports(@PathVariable int id) {

		Report report = null;
		
		//Fetch contract
		Contract contract = _contractDao.findById(id);

		if(contract != null) {
			//Fetch Car information from remote service
			Car car = fetchCarById(contract.getCarId());

			//Fetch User information from remote service
			User user = fetchUserById(contract.getUserId());
			
			//Create report
			report = new Report(car, user);
		}

		//Return report
		return report;

	}


	Car fetchCarById(int id) {
		Car car = null;

		RestTemplate restTemplate = new RestTemplate();
		car =  restTemplate.getForEntity(carsUrl + "/" + id, Car.class).getBody();

		return car;
	}

	User fetchUserById(int id) {
		User user = null;

		RestTemplate restTemplate = new RestTemplate();
		user =  restTemplate.getForEntity(usersUrl + "/" + id, User.class).getBody();

		return user;
	}

}
