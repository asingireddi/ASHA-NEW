package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.impl.EdiReportServiceImpl;

@Service
public class EdiReportService {
	@Autowired
	EdiReportServiceImpl ediReportServiceImpl;

	public List<Map<String, Object>> fetchEdiReport(final SearchCriteria searchCriteria) {
		return ediReportServiceImpl.fetchEdiReport(searchCriteria);
	}

	public ResponseEntity<byte[]> generateExcelResponse(SearchCriteria searchCriteria, String reportType)
			throws IOException {
		return ediReportServiceImpl.generateExcelResponse(searchCriteria, reportType);

	}
}