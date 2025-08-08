package com.miraclesoft.rehlko.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.Configurations;

@Repository
public interface  ConfigurationsRepository extends ReactiveCrudRepository<Configurations, Integer> {
	


}
