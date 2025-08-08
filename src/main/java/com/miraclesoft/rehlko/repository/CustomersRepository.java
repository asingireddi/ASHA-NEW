package com.miraclesoft.rehlko.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.Customers;

import reactor.core.publisher.Mono;

@Repository
public interface CustomersRepository extends ReactiveCrudRepository<Customers, Integer>{
	
	Mono<Customers> findByCustomerName(String customerName);

}
