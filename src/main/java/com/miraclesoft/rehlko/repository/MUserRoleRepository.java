package com.miraclesoft.rehlko.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.miraclesoft.rehlko.entity.MUserRole;

import reactor.core.publisher.Mono;

public interface MUserRoleRepository extends ReactiveCrudRepository<MUserRole, Integer> {

	Mono<MUserRole> findByUserId(Long userId);

}
