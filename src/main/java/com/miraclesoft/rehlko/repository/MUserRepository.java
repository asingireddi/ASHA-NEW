package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.miraclesoft.rehlko.entity.MUser;

import reactor.core.publisher.Flux;

@EnableR2dbcRepositories

public interface MUserRepository extends ReactiveCrudRepository<MUser, Long>{
	
	@Query("SELECT id, fnme, lnme, email, location, designation, organization, office_phone, education,"
			+ " file_visibility, buyer_contacts, timezone FROM m_user WHERE id =:userId")
	Flux<MUser> findByUserId(Long userId);

}
