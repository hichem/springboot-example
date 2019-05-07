package com.example.carapp.web.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CarDao extends JpaRepository<Car, Integer> {

	Car findById(int id);
	
}
