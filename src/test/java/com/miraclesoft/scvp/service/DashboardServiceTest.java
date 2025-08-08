package com.miraclesoft.scvp.service;

import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Dashboard;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.service.impl.DashboardServiceImpl;
import static org.mockito.Mockito.when;
/**
 * The Test DashboardServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private DashboardServiceImpl dashboardServiceImpl;

    @Test
    public void shouldGetDashboard() throws Exception {
        // Given
        final Dashboard dashboard = buildDashboard();

        // When
        doReturn("Found logistic dashboard succesfully.").when(dashboardServiceImpl)
                                                         .dashboard(dashboard);

        // Then
        assertThat(dashboardService.dashboard(dashboard)).isEqualTo("Found logistic dashboard succesfully.");
    }

    @Test
    public void shouldGetDailyTransactions() {
        // When
        doReturn("Found edi daily stats.").when(dashboardServiceImpl)
                                          .dailyTransactions();

        // Then
        assertThat(dashboardService.dailyTransactions()).isEqualTo("Found edi daily stats.");
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
        doReturn(hourlyVolumes.toString()).when(dashboardServiceImpl)
                                          .hourlyVolumes();

        // Then
        assertThat(dashboardService.hourlyVolumes()).isEqualTo(hourlyVolumes.toString());
    }

    @Test
    public void shouldGetDocumentsDailyFailureRate() {
        // When
        doReturn("Found daily failure rates.").when(dashboardServiceImpl)
                                              .dailyFailureRate();

        // Then
        assertThat(dashboardService.dailyFailureRate()).isEqualTo("Found daily failure rates.");
    }

    @Test
    public void shouldFindTopTpEdiInboundCount() {
        // Given
        final List<Long> topTpDocumentsInboundCountList = Arrays.asList(1L, 2L);
        final String topTenTP = "PARTNER A,PARTNER B,PARTNER C,PARTNER D,PARTNER E,PARTNER F,PARTNER G,PARTNER H,PARTNER I,PARTNER J";

        // When
        doReturn(topTpDocumentsInboundCountList).when(dashboardServiceImpl)
                                                .topTpInboundCount(topTenTP);

        // Then
        assertThat(dashboardService.topTpInboundCount(topTenTP)).isEqualTo(topTpDocumentsInboundCountList);
    }

    @Test
    public void shouldFindTopTpDocumentsOutboundCount() {
        // Given
        final List<Long> topTpDocumentsOutboundCountList = Arrays.asList(1L, 2L);
        final String topTenTP = "PARTNER A,PARTNER B,PARTNER C,PARTNER D,PARTNER E,PARTNER F,PARTNER G,PARTNER H,PARTNER I,PARTNER J";

        // When
        doReturn(topTpDocumentsOutboundCountList).when(dashboardServiceImpl)
                                                 .topTpOutboundCount(topTenTP);

        // Then
        assertThat(dashboardService.topTpOutboundCount(topTenTP)).isEqualTo(topTpDocumentsOutboundCountList);
    }

    @Test
    public void shouldFindTopTenTradingPartners() {
        // When
        doReturn("Top ten logistic trading partners.").when(dashboardServiceImpl)
                                                      .topTenTradingPartners();

        // Then
        assertThat(dashboardService.topTenTradingPartners()).isEqualTo("Top ten logistic trading partners.");
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

        // When
        final int userId = 0;
        doReturn(monthlyVolumes.toString()).when(dashboardServiceImpl)
                                           .monthlyVolumes(3,userId);

        // Then
        assertThat(dashboardService.monthlyVolumes(3,userId)).isEqualTo(monthlyVolumes.toString());
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
        doReturn(warehouseVolumes.toString()).when(dashboardServiceImpl)
                                             .warehouseVolumes(dashboard);

        // Then
        assertEquals(dashboardService.warehouseVolumes(dashboard), warehouseVolumes.toString());
    }

    private Dashboard buildDashboard() {
        List<String> warehouses = new ArrayList<>();
        warehouses.add("MockA");
        warehouses.add("MockB");
        return Dashboard.builder()
                        .tpId("All")
                        .docType("850")
                        .status("SUCCESS")
                        .warehouse(warehouses)
                        .downloadType("excel")
                        .build();
    }
    @Test
    public void searchByTransactionGroupTest()throws Exception{

    //Given
    final Dashboard dashboard = buildDashboard();
    final DocumentRepository documentRepository=DocumentRepository.builder()
    .id(5635l)
    .fileId("fieldId")
    .fileType("fileType")
    .transactionType("transactionType")
    .direction("direction")
    .status("status")
    .ackStatus("ackStatus")
    .dateTimeReceived("dateTimeReceived")
    .build();
    final List<DocumentRepository> documentRepositories = new ArrayList<DocumentRepository>();
    documentRepositories.add(documentRepository);
    final int count = 0;
    final CustomResponse customResponse = new CustomResponse(documentRepositories, count);

    //When
    when(dashboardService.searchByTransactionGroup(dashboard)).thenReturn(customResponse);

    //Then
    assertEquals(customResponse, dashboardService.searchByTransactionGroup(dashboard));
    }

    @Test
    public void getDashBoardExcelPdfDataTest() throws Exception {

    //Given
    final Dashboard dashboard=Dashboard.builder().build();
    final Map<String,Object> getDashBoardExcelPdfDataTest=new HashMap<String,Object>();
    getDashBoardExcelPdfDataTest.put("Dashboard", getDashBoardExcelPdfDataTest);

    //When
    when(dashboardService.getDashBoardExcelPdfData(dashboard)).thenReturn(getDashBoardExcelPdfDataTest);

    //Then
    assertEquals(dashboardService.getDashBoardExcelPdfData(dashboard),getDashBoardExcelPdfDataTest);

    }
    
}
