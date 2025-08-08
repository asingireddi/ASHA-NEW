package com.miraclesoft.rehlko.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.rehlko.entity.BannerNotifications;
import com.miraclesoft.rehlko.service.BannerNotificationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/bannerNotifications")
public class BannerNotificationController {

	private static final Logger logger = LoggerFactory.getLogger(BannerNotificationController.class);

	@Autowired
	private BannerNotificationService service;

	/**
	 * Creates a new banner notification.
	 *
	 * @param banner The banner notification to be created.
	 * @return Mono containing the created BannerNotifications entity wrapped in a
	 *         ResponseEntity.
	 */
	@PostMapping
	public Mono<ResponseEntity<BannerNotifications>> create(@RequestBody BannerNotifications banner) {
		logger.info("Creating new banner notification: {}", banner);
		return service.saveNotification(banner).map(saved -> {
			logger.info("Banner notification saved successfully with ID: {}", saved.getId());
			return ResponseEntity.ok(saved);
		});
	}

	/**
	 * Retrieves all banner notifications.
	 *
	 * @return Flux containing a list of all BannerNotifications.
	 */
	@GetMapping
	public Flux<BannerNotifications> getAll() {
		logger.info("Fetching all banner notifications");
		return service.getAllNotifications()
				.doOnComplete(() -> logger.info("Completed fetching all banner notifications"));
	}

	/**
	 * Updates an existing banner notification by ID.
	 *
	 * @param id     The ID of the banner notification to update.
	 * @param banner The updated banner notification details.
	 * @return Mono containing the updated BannerNotifications wrapped in a
	 *         ResponseEntity, or 404 if not found.
	 */
	@PutMapping("/{id}")
	public Mono<ResponseEntity<BannerNotifications>> updateBanner(@PathVariable Integer id,
			@RequestBody BannerNotifications banner) {
		logger.info("Updating banner notification with ID: {}", id);
		return service.updateNotification(id, banner).map(updated -> {
			logger.info("Successfully updated banner notification with ID: {}", id);
			return ResponseEntity.ok(updated);
		}).defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
