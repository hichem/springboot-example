package com.example.springbootexample.insurance.controller;

import java.net.URI;
import java.util.List;

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

import com.example.springbootexample.common.Contract;
import com.example.springbootexample.insurance.repository.ContractDao;


@RestController
public class ContractController {

	@Autowired
	private ContractDao _contractDao;
	
	
	
	@RequestMapping(value = "/contracts/{id}", method=RequestMethod.GET)
	public Contract getContract(@PathVariable int id) {
		
		//Fetch contract with id
		Contract aContract = _contractDao.findById(id);
		
		return aContract;
	}
	
	
	@RequestMapping(value = "/contracts", method=RequestMethod.GET)
	public List<Contract> getContract() {
		
		return _contractDao.findAll();
	}
	
	
	@PostMapping(value = "/contracts")
	public ResponseEntity<Void> addContract(@RequestBody Contract contract) {
		
		//Add a contract
		Contract newContract = _contractDao.save(contract);
		
		//In case of failure
		if(newContract == null) {
			return ResponseEntity.noContent().build();
		}
		
		//Build the final uri of the resource
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newContract.getId())
				.toUri();
		
		//Return the new resource
		return ResponseEntity.created(location).build();
	}
	
	
	@DeleteMapping (value = "/contracts/{id}")
	public void deleteUser(@PathVariable int id) {
		_contractDao.deleteById(id);
	}
	
	
	
}
