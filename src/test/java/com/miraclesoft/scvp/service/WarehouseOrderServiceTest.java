package com.miraclesoft.scvp.service;

import static com.miraclesoft.scvp.util.DateUtility.getTodayWithCustomTime;
import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.service.impl.WarehouseOrderServiceImpl;

/**
 * The Test WarehouseOrderServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WarehouseOrderServiceTest {

    @InjectMocks
    private WarehouseOrderService warehouseOrderService;

    @Mock
    private WarehouseOrderServiceImpl warehouseOrderServiceImpl;

    /*
     * @Test public void shouldSearchWarehouseOrders() { // Given final
     * WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND",
     * "Mock Sender Id", "Mock Receiver Id", "SUCCESS", "ACCEPTED"); final
     * List<WarehouseOrder> expectedWarehouseOrders = Arrays.asList(warehouseOrder);
     * final SearchCriteria searchCriteria = SearchCriteria.builder()
     * .database("LIVE") .transactionType("940") .senderId("Mock Sender Id")
     * .receiverId("Mock Receiver Id") .corrAttribute("Direction")
     * .corrValue("INBOUND") .corrAttribute1("Instance Id")
     * .corrValue1(warehouseOrder.getFileId()) .status("SUCCESS")
     * .ackStatus("ACCEPTED") .fromDate(getTodayWithCustomTime(0, 0, 0))
     * .toDate(getTodayWithCustomTime(23, 59, 59)) .build();
     * 
     * // When doReturn(expectedWarehouseOrders).when(warehouseOrderServiceImpl)
     * .search(searchCriteria);
     * 
     * // Then final List<WarehouseOrder> actualWarehouseOrders =
     * warehouseOrderService.search(searchCriteria);
     * assertThat(actualWarehouseOrders).isEqualTo(expectedWarehouseOrders); }
     */
    @Test
    public void shouldGetWarehouseOrderDetailInfo() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");

        // When
        doReturn(warehouseOrder).when(warehouseOrderServiceImpl)
                                .detailInfo(warehouseOrder.getDepositorOrderNumber(), warehouseOrder.getFileId(),
                                        "LIVE");

        // Then
        final WarehouseOrder actualWarehouseOrder = warehouseOrderService.detailInfo(
                warehouseOrder.getDepositorOrderNumber(), warehouseOrder.getFileId(), "LIVE");
        assertThat(actualWarehouseOrder).isEqualTo(warehouseOrder);

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
        doReturn(getInputStreamResource(mockFile)).when(warehouseOrderServiceImpl)
                                                  .download(searchCriteria);

        // Then
        final ResponseEntity<InputStreamResource> response = warehouseOrderService.download(searchCriteria);
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
    public void wareHouseDocumentTypeTest() {

    //Given
    final List<String> wareHouseDocument=Arrays.asList("database");

    //When
    doReturn(wareHouseDocument).when(warehouseOrderServiceImpl).wareHouseDocumentType("database");

    //Then
    final List<String> actualwareHouseDocument=warehouseOrderService.wareHouseDocumentType("database");
    assertThat(actualwareHouseDocument).isEqualTo(wareHouseDocument);
    }
}
