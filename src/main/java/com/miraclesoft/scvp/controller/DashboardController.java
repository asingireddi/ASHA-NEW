package com.miraclesoft.scvp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Dashboard;
import com.miraclesoft.scvp.service.DashboardService;

/**
 * The Class DashboardController.
 *
 * @author Narendar Geesidi
 */
@RestController
@RequestMapping("/dashboards")
public class DashboardController {

    /** The dashboard service. */
    @Autowired
    private DashboardService dashboardService;

    /**
     * Dashboard.
     *
     * @param dashboard the dashboard
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping("/dashboard")
    public String dashboard(@RequestBody final Dashboard dashboard) throws Exception {
        return dashboardService.dashboard(dashboard);
    }

    /**
     * Daily transactions.
     *
     * @return the string
     */
    @GetMapping("/dailyTransactions")
    public String dailyTransactions() {
        return dashboardService.dailyTransactions();
    }

    /**
     * Hourly volumes.
     *
     * @return the string
     */
    @GetMapping("/hourlyVolumes")
    public String hourlyVolumes() {
        return dashboardService.hourlyVolumes();
    }

    /**
     * Daily failure rate.
     *
     * @return the string
     */
    @GetMapping("/dailyFailureRate")
    public String dailyFailureRate() {
        return dashboardService.dailyFailureRate();
    }

    /**
     * Top ten trading partners.
     *
     * @return the string
     */
    @GetMapping("/topTenTradingPartners")
    public String topTenTradingPartners() {
        return dashboardService.topTenTradingPartners();
    }

    /**
     * Top tp inbound count.
     *
     * @param topTenTP the top ten TP
     * @return the list
     */
    @GetMapping("/topTpInboundCount/{topTenTP}")
    public List<Long> topTpInboundCount(@PathVariable final String topTenTP) {
        return dashboardService.topTpInboundCount(topTenTP);
    }

    /**
     * Top tp outbound count.
     *
     * @param topTenTP the top ten TP
     * @return the list
     */
    @GetMapping("/topTpOutboundCount/{topTenTP}")
    public List<Long> topTpOutboundCount(@PathVariable final String topTenTP) {
        return dashboardService.topTpOutboundCount(topTenTP);
    }

    /**
     * Monthly volumes.
     *
     * @param days   the days
     * @param userId the userId
     * @return the string
     */
    @GetMapping("/monthlyVolumes/{days}")
    public String monthlyVolumes(@PathVariable final int days,
            @RequestParam(required = false, defaultValue = "0") final int userId) {
        return dashboardService.monthlyVolumes(days, userId);
    }

    /**
     * Warehouse volumes.
     *
     * @param dashboard the dashboard
     * @return the string
     */
    @PostMapping("/warehouseVolumes")
    public String warehouseVolumes(@RequestBody final Dashboard dashboard) {
        return dashboardService.warehouseVolumes(dashboard);
    }

    /**
     * Search by transaction group.
     *
     * @param type the type
     * @return the list
     * @throws Exception the exception
     */
    @PostMapping("/byTransactionGroup")
    public CustomResponse searchByTransactionGroup(@RequestBody final Dashboard dashboard) throws Exception {
        return dashboardService.searchByTransactionGroup(dashboard);
    }

    /**
     * Gets the dash board excel data.
     *
     * @param dashboard the dashboard
     * @return the dash board excel data
     * @throws Exception the exception
     */
    @PostMapping("/downloadExcelPdfData")
    public Map<String, Object> getDashBoardExcelPdfData(@RequestBody final Dashboard dashboard) throws Exception {
        return dashboardService.getDashBoardExcelPdfData(dashboard);
    }
    
    @PostMapping("/downloadWarehouseVolumes")
    public Map<String, Object> download(@RequestBody final Dashboard dashboard) throws Exception {
        return dashboardService.download(dashboard);
    }

}
