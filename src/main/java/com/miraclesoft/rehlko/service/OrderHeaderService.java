package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.entity.OrderHeader;
import com.miraclesoft.rehlko.repository.OrderHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class OrderHeaderService {
    @Autowired
    OrderHeaderRepository orderHeaderRepository;

    public Flux<OrderHeader> getOrdersByCorrelationKey1andorderType(String correlationKey1, String orderType) {
        if (correlationKey1 != null && correlationKey1.isBlank()) {
            correlationKey1 = null;
        }
        if (orderType != null && orderType.isBlank()) {
            orderType = null;
        }
        return orderHeaderRepository.findByCorrelationKey1AndOrderType(correlationKey1, orderType);
    }
}
