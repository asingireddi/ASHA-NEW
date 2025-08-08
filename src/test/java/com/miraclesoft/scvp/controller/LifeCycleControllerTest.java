package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.LifeCyclePayload;
import com.miraclesoft.scvp.service.LifeCycleService;

/**
 * The Class LifeCycleControllerTest.java
 * 
 * @author Manisha Sagar
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LifeCycleControllerTest {

    @Mock
    private LifeCycleService lifeCycleService;

    @InjectMocks
    private LifeCycleController lifeCycleController = new LifeCycleController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetWarehouseOrderLifeCycles() {
        // Given
//        final LifeCycle lifeCycleOne = buildLifeCycle("940");
//        final LifeCycle lifeCycleTwo = buildLifeCycle("943");
//        final LifeCycle lifeCycleThree = buildLifeCycle("944");
//        final LifeCycle lifeCycleFour = buildLifeCycle("945");
//        final Map<String, Object> lifeCycles = (Map<String, Object>) Arrays.asList(lifeCycleOne, lifeCycleTwo, lifeCycleThree, lifeCycleFour);
    	final LifeCyclePayload lifeCyclePayload = buildLifeCyclePayload("LIVE","1258");
    	final LifeCycle lifeCycle = buildLifeCycle("940");
		final List<LifeCycle> lifeCycleList = new ArrayList<LifeCycle>();
		lifeCycleList.add(lifeCycle);
		final int count = 0;
		final CustomResponse customResponse =  new CustomResponse(lifeCycleList, count);
        // When
        when(lifeCycleService.lifeCycle(lifeCyclePayload)).thenReturn(customResponse);

        // Then
        assertEquals(customResponse,
                lifeCycleController.lifeCycle(lifeCyclePayload));
    }
    
    private LifeCyclePayload buildLifeCyclePayload(String database, String depositorOrderNumber) {
        return LifeCyclePayload.builder()
                        .database(database)
                        .depositorOrderNumber(depositorOrderNumber)
                        .build();
    }

    @Test
    public void shouldGetWarehouseOrderLifeCycleDetailInfo() {
        // Given
        final LifeCycle lifeCycle = buildLifeCycle("940");

        // When
        when(lifeCycleService.lifeCycleDetailInfo(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString())).thenReturn(lifeCycle);

        // Then
        assertEquals(lifeCycle, lifeCycleController.lifeCycleDetailInfo(
                lifeCycle.getDepositorOrderNumber(), lifeCycle.getFileId(), lifeCycle.getDatabase(),
                lifeCycle.getTransactionType()));
    }

    private LifeCycle buildLifeCycle(final String transaction) {
        return LifeCycle.builder()
                        .transactionType(transaction)
                        .database("LIVE")
                        .asnNumber("ASN")
                        .poNumber("PO")
                        .depositorOrderNumber("DON")
                        .fileId("FILE")
                        .build();
    }

}
