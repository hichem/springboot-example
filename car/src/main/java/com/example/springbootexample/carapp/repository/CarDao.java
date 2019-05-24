package com.example.springbootexample.carapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springbootexample.common.Car;


@Repository
public interface CarDao extends JpaRepository<Car, Integer> {

	Car findById(int id);
	
}
