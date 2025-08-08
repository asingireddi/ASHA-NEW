package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.miraclesoft.rehlko.entity.Users;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableR2dbcRepositories
public interface  UserRepository extends ReactiveCrudRepository<Users, Integer> {

	Flux<Users> findAll();
	
	Mono<Users> findByEmailId(String emailId);
	
	@Modifying
    @Transactional
    @Query("UPDATE users u SET u.customer_name = ?, u.customer_id = ? WHERE u.id = ?")
	 Mono<Integer> updateCustomerNameAndCustomerIdById(String customerName, int customerId, int id);

	
}
