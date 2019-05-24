package com.example.springbootexample.user.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springbootexample.common.User;


@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	User findById(int id);
	
}
