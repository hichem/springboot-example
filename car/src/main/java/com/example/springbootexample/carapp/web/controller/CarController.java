package com.example.springbootexample.carapp.web.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.springbootexample.common.Car;


@RestController
public class CarController {

	@Autowired
	private CarDao _carDao;
	
	
	Logger logger = LoggerFactory.getLogger(CarController.class);
	
	@RequestMapping(value = "/cars/{id}", method=RequestMethod.GET)
	public Car getCar(@PathVariable int id) {
		
		//Fetch car with id
		Car aCar = _carDao.findById(id);
		
		return aCar;
	}
	
	
	@RequestMapping(value = "/cars", method=RequestMethod.GET)
	public List<Car> getCars() {
		
		return _carDao.findAll();
	}
	
	
	@PostMapping(value = "/cars")
	public ResponseEntity<Void> addCar(@RequestBody Car car) {
		
		//Add a car
		Car newCar = _carDao.save(car);
		
		//In case of failure
		if(newCar == null) {
			return ResponseEntity.noContent().build();
		}
		
		//Build the final uri of the resource
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newCar.getId())
				.toUri();
		
		//Return the new resource
		return ResponseEntity.created(location).build();
	}
	
	
	@DeleteMapping (value = "/cars/{id}")
	public void deleteCar(@PathVariable int id) {
		_carDao.deleteById(id);
	}
}
