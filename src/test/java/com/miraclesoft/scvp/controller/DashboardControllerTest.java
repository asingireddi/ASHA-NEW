package com.miraclesoft.scvp.controller;

import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Dashboard;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.service.DashboardService;

/**
 * The Class DashboardControllerTest.java
 * 
 * @author Manisha Sagar
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController = new DashboardController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetDashboard() throws Exception {
        // Given
        final Dashboard dashboard = buildDashboard();
        final JSONArray inBoundJsonArray = new JSONArray();
        final JSONObject inboundJsonObject = new JSONObject();
        inboundJsonObject.put("name", "AAA_PARTNER A");
        inboundJsonObject.put("y", 10);
        inBoundJsonArray.put(inboundJsonObject);

        final JSONArray outBoundJsonArray = new JSONArray();
        final JSONObject outboundJsonObject = new JSONObject();
        outboundJsonObject.put("name", "BBB_PARTNER B");
        outboundJsonObject.put("y", 8);
        outBoundJsonArray.put(outboundJsonObject);

        final String resultString = "{\"data1\":" + inBoundJsonArray.toString() + ",\"data2\":"
                + outBoundJsonArray.toString() + "}";

        // When
        when(dashboardService.dashboard(dashboard)).thenReturn(resultString);

        // Then
        assertEquals(dashboardController.dashboard(dashboard), resultString);
    }

    @Test
    public void shouldGetDailyTransactions() throws JSONException {
        // Given
        final JSONArray documentsVolumeJsonArray = new JSONArray();
        final JSONObject dailyTransaction1 = new JSONObject();
        dailyTransaction1.put("name", "850");
        dailyTransaction1.put("y", 100L);
        documentsVolumeJsonArray.put(dailyTransaction1);
        final JSONObject dailyTransaction2 = new JSONObject();
        dailyTransaction2.put("name", "856");
        dailyTransaction2.put("y", 200L);
        documentsVolumeJsonArray.put(dailyTransaction2);
        // When
        when(dashboardService.dailyTransactions()).thenReturn(documentsVolumeJsonArray.toString());

        // Then
        assertEquals(dashboardController.dailyTransactions(), documentsVolumeJsonArray.toString());
    }

    @Test
    public void shouldGetHourlyVolumes() throws JSONException {
        // Given
        final List<Integer> hours = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21, 2, 23);
        final JSONArray hourlyVolumes = new JSONArray();
        for (final Integer hour : hours) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("hour", hour + ":00");
            jsonObject.put("count", hour * 10L);
            hourlyVolumes.put(jsonObject);
        }

        // When
        when(dashboardService.hourlyVolumes()).thenReturn(hourlyVolumes.toString());

        // Then
        assertEquals(dashboardController.hourlyVolumes(), hourlyVolumes.toString());
    }

    @Test
    public void shouldGetDocumentsDailyFailureRate() throws JSONException {
        // Given
        final JSONArray documentsDailyFailureRate = new JSONArray();
        final String name = "status";
        final String y = "count";
        final JSONObject count = new JSONObject();
        final JSONObject count1 = new JSONObject();
        final JSONObject count2 = new JSONObject();
        count.put(name, "Success");
        count.put(y, 80.5);
        documentsDailyFailureRate.put(count);
        count1.put(name, "Failure");
        count1.put(y, 10.5);
        documentsDailyFailureRate.put(count1);
        count2.put(name, "Pending");
        count2.put(y, 9.0);
        documentsDailyFailureRate.put(count2);
        // When
        when(dashboardService.dailyFailureRate()).thenReturn(documentsDailyFailureRate.toString());

        // Then
        assertEquals(dashboardController.dailyFailureRate(), documentsDailyFailureRate.toString());
    }

    @Test
    public void getTopTenTradingPartners() {
        // Given
        final StringBuilder partnerNamesWithCommaSeperated = new StringBuilder();
        final List<String> partners = Arrays.asList("P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10");
        for (final String partner : partners) {
            partnerNamesWithCommaSeperated.append(partner + ",");
        }
        final String partnersFromDB = partnerNamesWithCommaSeperated.substring(0,
                partnerNamesWithCommaSeperated.length() - 1);

        // When
        when(dashboardService.topTenTradingPartners()).thenReturn(partnersFromDB);

        // Then
        assertEquals(dashboardController.topTenTradingPartners(), partnersFromDB);
    }

    @Test
    public void shouldGetTopTpInboundCount() {
        // Given
        final StringBuilder partnerNamesWithCommaSeperated = new StringBuilder();
        final List<Long> topTpInboundCounts = new ArrayList<Long>();
        final List<String> partners = Arrays.asList("P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10");
        Long count = 1L;
        for (final String partner : partners) {
            partnerNamesWithCommaSeperated.append(partner + ",");
            topTpInboundCounts.add(count);
            count++;
        }

        // When
        when(dashboardService.topTpInboundCount(
                partnerNamesWithCommaSeperated.substring(0, partnerNamesWithCommaSeperated.length() - 1)))
                        .thenReturn(topTpInboundCounts);

        // Then
        assertEquals(
                dashboardController.topTpInboundCount(
                        partnerNamesWithCommaSeperated.substring(0, partnerNamesWithCommaSeperated.length() - 1)),
                topTpInboundCounts);
    }

    @Test
    public void shouldGetTopTpOutboundCount() {
        // Given
        final StringBuilder partnerNamesWithCommaSeperated = new StringBuilder();
        final List<Long> topTpOutboundCounts = new ArrayList<Long>();
        final List<String> partners = Arrays.asList("P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10");
        Long count = 1L;
        for (final String partner : partners) {
            partnerNamesWithCommaSeperated.append(partner + ",");
            topTpOutboundCounts.add(count);
            count++;
        }

        // When
        when(dashboardService.topTpOutboundCount(
                partnerNamesWithCommaSeperated.substring(0, partnerNamesWithCommaSeperated.length() - 1)))
                        .thenReturn(topTpOutboundCounts);

        // Then
        assertEquals(
                dashboardController.topTpOutboundCount(
                        partnerNamesWithCommaSeperated.substring(0, partnerNamesWithCommaSeperated.length() - 1)),
                topTpOutboundCounts);
    }

    @Test
    public void shouldGetMonthlyVolumes() throws JSONException {
        // Given
        final JSONArray monthlyVolumes = new JSONArray();
        final List<String> days = Arrays.asList("Aug 01", "Aug 02", "Aug 03");
        BigDecimal one = BigDecimal.ONE;
        BigDecimal ten = BigDecimal.TEN;
        for (final String day : days) {
            final JSONObject dailyVolumes = new JSONObject();
            dailyVolumes.put("day", day);
            dailyVolumes.put("ib", one.multiply(ten));
            dailyVolumes.put("ob", (one.add(new BigDecimal(2))).multiply(ten));
            monthlyVolumes.put(dailyVolumes);
        }
        final int userId = 0;
        // When
        when(dashboardService.monthlyVolumes(3, userId)).thenReturn(monthlyVolumes.toString());

        // Then
        assertEquals(dashboardController.monthlyVolumes(3, userId), monthlyVolumes.toString());
    }

    @Test
    public void shouldGetWarehouseVolumes() throws JSONException {
        // Given
        final Dashboard dashboard = buildDashboard();
        final JSONArray warehouseVolumes = new JSONArray();
        final List<String> warehouses = Arrays.asList("Warehouse A", "Warehouse B", "Warehouse C");
        BigDecimal one = BigDecimal.ONE;
        BigDecimal ten = BigDecimal.TEN;
        for (final String warehouse : warehouses) {
            final JSONObject volumes = new JSONObject();
            volumes.put("warehouse", warehouse);
            volumes.put("ib", one.multiply(ten));
            volumes.put("ob", (one.add(new BigDecimal(2))).multiply(ten));
            warehouseVolumes.put(volumes);
        }

        // When
        when(dashboardService.warehouseVolumes(dashboard)).thenReturn(warehouseVolumes.toString());

        // Then
        assertEquals(dashboardController.warehouseVolumes(dashboard), warehouseVolumes.toString());
    }

//    @Test
//    public void sholudGetgetDashBoardExcelPdfData() throws Exception {
//        // final DocumentRepository documentRepository = buildDocumentRepository();
//    	final Dashboard dashboard = buildDashboard();
////        final Map<String,Object> result=new HashMap<>();
//        List<DocumentRepository> documentRepositories = new ArrayList<>();
//        documentRepositories.add(buildDocumentRepository());
////        result.put("documentrepository", documentRepositories);
//        int count = 0;
//        final CustomResponse customResponse = new CustomResponse(documentRepositories,count);
//
//        // When
//        when(dashboardService.searchByTransactionGroup(dashboard)).thenReturn(customResponse);
//
//        // Then
//        assertEquals(dashboardController.searchByTransactionGroup(dashboard), customResponse);
//
//    }

    private DocumentRepository buildDocumentRepository() {
        List<String> filepath = new ArrayList<>();
        filepath.add("C:desktop/mscvP");
        filepath.add("C:desktop/mscvP1");

        List<String> adress = new ArrayList<>();
        adress.add("src/miracle.txt");
        adress.add("Miracle");
        return DocumentRepository.builder().id(1L).fileId("1").fileName("2").fileType(".txt").fileOrigin("india")
                .direction("INBOUND").ackFilePath("C:desktop/ mscvp").ackStatus("SUCESS")
                .dateTimeReceived("2022-09-22 20:22:59").errorMessage("ERROR").errorReportFilePath("C:desktop/ mscvp")
                .filePath(filepath).gsControlNumber("55").isaControlNumber("258").isaDate("2022-09-22 20:22:59")
                .isaTime("2022-09-22 20:22:59").orgFilePath("C:desktop/ mscvp").partnerName("AMRICOLD").poNumber("56")
                .postTransFilePath("C:desktop/ mscvp").preTransFilePath("C:desktop/ mscvp").primaryKeyType("PRIMARY")
                .primaryKeyValue("52").receiverId("22").receiverName("MIRACLE").reProcessStatus("SUCESS")
                .secondaryKeyType("SECONDARY").secondaryKeyValue("45").senderId("47").senderName("AMRICOLD")
                .shipmentId("40").status("SUCESS").stControlNumber("8").subject("REPORT").toAddress(adress)
                .transactionType("940").warehouse("MIRACLE").build();
    }

    private Dashboard buildDashboard() {
        List<String> warehouses = new ArrayList<>();
        warehouses.add("MockA");
        warehouses.add("MockB");
        return Dashboard.builder().tpId("All").docType("850").status("SUCCESS").warehouse(warehouses).downloadType("excel")
                .build();
    }

    @Test
    public void searchByTransactionGroup() throws Exception {
        // Given
    	final Dashboard dashboard = buildDashboard();
//        final Map<String,Object> result=new HashMap<>();
        final DocumentRepository documentRepository = DocumentRepository.builder().id(5635l).fileId("fieldId")
                .fileType("fileType").transactionType("transactionType").direction("direction").status("status")
                .ackStatus("ackStatus").dateTimeReceived("dateTimeReceived").build();
        final List<DocumentRepository> documents = Arrays.asList(documentRepository);
//        result.put("data", documents);
        int count = 0;
        final CustomResponse customResponse = new CustomResponse(documents,count);

        // When
        when(dashboardService.searchByTransactionGroup(dashboard)).thenReturn(customResponse);

        // Then
        assertEquals(customResponse, dashboardController.searchByTransactionGroup(dashboard));

    }
    
//    public class CustomResponse {
//    	private List<?> data;
//    	private int totalRecordsCount;
//    	public CustomResponse(List<?> data, int totalRecordsCount) {
//    		this.data=data;
//    		this.totalRecordsCount=totalRecordsCount;	
//    	}
//    }

    @Test
    public void getDashBoardExcelPdfData() throws Exception {

        // Given
        final Dashboard dashboard = Dashboard.builder().build();
        final Map<String, Object> getDashBoardExcelPdfDataTest = new HashMap<String, Object>();
        getDashBoardExcelPdfDataTest.put("Dashboard", getDashBoardExcelPdfDataTest);

        // When
        when(dashboardService.getDashBoardExcelPdfData(dashboard)).thenReturn(getDashBoardExcelPdfDataTest);

        // Then
        assertEquals(dashboardController.getDashBoardExcelPdfData(dashboard), getDashBoardExcelPdfDataTest);

    }

}
