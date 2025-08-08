package com.miraclesoft.rehlko.repository;


import com.miraclesoft.rehlko.entity.OrderHeader;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderHeaderRepository extends ReactiveCrudRepository<OrderHeaderRepository, Integer> {

    @Query("SELECT * FROM order_header " + "WHERE (:correlationKey1 IS NULL OR correlation_key1 = :correlationKey1) " + "AND (:orderType IS NULL OR order_type = :orderType)")
    Flux<OrderHeader> findByCorrelationKey1AndOrderType(@Param("correlationKey1") String correlationKey1, @Param("orderType") String orderType);

}