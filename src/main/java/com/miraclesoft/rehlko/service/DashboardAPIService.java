package com.miraclesoft.rehlko.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.rehlko.dto.StatusCountDTO;
import com.miraclesoft.rehlko.repository.InboxRepository;
import com.miraclesoft.rehlko.repository.OutboxRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DashboardAPIService {

	private final InboxRepository inboxRepository;
	private final OutboxRepository outboxRepository;

	@Autowired
	public DashboardAPIService(InboxRepository inboxRepository, OutboxRepository outboxRepository) {
		this.inboxRepository = inboxRepository;
		this.outboxRepository = outboxRepository;
	}

	/*
	 * Retrieves the count of each status from inbox and outbox tables for the given
	 * partner ID. Returns default empty maps if no data is found.
	 *
	 * @param partnerId the partner ID for which status counts are retrieved
	 * 
	 * @return Mono containing a map with "inbound" and "outbound" keys and their
	 * status count maps
	 */
	public Mono<Map<String, Map<String, Long>>> getStatusCountByPartnerId(String partnerId) {
		log.info("Fetching status count for partnerId: {}", partnerId);

		Mono<Map<String, Long>> inboundStatusMapMono = inboxRepository.countStatusByPartnerIdInbox(partnerId)
				.collectMap(StatusCountDTO::getStatus, StatusCountDTO::getCount).defaultIfEmpty(Collections.emptyMap());

		Mono<Map<String, Long>> outboundStatusMapMono = outboxRepository.countStatusByPartnerIdOutbox(partnerId)
				.collectMap(StatusCountDTO::getStatus, StatusCountDTO::getCount).defaultIfEmpty(Collections.emptyMap());

		return Mono.zip(inboundStatusMapMono, outboundStatusMapMono).map(resultTuple -> {
			Map<String, Long> inboundStatusMap = resultTuple.getT1();
			Map<String, Long> outboundStatusMap = resultTuple.getT2();

			log.debug("Inbox Status Map: {}", inboundStatusMap);
			log.debug("Outbox Status Map: {}", outboundStatusMap);

			Map<String, Map<String, Long>> responseMap = new HashMap<>();
			responseMap.put("inbox", inboundStatusMap);
			responseMap.put("outbox", outboundStatusMap);

			return responseMap;
		}).doOnSuccess(result -> log.info("Successfully fetched status counts for partnerId: {}", partnerId))
				.doOnError(error -> log.error("Failed to fetch status counts for partnerId: {}", partnerId, error));
	}
}