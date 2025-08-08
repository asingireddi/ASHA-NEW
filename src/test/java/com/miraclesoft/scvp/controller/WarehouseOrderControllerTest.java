package com.miraclesoft.scvp.controller;

import static com.miraclesoft.scvp.util.DateUtility.getTodayWithCustomTime;
import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.service.WarehouseOrderService;

/**
 * The Class WarehouseOrderControllerTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WarehouseOrderControllerTest {

    @Mock
    private WarehouseOrderService warehouseOrderService;

    @InjectMocks
    private WarehouseOrderController warehouseOrderController = new WarehouseOrderController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /*
     * @Test public void shouldSearchWarehouseOrders() { // Given final
     * WarehouseOrder warehouseOrderOne = buildWarehouseOrder("Mock Id", "INBOUND",
     * "Mock Sender Id", "Mock Receiver Id", "SUCCESS", "ACCEPTED"); final
     * WarehouseOrder warehouseOrderTwo = buildWarehouseOrder("Mock Id2", "INBOUND",
     * "Mock Sender Id2", "Mock Receiver Id2", "SUCCESS", "ACCEPTED"); final
     * List<WarehouseOrder> warehouseOrders = Arrays.asList(warehouseOrderOne,
     * warehouseOrderTwo); final SearchCriteria searchCriteria =
     * SearchCriteria.builder() .database("LIVE") .transactionType("940")
     * .senderId("Mock Sender Id") .receiverId("Mock Receiver Id")
     * .corrAttribute("Direction") .corrValue("INBOUND") .status("SUCCESS")
     * .ackStatus("ACCEPTED") .fromDate(getTodayWithCustomTime(0, 0, 0))
     * .toDate(getTodayWithCustomTime(23, 59, 59)) .build();
     * 
     * // When
     * when(warehouseOrderService.search(searchCriteria)).thenReturn(warehouseOrders
     * );
     * 
     * // Then assertEquals(warehouseOrders,
     * warehouseOrderController.search(searchCriteria)); }
     */

    @Test
    public void shouldGetWarehouseOrderDetailInfo() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");

        // When
        when(warehouseOrderService.detailInfo(warehouseOrder.getDepositorOrderNumber(), warehouseOrder.getFileId(),
                "LIVE")).thenReturn(warehouseOrder);

        // Then
        assertEquals(warehouseOrder, warehouseOrderController.detailInfo(warehouseOrder.getDepositorOrderNumber(),
                warehouseOrder.getFileId(), "LIVE"));
    }

    @Test
    public void shouldDownloadWarehouseOrders() throws IOException {
        // Given
        final SearchCriteria searchCriteria = SearchCriteria.builder()
                                                            .database("LIVE")
                                                            .transactionType("940")
                                                            .build();
        final File mockFile = new File("mock-file-name");
        mockFile.createNewFile();
        final FileWriter fileWriter = new FileWriter(mockFile);
        fileWriter.write("Mock message");
        fileWriter.close();

        // When
        when(warehouseOrderService.download(searchCriteria)).thenReturn(getInputStreamResource(mockFile));

        // Then
        final ResponseEntity<InputStreamResource> response = warehouseOrderController.download(searchCriteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockFile.delete();
    }

    private WarehouseOrder buildWarehouseOrder(final String fileId, final String direction, final String senderId,
            final String receiverId, final String status, final String ackStatus) {
        return WarehouseOrder.builder()
                             .fileId(fileId)
                             .transactionType("940")
                             .direction(direction)
                             .senderId(senderId)
                             .receiverId(receiverId)
                             .status(status)
                             .ackStatus(ackStatus)
                             .build();
    }
    @Test
    public void shouldWareHouseDocumentTypeTest() {
    // Given
    final String database="LIVE";

    final List<String> warehouseOrders = Arrays.asList(database);

    // When
    when(warehouseOrderService.wareHouseDocumentType(
    Mockito.anyString())).thenReturn(warehouseOrders);

    // Then
    assertEquals( warehouseOrderService.wareHouseDocumentType(
    Mockito.anyString()),warehouseOrders );
    }
}
