package com.miraclesoft.rehlko.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.miraclesoft.rehlko.entity.Details820;

import reactor.core.publisher.Flux;

public interface Details820Repository extends ReactiveCrudRepository<Details820, Integer> {

	Flux<Details820> findByCorrelationKey1Val(String correlationKey1Val);
}
