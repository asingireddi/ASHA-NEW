package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Mockito.when;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.impl.UtilizationServiceImpl;

/**
 * The Test UtilizationServiceTest
 * 
 * @author Manisha Sagar
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UtilizationServiceTest {

    @InjectMocks
    private UtilizationService utilizationService;
    
    @Mock
    private SearchCriteria searchCriteria;

    @Mock
    private UtilizationServiceImpl utilizationServiceImpl;

    @Test
    public void shouldGetMscvpRoles() {
        // When
        doReturn("Found mscvp roles succesfully.").when(utilizationServiceImpl)
                                                  .mscvpRoles();

        // Then
        assertThat(utilizationService.mscvpRoles()).isEqualTo("Found mscvp roles succesfully.");
    }

    @Test
    public void shouldFindPrimaryFlows() {
        // When
        doReturn("Found primary flows succesfully.").when(utilizationServiceImpl)
                                                    .primaryFlows();

        // Then
        assertThat(utilizationService.primaryFlows()).isEqualTo("Found primary flows succesfully.");
    }

    @Test
    public void shouldSearchDocumentTypes() {
        // Given
        final String database = "MSCVP";
        final List<String> documentTypesList = Arrays.asList("850", "856");

        // When
        doReturn(documentTypesList).when(utilizationServiceImpl)
                                   .documentTypes(database);

        // Then
        assertThat(utilizationService.documentTypes(database)).isEqualTo(documentTypesList);
    }

    @Test
    public void shouldFindCorrelations() {
        // Given
        final String transaction = "All";
        final List<String> correlationsList = Arrays.asList("PO Number", "BOL Number");

        // When
        doReturn(correlationsList).when(utilizationServiceImpl)
                                  .correlations(transaction);

        // Then
        assertThat(utilizationService.correlations(transaction)).isEqualTo(correlationsList);
    }

//    @Test
//    public void shouldGetWarehouses() {
//        // Given
//        final String database = "ARCHIVE";
//        final List<String> warehouses = Arrays.asList("Warehouse A", "Warehouse B", "Warehouse C");
//
//        // When
//        doReturn(warehouses).when(utilizationServiceImpl)
//                            .warehouses(database);
//
//        // Then
//        assertThat(utilizationService.warehouses(database)).isEqualTo(warehouses);
//    }

    @Test
    public void shouldGetParentWarehouses() {
        // Given
        final String database = "ARCHIVE";
        final List<String> parentWarehouses = Arrays.asList("Warehouse 1", "Warehouse 2", "Warehouse 3");

        // When
        doReturn(parentWarehouses).when(utilizationServiceImpl)
                                  .parentWarehouses(database);

        // Then
        assertThat(utilizationService.parentWarehouses(database)).isEqualTo(parentWarehouses);
    }

    @Test
    public void shouldGetWarehousesFor() {
        // Given
        final String database = "ARCHIVE";
        final String parentWarehouse = "Warehouse 1";
        final List<String> warehouses = Arrays.asList("Warehouse A", "Warehouse B", "Warehouse C");

        // When
        doReturn(warehouses).when(utilizationServiceImpl)
                            .warehousesFor(searchCriteria);

        // Then
        assertThat(utilizationService.warehousesFor(searchCriteria)).isEqualTo(warehouses);
    }
    
    @Test
    public void getFileTest() throws IOException {

    //Given
    String file = "MSCVP";
    ResponseEntity<byte[]> response = null;

    //When
    when(utilizationServiceImpl.getFileFromAmazonS3(file)).thenReturn(response);

    //Then
    assertEquals(utilizationService.getFileFromAmazonS3(file), response);
    }

}
