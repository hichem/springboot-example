package com.example.springbootexample.insurance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springbootexample.common.Contract;


@Repository
public interface ContractDao extends JpaRepository<Contract, Integer> {

	Contract findById(int id);
	
}
