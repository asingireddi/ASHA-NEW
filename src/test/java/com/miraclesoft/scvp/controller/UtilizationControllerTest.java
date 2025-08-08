package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.glue.model.SerDeInfo;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.UtilizationService;

/**
 * The Class UtilizationControllerTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UtilizationControllerTest {

    @Mock
    private UtilizationService utilizationService;
    
    @Mock
    private SearchCriteria searchCriteria;

    @InjectMocks
    private UtilizationController utilizationController = new UtilizationController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetMscvpRolesTest() throws JSONException {
        // Given
        final JSONArray mscvpRolesJsonArray = new JSONArray();
        final JSONObject obj1 = new JSONObject();
        obj1.put("id", 1);
        obj1.put("name", "MSCVP ADMIN");
        mscvpRolesJsonArray.put(obj1);
        final JSONObject obj2 = new JSONObject();
        obj2.put("id", 2);
        obj2.put("name", "GIS On-call, Admin");
        mscvpRolesJsonArray.put(obj2);

        // When
        when(utilizationService.mscvpRoles()).thenReturn(mscvpRolesJsonArray.toString());

        // Then
        assertEquals(utilizationController.mscvpRoles(), mscvpRolesJsonArray.toString());
    }

    @Test
    public void shouldGetPrimaryFlowsTest() throws JSONException {
        // Given
        final JSONArray primaryFlowJsonArray = new JSONArray();
        final JSONObject obj = new JSONObject();
        obj.put("id", 2);
        obj.put("name", "Manufacturing");
        primaryFlowJsonArray.put(obj);

        // When
        when(utilizationService.primaryFlows()).thenReturn(primaryFlowJsonArray.toString());

        // Then
        assertEquals(utilizationController.primaryFlows(), primaryFlowJsonArray.toString());
    }

    @Test
    public void shouldGetDocumentTypesTest() {
        // Given
        final List<String> transactions = Arrays.asList("850", "856", "820");

        // When
        when(utilizationService.documentTypes("LIVE")).thenReturn(transactions);

        // Then
        assertEquals(utilizationController.documentTypes("M", "LIVE"), transactions);
    }

    @Test
    public void shouldGetCorrelationsTest() {
        // Given
        final List<String> correlations = Arrays.asList("Instance Id", "Direction", "PO number");

        // When
        when(utilizationService.correlations("850")).thenReturn(correlations);

        // Then
        assertEquals(utilizationController.correlations("850"), correlations);
    }
    
//    @Test
//    public void shouldGetWarehouses() {
//        // Given
//        final List<String> warehouses = Arrays.asList("Warehouse A", "Warehouse B", "Warehouse C");
//
//        // When
//        when(utilizationService.warehouses("LIVE")).thenReturn(warehouses);
//
//        // Then
//        assertEquals(utilizationController.warehouses("M", "LIVE"), warehouses);
//    }

    @Test
    public void shouldGetParentWarehouses() {
        // Given
        final List<String> parentWarehouses = Arrays.asList("Warehouse 1", "Warehouse 2", "Warehouse 3");

        // When
        when(utilizationService.parentWarehouses("LIVE")).thenReturn(parentWarehouses);

        // Then
        assertEquals(utilizationController.parentWarehouses("M", "LIVE"), parentWarehouses);
    }

    @Test
    public void shouldGetWarehousesFor() {
        // Given
        final List<String> warehouses = Arrays.asList("Warehouse A", "Warehouse B", "Warehouse C");

        // When
        when(utilizationService.warehousesFor(searchCriteria)).thenReturn(warehouses);

        // Then
        assertEquals(utilizationController.warehousesFor(searchCriteria), warehouses);
    }

}
