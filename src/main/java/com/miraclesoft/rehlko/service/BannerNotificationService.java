package com.miraclesoft.rehlko.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.rehlko.entity.BannerNotifications;
import com.miraclesoft.rehlko.repository.BannerNotificationsRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BannerNotificationService {

	@Autowired
	private BannerNotificationsRepository repository;

	public Mono<BannerNotifications> saveNotification(BannerNotifications banner) {
		banner.setCreatedAt(LocalDateTime.now());
		banner.setUpdatedAt(LocalDateTime.now());
		return repository.save(banner);
	}

	public Flux<BannerNotifications> getAllNotifications() {
		return repository.findAll();
	}

	public Mono<BannerNotifications> updateNotification(Integer id, BannerNotifications updatedBanner) {
		return repository.findById(id).flatMap(existing -> {
			existing.setMessage(updatedBanner.getMessage());
			existing.setModifiedBy(updatedBanner.getModifiedBy());
			existing.setActive(updatedBanner.isActive());
			existing.setUpdatedAt(LocalDateTime.now());
			return repository.save(existing);
		});
	}

}
