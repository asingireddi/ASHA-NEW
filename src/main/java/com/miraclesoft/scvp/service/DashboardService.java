package com.miraclesoft.scvp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Dashboard;
import com.miraclesoft.scvp.service.impl.DashboardServiceImpl;

/**
 * The Interface DashboardService.
 *
 * @author Narendar Geesidi
 */
@Service
public class DashboardService {

    /** The dashboard service impl. */
    @Autowired
    private DashboardServiceImpl dashboardServiceImpl;

    /**
     * Dashboard.
     *
     * @param dashboard the dashboard
     * @return the string
     * @throws Exception the exception
     */
    public String dashboard(final Dashboard dashboard) throws Exception {
        return dashboardServiceImpl.dashboard(dashboard);
    }

    /**
     * Daily transactions.
     *
     * @return the string
     */
    public String dailyTransactions() {
        return dashboardServiceImpl.dailyTransactions();
    }

    /**
     * Hourly volumes.
     *
     * @return the string
     */
    public String hourlyVolumes() {
        return dashboardServiceImpl.hourlyVolumes();
    }

    /**
     * Daily failure rate.
     *
     * @return the string
     */
    public String dailyFailureRate() {
        return dashboardServiceImpl.dailyFailureRate();
    }

    /**
     * Top tp inbound count.
     *
     * @param topTenTP the top ten TP
     * @return the list
     */
    public List<Long> topTpInboundCount(final String topTenTP) {
        return dashboardServiceImpl.topTpInboundCount(topTenTP);
    }

    /**
     * Top tp outbound count.
     *
     * @param topTenTP the top ten TP
     * @return the list
     */
    public List<Long> topTpOutboundCount(final String topTenTP) {
        return dashboardServiceImpl.topTpOutboundCount(topTenTP);
    }

    /**
     * Top ten trading partners.
     *
     * @return the string
     */
    public String topTenTradingPartners() {
        return dashboardServiceImpl.topTenTradingPartners();
    }

    /**
     * Monthly volumes.
     *
     * @param days   the days
     * @param userId the userId
     * @return the string
     */
    public String monthlyVolumes(final int days, final int userId) {
        return dashboardServiceImpl.monthlyVolumes(days, userId);
    }

    /**
     * Warehouse volumes.
     *
     * @param dashboard the dashboard
     * @return the string
     */
    public String warehouseVolumes(final Dashboard dashboard) {
        return dashboardServiceImpl.warehouseVolumes(dashboard);
    }

    /**
     * Search by transaction group.
     *
     * @param type the type
     * @return the list
     * @throws Exception the exception
     */
    public CustomResponse searchByTransactionGroup(final Dashboard dashboard) throws Exception {
        return dashboardServiceImpl.searchByTransactionGroup(dashboard);
    }

    /**
     * Gets the dash board excel pdf data.
     *
     * @param dashboard the dashboard
     * @return the dash board excel pdf data
     * @throws Exception the exception
     */
    public Map<String, Object> getDashBoardExcelPdfData(final Dashboard dashboard) throws Exception {
        return dashboardServiceImpl.getDashBoardExcelPdfData(dashboard);
    }

	public Map<String, Object> download(Dashboard dashboard) {
        return dashboardServiceImpl.download(dashboard);
	}
}
