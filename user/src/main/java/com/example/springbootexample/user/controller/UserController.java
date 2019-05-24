package com.example.springbootexample.user.controller;

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

import com.example.springbootexample.common.User;
import com.example.springbootexample.user.service.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
public class UserController {

	@Autowired
	private UserDao _userDao;

	@Autowired
	private MessageSender messageSender;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	
	@RequestMapping(value = "/users/{id}", method=RequestMethod.GET)
	public User getUser(@PathVariable int id) {

		//Fetch user with id
		User aUser = _userDao.findById(id);

		return aUser;
	}


	@RequestMapping(value = "/users", method=RequestMethod.GET)
	public List<User> getUsers() {

		return _userDao.findAll();
	}


	@PostMapping(value = "/users")
	public ResponseEntity<Void> addUser(@RequestBody User user) {

		//Add a user
		User newUser = _userDao.save(user);

		//In case of failure
		if(newUser == null) {
			return ResponseEntity.noContent().build();
		}

		//Send new car object on the message queue
		ObjectMapper mapper = new ObjectMapper();
		String message;
		try {

			message = mapper.writeValueAsString(newUser);
			messageSender.sendMessage(message);

		} catch (JsonProcessingException e) {
			logger.error(String.format("[addCar] Error: %s", e.getMessage()));
		}

		//Build the final uri of the resource
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newUser.getId())
				.toUri();

		//Return the new resource
		return ResponseEntity.created(location).build();
	}


	@DeleteMapping (value = "/users/{id}")
	public void deleteUser(@PathVariable int id) {
		_userDao.deleteById(id);
	}
}
