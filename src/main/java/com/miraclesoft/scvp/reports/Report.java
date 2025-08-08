package com.miraclesoft.scvp.reports;

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.model.Sfgpartner;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class Report.
 *
 * @author Narendar Geesidi
 */
@Component
public class Report {

	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	/** The documents creation path. */
	@Value("${documentPath}")
	private String documentPath;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(Report.class.getName());

	/** The Constant SMALL_COLUMN_SIZE. */
	private static final int SMALL_COLUMN_SIZE = 3000;

	/** The Constant MEDIUM_COLUMN_SIZE. */
	private static final int MEDIUM_COLUMN_SIZE = 5000;

	/** The Constant LARGE_COLUMN_SIZE. */
	private static final int LARGE_COLUMN_SIZE = 10000;

	/** The Constant ONE. */
	private static final int ONE = 1;

	/** The Constant TWO. */
	private static final int TWO = 2;

	/** The Constant THREE. */
	private static final int THREE = 3;

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant FIVE. */
	private static final int FIVE = 5;

	/** The Constant SIX. */
	private static final int SIX = 6;

	/** The Constant SEVEN. */
	private static final int SEVEN = 7;

	/** The Constant EIGHT. */
	private static final int EIGHT = 8;

	/** The Constant NINE. */
	private static final int NINE = 9;

	/** The Constant TEN. */
	private static final int TEN = 10;

	/** The Constant ELEVEN. */
	private static final int ELEVEN = 11;

	/** The Constant TWELVE. */
	private static final int TWELVE = 12;

	/** The Constant THIRTEEN. */
	private static final int THIRTEEN = 13;

	/** The Constant FOURTEEN. */
	private static final int FOURTEEN = 14;

	private static final int FIFTEEN = 15;

	private static final int SIXTEEN = 16;

	private static final int SEVENTEEN = 17;

	private static final int EIGHTEEN = 18;

	private static final int NINETEEN = 19;
	// CHECKSTYLE:ON

