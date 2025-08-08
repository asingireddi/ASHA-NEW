package com.miraclesoft.scvp.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.SearchCriteria;

@Component
public class EdiReportServiceImpl {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> fetchEdiReport(SearchCriteria searchCriteria) {
		List<Object> params = new ArrayList<>();
		params.add(searchCriteria.getFromDate()); // first ?
		params.add(searchCriteria.getToDate());

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT partner_id, correlation_key1, status, ")
				.append("SUM(CASE WHEN cnt_col = '850d' THEN 1 ELSE 0 END) AS dataLoad850Orders, ")
				.append("SUM(CASE WHEN cnt_col = '850e' THEN 1 ELSE 0 END) AS EDI850Orders, ")
				.append("SUM(CASE WHEN cnt_col = '850ODe' THEN 1 ELSE 0 END) AS EDI850OrderLines, ")
				.append("SUM(CASE WHEN cnt_col = '860e' THEN 1 ELSE 0 END) AS EDI860OrderChanges, ")
				.append("SUM(CASE WHEN cnt_col = '855c' THEN 1 ELSE 0 END) AS EDI855OrderAck, ")
				.append("SUM(CASE WHEN cnt_col = '856c' THEN 1 ELSE 0 END) AS EDI856ShipmentNotice, ")
				.append("SUM(CASE WHEN cnt_col = '810c' THEN 1 ELSE 0 END) AS EDI810Invoice ").append("FROM ( ")

				.append("SELECT partner_id, correlation_key1, transaction_type, recieved_date, status, ")
				.append("CASE ").append("WHEN transaction_type = 850 AND file_location LIKE 'Dummy' THEN '850d' ")
				.append("WHEN transaction_type = 850 AND file_location NOT LIKE 'Dummy' THEN '850e' ")
				.append("WHEN transaction_type = 860 AND file_location IS NOT NULL THEN '860e' ")
				.append("END AS cnt_col ").append("FROM inbox ").append("WHERE recieved_date BETWEEN ? AND ? ");

		if (searchCriteria.getPartnerId() != null && !searchCriteria.getPartnerId().isEmpty()) {
			System.out.println(searchCriteria.getPartnerId() + " partnerId");
			sql.append("AND partner_id = ? ");
			params.add(searchCriteria.getPartnerId());
		}

		sql.append("UNION ALL ")
				.append("SELECT partner_id, correlation_key1, transaction_type, recieved_date, status, ")
				.append("CASE ").append("WHEN transaction_type = '855' THEN '855c' ")
				.append("WHEN transaction_type = '810' THEN '810c' ")
				.append("WHEN transaction_type = '856' THEN '856c' ").append("END AS cnt_col ")
				.append("FROM outbox WHERE 1=1 "); // Ensure WHERE clause starts correctly

		if (searchCriteria.getPartnerId() != null && !searchCriteria.getPartnerId().isEmpty()) {
			sql.append("AND partner_id = ? ");
			params.add(searchCriteria.getPartnerId()); // ✅ Added for OUTBOX
		}

		sql.append("UNION ALL ").append("SELECT partner_id, correlation_key1, order_type, po_line_number, changeDate, ")
				.append("CASE WHEN order_type = '850' OR order_type = '860' THEN '850ODe' END AS cnt_col ")
				.append("FROM order_details WHERE 1=1 "); // Ensure WHERE clause starts correctly

		if (searchCriteria.getPartnerId() != null && !searchCriteria.getPartnerId().isEmpty()) {
			sql.append("AND partner_id = ? ");
			params.add(searchCriteria.getPartnerId());
		}

		sql.append(") a ").append("WHERE correlation_key1 NOT LIKE '%-ORPH' ")
				.append("GROUP BY status, partner_id, correlation_key1");

		// if (searchCriteria.getPartnerId() != null &&
		// !searchCriteria.getPartnerId().isEmpty()) {
		return jdbcTemplate.queryForList(sql.toString(), params.toArray());
		// }
		// else {

		// return jdbcTemplate.queryForList(sql.toString(),
		// LocalDateTime.parse(searchCriteria.getFromDate(), fmt),
		// searchCriteria.getToDate());
		// }
	}

	public ResponseEntity<byte[]> generateExcelResponse(SearchCriteria searchCriteria, String reportType)
			throws IOException {

//		if (!"webformsReport".equalsIgnoreCase(reportType)) {
//			String jsonError = "{\"status\":\"error\",\"message\":\"Invalid reportType. Only 'webformsReport' is supported.\"}";
//			byte[] errorBytes = jsonError.getBytes(StandardCharsets.UTF_8);
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON); // 👈 return JSON content
//			return ResponseEntity.badRequest().headers(headers).body(errorBytes);
//		}

		List<Map<String, Object>> data = fetchEdiReport(searchCriteria);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("EDI Report");

		String[] columns = { "partner_id", "correlation_key1", "status", "dataLoad850Orders", "EDI850Orders",
				"EDI850OrderLines", "EDI860OrderChanges", "EDI855OrderAck", "EDI856ShipmentNotice", "EDI810Invoice" };

		// Header row
		XSSFRow header = sheet.createRow(0);
		for (int c = 0; c < columns.length; c++) {
			header.createCell(c).setCellValue(columns[c]);
		}

		// Data rows
		int r = 1;
		for (Map<String, Object> map : data) {
			XSSFRow row = sheet.createRow(r++);
			for (int c = 0; c < columns.length; c++) {
				Object v = map.get(columns[c]);
				row.createCell(c).setCellValue(v != null ? v.toString() : "");
			}
		}

		workbook.write(out);
		workbook = null;

		HttpHeaders hdr = new HttpHeaders();
		hdr.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm"));
		hdr.setContentDispositionFormData("attachment", "edi-report_" + timestamp + ".xlsx");

		return ResponseEntity.ok().headers(hdr).body(out.toByteArray());
	}

}