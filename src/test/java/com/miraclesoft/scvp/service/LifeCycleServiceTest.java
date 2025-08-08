package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.LifeCyclePayload;
import com.miraclesoft.scvp.service.impl.LifeCycleServiceImpl;

/**
 * The Test LifeCycleServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LifeCycleServiceTest {

    @InjectMocks
    private LifeCycleService lifeCycleService;

    @Mock
    private LifeCycleServiceImpl lifeCycleServiceImpl;

    @Test
    public void shouldSearchWarehouseorderLifeCycles() {
    	final LifeCyclePayload lifeCyclePayload = buildLifeCyclePayload("LIVE","1258");
        // Given
//        final Map<String, Object> lifeCycle = (Map<String, Object>) buildLifeCycle();
//        final Map<String, Object> expectedLifeCycles = lifeCycle;
        final LifeCycle lifeCycle = buildLifeCycle();
        final List<LifeCycle> expectedResponse =  new ArrayList<LifeCycle>();
        expectedResponse.add(lifeCycle);
        final int count = 0;
        final CustomResponse customResponse = new CustomResponse(expectedResponse,count);

        // When
        doReturn(customResponse).when(lifeCycleServiceImpl)
                                    .lifeCycle(lifeCyclePayload);

        // Then
        final CustomResponse actualLifeCycles = lifeCycleService.lifeCycle(lifeCyclePayload);
        assertThat(actualLifeCycles).isEqualTo(customResponse);

    }
    
    private LifeCyclePayload buildLifeCyclePayload(String database, String depositorOrderNumber) {
        return LifeCyclePayload.builder()
                        .database(database)
                        .depositorOrderNumber(depositorOrderNumber)
                        .build();
    }

    @Test
    public void shouldGetWarehouseorderLifeCycleDetailInfo() {
        // Given
        final LifeCycle warehouseOrder = buildLifeCycle();

        // When
        doReturn(warehouseOrder).when(lifeCycleServiceImpl)
                                .lifeCycleDetailInfo(warehouseOrder.getDepositorOrderNumber(),
                                        warehouseOrder.getFileId(), warehouseOrder.getDatabase(),
                                        warehouseOrder.getTransactionType());

        // Then
        final LifeCycle actualPo = lifeCycleService.lifeCycleDetailInfo(
                warehouseOrder.getDepositorOrderNumber(), warehouseOrder.getFileId(), warehouseOrder.getDatabase(),
                warehouseOrder.getTransactionType());
        assertThat(actualPo).isEqualTo(warehouseOrder);

    }

    private LifeCycle buildLifeCycle() {
        return LifeCycle.builder()
                        .build();
    }

}