	/**
	 * Document repository excel download.
	 *
	 * @param documents the documents
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String documentRepositoryExcelDownload(final List<DocumentRepository> documents) throws IOException {
		final String filePath = getFilePath("DocumentRepository.xlsx");
		try {
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("DocumentRepository");
			XSSFRow row;
			if (!documents.isEmpty()) {
				final XSSFCellStyle greenFontCellStyle = greenFontCellStyle(workbook);
				final XSSFCellStyle redFontCellStyle = redFontCellStyle(workbook);
				final XSSFCellStyle orangeFontCellStyle = orangeFontCellStyle(workbook);

				row = worksheet.createRow(0);
				row = worksheet.createRow(ONE);
				final Cell cell = row.createCell(ONE);
				cell.setCellValue("DocumentRepository records on " + CurrDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:T2"));

				row = worksheet.createRow(THREE);
				final List<String> headers = getDocumentRepositoryHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}

				for (int i = 0; i < documents.size(); i++) {
					final DocumentRepository document = documents.get(i);
					row = worksheet.createRow(i + FOUR);
					row.createCell(ONE).setCellValue(document.getDateTimeReceived());
					row.createCell(TWO).setCellValue(document.getFileId());
					row.createCell(THREE).setCellValue(document.getShipmentId());
					row.createCell(FOUR).setCellValue(document.getPrimaryKeyValue());
					row.createCell(FIVE).setCellValue(document.getParentWarehouse());
					row.createCell(SIX).setCellValue(document.getWarehouse());
					row.createCell(SEVEN).setCellValue(document.getPartnerName());
					row.createCell(EIGHT).setCellValue(document.getTransactionType());
					final XSSFCell cellI1 = row.createCell(NINE);
					if (nonNull(document.getStatus())) {
						cellI1.setCellValue(document.getStatus().toUpperCase());
						if (document.getStatus().equalsIgnoreCase("SUCCESS")
								|| document.getStatus().equalsIgnoreCase("DROPPED")) {
							cellI1.setCellStyle(greenFontCellStyle);
						} else if (document.getStatus().equalsIgnoreCase("ERROR")) {
							cellI1.setCellStyle(redFontCellStyle);
						} else {
							cellI1.setCellStyle(orangeFontCellStyle);
						}
					}
					row.createCell(TEN).setCellValue(document.getIsaControlNumber());
					row.createCell(ELEVEN).setCellValue(document.getDirection());
					row.createCell(TWELVE).setCellValue(document.getFileType());
					final XSSFCell cellM1 = row.createCell(THIRTEEN);
					if (nonNull(document.getAckStatus())) {
						cellM1.setCellValue(document.getAckStatus().toUpperCase());
						if (document.getAckStatus().equalsIgnoreCase("Accepted")) {
							cellM1.setCellStyle(greenFontCellStyle);
						} else if (document.getAckStatus().equalsIgnoreCase("Rejected")) {
							cellM1.setCellStyle(redFontCellStyle);
						} else {
							cellM1.setCellStyle(orangeFontCellStyle);
						}
					}
					row.createCell(FOURTEEN).setCellValue(document.getReProcessStatus());
					row.createCell(FIFTEEN).setCellValue(document.getSenderId());
					row.createCell(SIXTEEN).setCellValue(document.getFileName());
					row.createCell(SEVENTEEN).setCellValue(document.getPostTransFileName());
					row.createCell(EIGHTEEN).setCellValue(document.getGsControlNumber());
					row.createCell(NINETEEN).setCellValue(document.getStControlNumber());

				}
			}
			worksheet.setColumnWidth(ONE, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(TWO, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(THREE, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FOUR, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FIVE, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(SIX, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(SEVEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(EIGHT, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(NINE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(TEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(ELEVEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(TWELVE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(THIRTEEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(FOURTEEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FIFTEEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(SIXTEEN, LARGE_COLUMN_SIZE);
			worksheet.setColumnWidth(SEVENTEEN, LARGE_COLUMN_SIZE);
			worksheet.setColumnWidth(EIGHTEEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(NINETEEN, SMALL_COLUMN_SIZE);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException fileNotFoundException) {
			logger.log(Level.ERROR, "FileNotFoundException occurred in mDocRepositoryExcelDownload :: "
					+ fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			logger.log(Level.ERROR,
					"IOException occurred in mDocRepositoryExcelDownload :: " + ioException.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, "Exception occurred in mDocRepositoryExcelDownload :: " + exception.getMessage());
		}
		return filePath;
	}

	/**
	 * Excel report download.
	 *
	 * @param documents the documents
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String excelReportDownload(final List<DocumentRepository> documents) throws IOException {
		final String filePath = getFilePath("ExcelReport.xlsx");
		try {
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("ExcelReport");
			XSSFRow row;
			if (!documents.isEmpty()) {
				final XSSFCellStyle greenFontCellStyle = greenFontCellStyle(workbook);
				final XSSFCellStyle redFontCellStyle = redFontCellStyle(workbook);
				final XSSFCellStyle orangeFontCellStyle = orangeFontCellStyle(workbook);

				row = worksheet.createRow(0);
				row = worksheet.createRow(ONE);
				final Cell cell = row.createCell(ONE);
				cell.setCellValue("Excel Report on : " + CurrDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:J2"));

				row = worksheet.createRow(THREE);
				final List<String> headers = getExcelReportHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}

				for (int i = 0; i < documents.size(); i++) {
					final DocumentRepository doc = documents.get(i);
					row = worksheet.createRow(i + FOUR);
					row.createCell(ONE).setCellValue(doc.getFileId());
					row.createCell(TWO).setCellValue(doc.getFileType());
					row.createCell(THREE).setCellValue(doc.getPartnerName());
					row.createCell(FOUR).setCellValue(doc.getDateTimeReceived());
					row.createCell(FIVE).setCellValue(doc.getTransactionType());
					row.createCell(SIX).setCellValue(doc.getDirection());
					final XSSFCell cellG1 = row.createCell(SEVEN);
					if (nonNull(doc.getStatus())) {
						cellG1.setCellValue(doc.getStatus().toUpperCase());
						if (doc.getStatus().equalsIgnoreCase("SUCCESS")) {
							cellG1.setCellStyle(greenFontCellStyle);
						} else if (doc.getStatus().equalsIgnoreCase("ERROR")) {
							cellG1.setCellStyle(redFontCellStyle);
						} else {
							cellG1.setCellStyle(orangeFontCellStyle);
						}
					}
					row.createCell(EIGHT).setCellValue(doc.getReProcessStatus());
					row.createCell(NINE).setCellValue(doc.getErrorMessage());
				}
			}
			worksheet.setColumnWidth(ONE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(TWO, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(THREE, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FOUR, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FIVE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SIX, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SEVEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(EIGHT, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(NINE, LARGE_COLUMN_SIZE);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException fileNotFoundException) {
			logger.log(Level.ERROR,
					"FileNotFoundException occurred in mReportsExcelDownload :: " + fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			logger.log(Level.ERROR, "IOException occurred in mReportsExcelDownload :: " + ioException.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, "Exception occurred in mReportsExcelDownload :: " + exception.getMessage());
		}
		return filePath;
	}

	/**
	 * Warehouse order excel download.
	 *
	 * @param warehouseOrders the warehouse orders
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String warehouseOrderExcelDownload(final List<WarehouseOrder> warehouseOrders) throws IOException {
		final String filePath = getFilePath("PurchaseOrder.xlsx");
		try {
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("PurchaseOrder");
			XSSFRow row;
			if (!warehouseOrders.isEmpty()) {
				final XSSFCellStyle greenFontCellStyle = greenFontCellStyle(workbook);
				final XSSFCellStyle redFontCellStyle = redFontCellStyle(workbook);
				final XSSFCellStyle orangeFontCellStyle = orangeFontCellStyle(workbook);

				row = worksheet.createRow(0);
				row = worksheet.createRow(ONE);
				final Cell cell = row.createCell(ONE);
				cell.setCellValue("Warehouse Order records on " + CurrDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:Q2"));

				row = worksheet.createRow(THREE);
				final List<String> headers = getWarehouseOrderHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}

				for (int i = 0; i < warehouseOrders.size(); i++) {
					final WarehouseOrder warehouseOrder = warehouseOrders.get(i);
					row = worksheet.createRow(i + FOUR);
					row.createCell(ONE).setCellValue(warehouseOrder.getDateTimeReceived());
					row.createCell(TWO).setCellValue(warehouseOrder.getFileId());
					row.createCell(THREE).setCellValue(warehouseOrder.getDepositorOrderNumber());
					row.createCell(FOUR).setCellValue(warehouseOrder.getTransactionType());
					final XSSFCell cellF1 = row.createCell(FIVE);
					if (nonNull(warehouseOrder.getStatus())) {
						cellF1.setCellValue(warehouseOrder.getStatus().toUpperCase());
						if (warehouseOrder.getStatus().equalsIgnoreCase("SUCCESS")
								|| warehouseOrder.getStatus().equalsIgnoreCase("DROPPED")) {
							cellF1.setCellStyle(greenFontCellStyle);
						} else if (warehouseOrder.getStatus().equalsIgnoreCase("ERROR")) {
							cellF1.setCellStyle(redFontCellStyle);
						} else {
							cellF1.setCellStyle(orangeFontCellStyle);
						}
					}
					row.createCell(SIX).setCellValue(warehouseOrder.getIsaControlNumber());
					row.createCell(SEVEN).setCellValue(warehouseOrder.getPartnerName());
					row.createCell(EIGHT).setCellValue(warehouseOrder.getDirection());
					final XSSFCell cellJ1 = row.createCell(NINE);
					if (nonNull(warehouseOrder.getAckStatus())) {
						cellJ1.setCellValue(warehouseOrder.getAckStatus().toUpperCase());
						if (warehouseOrder.getAckStatus().equalsIgnoreCase("Accepted")) {
							cellJ1.setCellStyle(greenFontCellStyle);
						} else if (warehouseOrder.getAckStatus().equalsIgnoreCase("Rejected")) {
							cellJ1.setCellStyle(redFontCellStyle);
						} else {
							cellJ1.setCellStyle(orangeFontCellStyle);
						}
					}
					row.createCell(TEN).setCellValue(warehouseOrder.getParentWarehouse());
					row.createCell(ELEVEN).setCellValue(warehouseOrder.getWarehouse());
					row.createCell(TWELVE).setCellValue(warehouseOrder.getFileName());
					row.createCell(THIRTEEN).setCellValue(warehouseOrder.getPostTransFileName());
					row.createCell(FOURTEEN).setCellValue(warehouseOrder.getReProcessStatus());
					row.createCell(FIFTEEN).setCellValue(warehouseOrder.getGsControlNumber());
					row.createCell(SIXTEEN).setCellValue(warehouseOrder.getStControlNumber());
				}
			}
			worksheet.setColumnWidth(ONE, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(TWO, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(THREE, LARGE_COLUMN_SIZE);
			worksheet.setColumnWidth(FOUR, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(FIVE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SIX, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SEVEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(EIGHT, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(NINE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(TEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(ELEVEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(TWELVE, LARGE_COLUMN_SIZE);
			worksheet.setColumnWidth(THIRTEEN, LARGE_COLUMN_SIZE);
			worksheet.setColumnWidth(FOURTEEN, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FIFTEEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SIXTEEN, SMALL_COLUMN_SIZE);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException fileNotFoundException) {
			logger.log(Level.ERROR, "FileNotFoundException occurred in warehouseOrderExcelDownload :: "
					+ fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			logger.log(Level.ERROR,
					"IOException occurred in warehouseOrderExcelDownload :: " + ioException.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, "Exception occurred in warehouseOrderExcelDownload :: " + exception.getMessage());
		}
		return filePath;
	}

	/**
	 * Gets the file path.
	 *
	 * @param type the type
	 * @return the file path
	 */
	private String getFilePath(final String type) {
		final File file = new File(documentPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath() + File.separator + type;
	}

	/**
	 * Gets the document repository headers.
	 *
	 * @return the document repository headers
	 */
	private List<String> getDocumentRepositoryHeaders() {
		return Arrays.asList("Date Time", "Instance Id", "Shipment", "Document #", "WMS", "Warehouse", "Partner",
				"Transaction", "Status", "ISA CTRL", "Direction", "File format", "Ack Status", "Reprocess", "Sender Id",
				"Pre translation file name", "Post translation file name", "GS CONTROL NUMBER", "ST CONTROL NUMBER");
	}

	/**
	 * Gets the excel report headers.
	 *
	 * @return the excel report headers
	 */
	private List<String> getExcelReportHeaders() {
		return Arrays.asList("Instance Id", "File format", "Partner", "Date", "Transaction", "Direction", "Status",
				"Reprocess", "Error message");
	}

	/**
	 * Gets the warehouse order headers.
	 *
	 * @return the warehouse order headers
	 */
	private List<String> getWarehouseOrderHeaders() {
		return Arrays.asList("Date Time", "Instance Id", "Document #", "Transaction", "Status", "ISA CTRL", "Partner",
				"Direction", "Ack Status", "WMS", "Warehouse", "Pre translation file name",
				"Post translation file name", "Reprocess", "GS CONTROL NUMBER", "ST CONTROL NUMBER");
	}

	private List<String> getPartnerDataHeaders() {
		return Arrays.asList("Serial Num", "Name", "Partner Id", "Country", "Status", "Created on");
	}

	private List<String> getSfgPartnerDataHeaders() {
		return Arrays.asList("Serial Num", "Name", "Country", "Status", "Created on");
	}

	/**
	 * Page header cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle pageHeaderCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle pageHeaderCellStyle = workbook.createCellStyle();
		final XSSFFont pageHeaderFont = workbook.createFont();
		pageHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		pageHeaderCellStyle.setFont(pageHeaderFont);
		pageHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		pageHeaderCellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		pageHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		return pageHeaderCellStyle;
	}

	/**
	 * Table header cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle tableHeaderCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle tableHeaderCellStyle = workbook.createCellStyle();
		final XSSFFont tableHeaderFont = workbook.createFont();
		tableHeaderFont.setColor(HSSFColor.WHITE.index);
		tableHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		tableHeaderCellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		tableHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		tableHeaderCellStyle.setFont(tableHeaderFont);
		return tableHeaderCellStyle;
	}

	/**
	 * Green font cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle greenFontCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle greenFontCellStyle = workbook.createCellStyle();
		final XSSFFont greenFont = workbook.createFont();
		greenFont.setColor(HSSFColor.GREEN.index);
		greenFontCellStyle.setFont(greenFont);
		return greenFontCellStyle;
	}

	/**
	 * Red font cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle redFontCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle redFontCellStyle = workbook.createCellStyle();
		final XSSFFont redFont = workbook.createFont();
		redFont.setColor(HSSFColor.RED.index);
		redFontCellStyle.setFont(redFont);
		return redFontCellStyle;
	}

	/**
	 * Orange font cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle orangeFontCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle orangeFontCellStyle = workbook.createCellStyle();
		final XSSFFont orangeFont = workbook.createFont();
		orangeFont.setColor(HSSFColor.ORANGE.index);
		orangeFontCellStyle.setFont(orangeFont);
		return orangeFontCellStyle;
	}

	/**
	 * Gets the dash board excel pdf data.
	 *
	 * @param inOutData the inOutData
	 * @return the dash board excel pdf data
	 */
	public Map<String, Object> dashBoardExcelPdfData(final String inOutData) {
		final List<Object> inboundData = new ArrayList<Object>();
		final List<Object> outboundData = new ArrayList<Object>();
		Map<String, Object> response = new HashMap<>();
		try {
			logger.log(Level.INFO, "\n======> inOutData " + inOutData);
			final JSONObject jSONObject = new JSONObject(inOutData);
			final JSONArray array = jSONObject.getJSONArray("inbound");
			for (int i = 0; i < array.length(); i++) {
				List<Object> innerList = new ArrayList<>();
				innerList.add(array.getJSONObject(i).getString("name"));
//                innerList.add(Integer.toString(array.getJSONObject(i).getInt("y")));
				innerList.add(array.getJSONObject(i).getInt("count"));
				inboundData.add(innerList);
			}
			final JSONArray array1 = jSONObject.getJSONArray("outbound");
			for (int i = 0; i < array1.length(); i++) {
				List<Object> innerList = new ArrayList<>();
				innerList.add(array1.getJSONObject(i).getString("name"));
//                innerList.add(Integer.toString(array1.getJSONObject(i).getInt("y")));
				innerList.add(array1.getJSONObject(i).getInt("count"));
				outboundData.add(innerList);
			}
			response.put("inbound", inboundData);
			response.put("outbound", outboundData);

		} catch (Exception exception) {
			exception.printStackTrace();
			logger.log(Level.ERROR, "Exception occurred in dashBoardExcelPdfData :: " + exception.getMessage());
		}
		return response;
	}

	public String downloadPartnersData(List<Partner> findAll) throws IOException {
		final String filePath = getFilePath("PartnersData.xlsx");
//        final Partner result = (Partner) findAll.get("data");
		try {
			int serialNumber = 1;
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("PartnersData");
			XSSFRow row;
			if (!findAll.isEmpty()) {
				row = worksheet.createRow(ONE);
				final Cell cell = row.createCell(ONE);
				cell.setCellValue("Partners list on " + CurrDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:G2"));
				row = worksheet.createRow(THREE);
				final List<String> headers = getPartnerDataHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}
				for (int i = 0; i < findAll.size(); i++) {
					final Partner partner = findAll.get(i);
					row = worksheet.createRow(i + FOUR);
					row.createCell(ONE).setCellValue(serialNumber++);
					row.createCell(TWO).setCellValue(partner.getPartnerName());
					row.createCell(THREE).setCellValue(partner.getPartnerIdentifier());
					row.createCell(FOUR).setCellValue(partner.getCountryCode());
					row.createCell(FIVE).setCellValue(partner.getStatus());
					row.createCell(SIX).setCellValue(partner.getCreatedDate());
				}
				worksheet.setColumnWidth(ONE, SMALL_COLUMN_SIZE);
				worksheet.setColumnWidth(TWO, LARGE_COLUMN_SIZE);
				worksheet.setColumnWidth(THREE, MEDIUM_COLUMN_SIZE);
				worksheet.setColumnWidth(FOUR, SMALL_COLUMN_SIZE);
				worksheet.setColumnWidth(FIVE, SMALL_COLUMN_SIZE);
				worksheet.setColumnWidth(SIX, MEDIUM_COLUMN_SIZE);
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			}
		} catch (FileNotFoundException fileNotFoundException) {
			logger.log(Level.ERROR, "FileNotFoundException occurred in listOfPartnersDownload :: "
					+ fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			logger.log(Level.ERROR, "IOException occurred in listOfPartnersDownload :: " + ioException.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, "Exception occurred in listOfPartnersDownload :: " + exception.getMessage());
		}
		return filePath;
	}

	public String downloadSfgPartnersData(List<Sfgpartner> findAll) throws IOException {
		final String filePath = getFilePath("SfgPartnersData.xlsx");
		try {
			int serialNumber = 1;
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("SfgPartnersData");
			XSSFRow row;
			if (!findAll.isEmpty()) {
				row = worksheet.createRow(ONE);
				final Cell cell = row.createCell(ONE);
				cell.setCellValue("SFG partners list on " + CurrDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:F2"));
				row = worksheet.createRow(THREE);
				final List<String> headers = getSfgPartnerDataHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}
				for (int i = 0; i < findAll.size(); i++) {
					final Sfgpartner sfgpartner = findAll.get(i);
					row = worksheet.createRow(i + FOUR);
					row.createCell(ONE).setCellValue(serialNumber++);
					row.createCell(TWO).setCellValue(sfgpartner.getSfgPartnerName());
					row.createCell(THREE).setCellValue(sfgpartner.getSfgCountryCode());
					row.createCell(FOUR).setCellValue(sfgpartner.getStatus());
					row.createCell(FIVE).setCellValue(sfgpartner.getCreatedDate());
				}
				worksheet.setColumnWidth(ONE, SMALL_COLUMN_SIZE);
				worksheet.setColumnWidth(TWO, LARGE_COLUMN_SIZE);
				worksheet.setColumnWidth(THREE, SMALL_COLUMN_SIZE);
				worksheet.setColumnWidth(FOUR, SMALL_COLUMN_SIZE);
				worksheet.setColumnWidth(FIVE, MEDIUM_COLUMN_SIZE);
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			}
		} catch (FileNotFoundException fileNotFoundException) {
			logger.log(Level.ERROR, "FileNotFoundException occurred in listOfSfgPartnersDownload :: "
					+ fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			logger.log(Level.ERROR, "IOException occurred in listOfSfgPartnersDownload :: " + ioException.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, "Exception occurred in listOfSfgPartnersDownload :: " + exception.getMessage());
		}
		return filePath;
	}

	public String tradingPartnerExcelDownload(List<Map<String, Object>> data) throws IOException {
		final String filePath = getFilePath("TradingPartner.xlsx");
		try {
			String currDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("TradingPartner");
			XSSFRow row;

			if (!data.isEmpty()) {
				final XSSFCellStyle greenFontCellStyle = greenFontCellStyle(workbook);
				final XSSFCellStyle redFontCellStyle = redFontCellStyle(workbook);
				final XSSFCellStyle orangeFontCellStyle = orangeFontCellStyle(workbook);

				row = worksheet.createRow(0);
				row = worksheet.createRow(1);
				final Cell cell = row.createCell(1);
				cell.setCellValue("Trading Partner records on " + currDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:T2"));

				row = worksheet.createRow(3);
				final List<String> headers = getTradingPartnerHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}

				for (int i = 0; i < data.size(); i++) {
					final Map<String, Object> record = data.get(i);
					row = worksheet.createRow(i + 4);

					row.createCell(1).setCellValue((String) record.get("TC_DIRECTION"));
					row.createCell(2).setCellValue((String) record.get("TC_COMPANY_CODE"));
					row.createCell(3).setCellValue((String) record.get("TC_DIVISION"));
					row.createCell(4).setCellValue((String) record.get("TC_PURCH_ORG"));
					row.createCell(5).setCellValue((String) record.get("TC_SAP_ID_QUALIF"));
					row.createCell(6).setCellValue((String) record.get("TC_SAP_ID"));
					row.createCell(7).setCellValue((String) record.get("TC_IDOC_TYPE"));
					row.createCell(8).setCellValue((String) record.get("TC_MESSAGE_CODE"));
					row.createCell(9).setCellValue((String) record.get("TC_MESSAGE_PORT"));
					row.createCell(10).setCellValue((String) record.get("TC_MESSAGE_FUNCTION"));
					row.createCell(11).setCellValue((String) record.get("TC_MESSAGE_TYPE"));
					row.createCell(12).setCellValue((String) record.get("TC_VERSION"));
					row.createCell(13).setCellValue((String) record.get("TC_REHLKO_ISA_ID_QUALF"));
					row.createCell(14).setCellValue((String) record.get("TC_REHLKO_ISA_ID"));
					row.createCell(15).setCellValue((String) record.get("TC_REHLKO_GS"));
					row.createCell(16).setCellValue((String) record.get("TC_PARTNER_ISA_ID_QUALF"));
					row.createCell(17).setCellValue((String) record.get("TC_PARTNER_ISA_ID"));
					row.createCell(18).setCellValue((String) record.get("TC_PARTNER_GS"));
					row.createCell(19).setCellValue((String) record.get("TC_GS_FunctionalCode"));
					row.createCell(20).setCellValue((String) record.get("TC_MapName"));
					row.createCell(21).setCellValue((String) record.get("TC_PartnerName"));
					row.createCell(22).setCellValue((String) record.get("TC_ListVersion"));
					row.createCell(23).setCellValue((String) record.get("TC_Stream"));
					row.createCell(24).setCellValue((String) record.get("TC_DivisionName"));
					row.createCell(25).setCellValue((String) record.get("FlowType"));
					row.createCell(26).setCellValue((String) record.get("TC_TransactionCode"));
					row.createCell(27).setCellValue((String) record.get("TC_DeliveryType"));
					row.createCell(28).setCellValue((String) record.get("Active"));
					row.createCell(29).setCellValue((String) record.get("Test_REHLKO_ID"));
					row.createCell(30).setCellValue((String) record.get("GenericSID"));
					row.createCell(31).setCellValue((String) record.get("GenericRID"));
					
					
					
					// Status with Conditional Formatting
					final XSSFCell statusCell = row.createCell(5);
					String activeStatus = (String) record.get("TC_Active");
					if (activeStatus != null) {
						statusCell.setCellValue(activeStatus.toUpperCase());
						if ("A".equalsIgnoreCase(activeStatus)) {
							statusCell.setCellStyle(greenFontCellStyle);
						} else if ("I".equalsIgnoreCase(activeStatus)) {
							statusCell.setCellStyle(redFontCellStyle);
						} else {
							statusCell.setCellStyle(orangeFontCellStyle);
						}
					}
				}
			}

			// Adjust Column Widths
			worksheet.setColumnWidth(1, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(2, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(3, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(4, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(5, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(6, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(7, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(8, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(9, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(10, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(11, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(12, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(13, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(14, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(15, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(16, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(17, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(18, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(19, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(20, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(21, LARGE_COLUMN_SIZE);
			worksheet.setColumnWidth(22, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(23, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(24, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(25, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(26, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(27, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(28, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(29, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(30, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(31, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(32, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(33, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(34, MEDIUM_COLUMN_SIZE);
			// Write to file
			workbook.write(fileOut);
			fileOut.flush();

		} catch (FileNotFoundException e) {
			logger.log(Level.ERROR, "FileNotFoundException in tradingPartnerExcelDownload: " + e.getMessage());
			return null;
		} catch (IOException e) {
			logger.log(Level.ERROR, "IOException in tradingPartnerExcelDownload: " + e.getMessage());
			return null;
		} catch (Exception e) {
			logger.log(Level.ERROR, "Exception in tradingPartnerExcelDownload: " + e.getMessage());
			return null;
		}

		return filePath;
	}
 
	private List<String> getTradingPartnerHeaders() {
		return Arrays.asList("TC_DIRECTION", "TC_COMPANY_CODE", "TC_DIVISION", "TC_PURCH_ORG", "TC_SAP_ID_QUALIF",
				"TC_SAP_ID", "TC_IDOC_TYPE", "TC_MESSAGE_CODE", "TC_MESSAGE_PORT", "TC_MESSAGE_FUNCTION",
				"TC_MESSAGE_TYPE", "TC_VERSION", "TC_REHLKO_ISA_ID_QUALF", "TC_REHLKO_ISA_ID", "TC_REHLKO_GS",
				"TC_PARTNER_ISA_ID_QUALF", "TC_PARTNER_ISA_ID", "TC_PARTNER_GS", "TC_GS_FunctionalCode", "TC_MapName",
				"TC_PartnerName", "TC_ListVersion", "TC_Stream", "TC_DivisionName", "FlowType", "TC_TransactionCode",
				"TC_DeliveryType", "Active", "Test_REHLKO_ID", "GenericSID", "GenericRID");
	}

}
