package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.EdiReportService;

@RestController
public class EdiReportController {
	@Autowired
	EdiReportService ediReportService;

//	@GetMapping("/excel")
//	public ResponseEntity<byte[]> downloadExcel(@RequestParam(required = false) String partnerId,
//			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
//			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) throws IOException {
//		return ediReportService.generateExcelResponse(partnerId, fromDate, toDate);
//	}
	@PostMapping("/searchData")
	public List<Map<String, Object>> fetchEdiReport(@RequestBody SearchCriteria searchCriteria) {
		return ediReportService.fetchEdiReport(searchCriteria);
	}

	@PostMapping("/downloadExcel")
	public ResponseEntity<byte[]> generateExcelResponse(@RequestBody SearchCriteria searchCriteria,
			@RequestParam(required = false) String reportType) throws IOException {
		return ediReportService.generateExcelResponse(searchCriteria, reportType);
	}

}
