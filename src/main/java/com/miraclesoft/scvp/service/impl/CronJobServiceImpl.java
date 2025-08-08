package com.miraclesoft.scvp.service.impl;

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.DocumentRepository;

/**
* The class CronJobServiceImpl.
*
* @author Priyanka Kolla
*/
@Component
public class CronJobServiceImpl {

    /** The documents creation path. */
    @Value("${documentPath}")
    private String documentPath;

    /** The jdbc template. */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** The logger. */
    private static Logger logger = LogManager.getLogger(CronJobServiceImpl.class.getName());

    /** The Constant SMALL_COLUMN_SIZE. */
    private static final int SMALL_COLUMN_SIZE = 4000;

    /** The Constant MEDIUM_COLUMN_SIZE. */
    private static final int MEDIUM_COLUMN_SIZE = 6000;

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

    /**
     * Gets the to emails for report.
     *
     * @param type the type
     * @return the to emails for report
     */
    public Set<String> getToEmailsForReport(final String type) {
        final Set<String> toEmails = new HashSet<String>();
        try {
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT reciver_ids FROM scheduler WHERE reciver_ids IS NOT NULL AND sch_type = ?", type);
            for (final Map<String, Object> row : rows) {
                final String emails = (String) row.get("reciver_ids");
                final String[] emailsArray = emails.split(",");
                for (int i = 0; i < emailsArray.length; i++) {
                    toEmails.add(emailsArray[i]);
                }
            }
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " getToEmailsForReport :: " + exception.getMessage());
        }
        return toEmails;
    }

    /**
     * Gets the cc emails for report.
     *
     * @param type the type
     * @return the cc emails for report
     */
    public Set<String> getCcEmailsForReport(final String type) {
        final Set<String> ccEmails = new HashSet<String>();
        try {
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT extranal_emailids FROM scheduler WHERE extranal_emailids IS NOT NULL AND sch_type = ?", type);
            for (final Map<String, Object> row : rows) {
                final String emails = (String) row.get("extranal_emailids");
                final String[] emailsArray = emails.split(",");
                for (int i = 0; i < emailsArray.length; i++) {
                    ccEmails.add(emailsArray[i]);
                }
            }
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " getCcEmailsForReport :: " + exception.getMessage());
        }
        return ccEmails;
    }

    /**
     * Archive purge scheduler.
     *
     * @return the string
     * @throws SQLException the SQL exception
     */
    public String archivePurgeScheduler() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        String responseString = null;
        try {
            connection = jdbcTemplate.getDataSource()
                                     .getConnection();
            statement = connection.prepareStatement("CALL archive_and_purge()");
            statement.execute();
            responseString = "Files archived and purged successfully.";
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " archivePurgeScheduler :: " + exception.getMessage());
        }
        finally {
            if (nonNull(statement)) {
                statement.close();
                statement = null;
            }
            if (nonNull(connection)) {
                connection.close();
                connection = null;
            }
        }
        return responseString;
    }

    /**
     * Gets the file.
     *
     * @param type the type
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String getFile(final String type) throws IOException {
        FileOutputStream fileOutputStream = null;
        String filePath = "No file";
        String from = null;
        String fromForFile = null;
        String toForFile = null;
        try {
            final SimpleDateFormat dateFormatForQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final SimpleDateFormat dateFormatForFile = new SimpleDateFormat("MM/dd/yyyy");
            final Calendar now = getTodayWithStartTime();
            if (type.equals("Daily")) {
                now.add(Calendar.DATE, -ONE);
                from = dateFormatForQuery.format(now.getTime());
                fromForFile = dateFormatForFile.format(now.getTime());
            }
            else if (type.equals("Weekly")) {
                now.add(Calendar.DATE, -SEVEN);
                from = dateFormatForQuery.format(now.getTime());
                fromForFile = dateFormatForFile.format(now.getTime());
                now.add(Calendar.DATE, +SIX);
                toForFile = dateFormatForFile.format(now.getTime());
            }
            else if (type.equals("Monthly")) {
                now.add(Calendar.MONTH, -ONE);
                from = dateFormatForQuery.format(now.getTime());
                fromForFile = dateFormatForFile.format(now.getTime());
                now.add(Calendar.MONTH, +ONE);
                now.add(Calendar.DATE, -ONE);
                toForFile = dateFormatForFile.format(now.getTime());
            }
            final List<DocumentRepository> docs = documents(from,
                    dateFormatForQuery.format(getTodayWithStartTime().getTime()));
            if (docs.size() != 0) {
                final File file = new File(documentPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                filePath = file.getAbsolutePath() + File.separator + type + "-report.xlsx";
                fileOutputStream = new FileOutputStream(filePath);
                final XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet worksheet = null;
                worksheet = workbook.createSheet(type);
                final XSSFCellStyle greenFontCellStyle = greenFontCellStyle(workbook);
                final XSSFCellStyle redFontCellStyle = redFontCellStyle(workbook);
                final XSSFCellStyle orangeFontCellStyle = orangeFontCellStyle(workbook);
                XSSFRow row = worksheet.createRow(0);
                row = worksheet.createRow(ONE);
                final Cell cell = row.createCell(ONE);
                if (type.equals("Daily")) {
                    cell.setCellValue("Daily Report         ::         " + fromForFile);
                }
                else {
                    cell.setCellValue(
                            type + " Report         ::         From : " + fromForFile + "     To : " + toForFile);
                }
                cell.setCellStyle(pageHeaderCellStyle(workbook));
                worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:J2"));
                row = worksheet.createRow(THREE);
                final List<String> headers = getHeaders();
                for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
                    final XSSFCell headerCell = row.createCell(headerIndex);
                    headerCell.setCellValue(headers.get(headerIndex));
                    headerCell.setCellStyle(tableHeaderCellStyle(workbook));
                }
                for (int docIndex = 0; docIndex < docs.size(); docIndex++) {
                    final DocumentRepository documentRepository = docs.get(docIndex);
                    row = worksheet.createRow(docIndex + FOUR);
                    row.createCell(0)
                       .setCellValue(Integer.toString(docIndex + 1));
                    row.createCell(ONE)
                       .setCellValue(documentRepository.getDateTimeReceived()
                                                       .substring(0, documentRepository.getDateTimeReceived()
                                                                                       .lastIndexOf(':')
                                                               - TWO));
                    row.createCell(TWO)
                       .setCellValue(documentRepository.getFileType());
                    row.createCell(THREE)
                       .setCellValue(documentRepository.getFileId());
                    row.createCell(FOUR)
                       .setCellValue(documentRepository.getPartnerName());
                    row.createCell(FIVE)
                       .setCellValue(documentRepository.getTransactionType());
                    row.createCell(SIX)
                       .setCellValue(documentRepository.getDirection());
                    final XSSFCell cellX = row.createCell(SEVEN);
                    if (nonNull(documentRepository.getStatus())) {
                        if (documentRepository.getStatus()
                                              .equalsIgnoreCase("SUCCESS")) {
                            cellX.setCellValue(documentRepository.getStatus()
                                                                 .toUpperCase());
                            cellX.setCellStyle(greenFontCellStyle);
                        }
                        else if (documentRepository.getStatus()
                                                   .equalsIgnoreCase("ERROR")) {
                            cellX.setCellValue(documentRepository.getStatus()
                                                                 .toUpperCase());
                            cellX.setCellStyle(redFontCellStyle);
                        }
                        else {
                            cellX.setCellValue(documentRepository.getStatus()
                                                                 .toUpperCase());
                            cellX.setCellStyle(orangeFontCellStyle);
                        }
                    }
                    row.createCell(EIGHT)
                       .setCellValue(documentRepository.getAckStatus()
                                                       .toUpperCase());
                    row.createCell(NINE)
                       .setCellValue(documentRepository.getFileName());
                }
                worksheet.setColumnWidth(ONE, MEDIUM_COLUMN_SIZE);
                worksheet.setColumnWidth(TWO, SMALL_COLUMN_SIZE);
                worksheet.setColumnWidth(THREE, SMALL_COLUMN_SIZE);
                worksheet.setColumnWidth(FOUR, MEDIUM_COLUMN_SIZE);
                worksheet.setColumnWidth(FIVE, SMALL_COLUMN_SIZE);
                worksheet.setColumnWidth(SIX, SMALL_COLUMN_SIZE);
                worksheet.setColumnWidth(SEVEN, SMALL_COLUMN_SIZE);
                worksheet.setColumnWidth(EIGHT, SMALL_COLUMN_SIZE);
                worksheet.setColumnWidth(NINE, LARGE_COLUMN_SIZE);
                workbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            logger.log(Level.ERROR, " getFile :: " + fileNotFoundException.getMessage());
        }
        catch (IOException ioException) {
            logger.log(Level.ERROR, " getFile :: " + ioException.getMessage());
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " getFile :: " + exception.getMessage());
        }
        finally {
            fileOutputStream.close();
        }
        return filePath;
    }

    /**
     * Document repository search.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the list
     */
    private List<DocumentRepository> documents(final String startDate, final String endDate) {
        final List<DocumentRepository> docs = new ArrayList<DocumentRepository>();
        try {
            final StringBuilder documentSearchQuery = new StringBuilder();
            documentSearchQuery.append("SELECT file_id, date_time_received, file_type, transaction_type, direction, "
                    + "status, ack_status, sender_id, receiver_id, filename FROM files "
                    + "WHERE date_time_received >= ? AND date_time_received < ? "
                    + "ORDER BY date_time_received DESC");
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(documentSearchQuery.toString(), startDate, endDate);
            final Map<String, String> tradingPartners = getAllTradingPartners();
            for (final Map<String, Object> row : rows) {
                final DocumentRepository document = new DocumentRepository();
                document.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
                document.setDateTimeReceived(nonNull(row.get("date_time_received"))
                        ? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
                        : "-");
                document.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
                document.setTransactionType(
                        nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
                final String direction = nonNull(row.get("direction")) ? (String) row.get("direction") : "-";
                document.setDirection(direction);
                document.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
                document.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
                String partnerName = "-";
                if ("INBOUND".equalsIgnoreCase(direction) && nonNull(row.get("sender_id"))
                        && nonNull(tradingPartners.get(row.get("sender_id")))) {
                    partnerName = tradingPartners.get(row.get("sender_id"));
                }
                else if ("OUTBOUND".equalsIgnoreCase(direction) && nonNull(row.get("receiver_id"))
                        && nonNull(tradingPartners.get(row.get("receiver_id")))) {
                    partnerName = tradingPartners.get(row.get("receiver_id"));
                }
                document.setPartnerName(partnerName);
                document.setFileName(nonNull(row.get("filename")) ? (String) row.get("filename") : "-");
                docs.add(document);
            }
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " documents :: " + exception.getMessage());
        }
        return docs;
    }

    private Map<String, String> getAllTradingPartners() {
        final Map<String, String> sendersMap = new TreeMap<String, String>();
        try {
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, name FROM tp ORDER BY name ASC");
            for (final Map<String, Object> row : rows) {
                sendersMap.put((String) row.get("id"), (String) row.get("name"));
            }
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " getAllTradingPartners :: " + exception.getMessage());
        }
        return sendersMap;
    }

    private List<String> getHeaders() {
        return Arrays.asList("#", "Date", "File format", "Instance Id", "Partner", "Transaction", "Direction", "Status",
                "Ack Status", "File name");
    }

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

    private XSSFCellStyle tableHeaderCellStyle(final XSSFWorkbook workbook) {
        final XSSFCellStyle tableHeaderCellStyle = workbook.createCellStyle();
        final XSSFFont tableHeaderFont = workbook.createFont();
        tableHeaderFont.setColor(HSSFColor.WHITE.index);
        tableHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        tableHeaderCellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        tableHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        tableHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        tableHeaderCellStyle.setFont(tableHeaderFont);
        return tableHeaderCellStyle;
    }

    private XSSFCellStyle greenFontCellStyle(final XSSFWorkbook workbook) {
        final XSSFCellStyle greenFontCellStyle = workbook.createCellStyle();
        final XSSFFont greenFont = workbook.createFont();
        greenFont.setColor(HSSFColor.GREEN.index);
        greenFontCellStyle.setFont(greenFont);
        return greenFontCellStyle;
    }

    private XSSFCellStyle redFontCellStyle(final XSSFWorkbook workbook) {
        final XSSFCellStyle redFontCellStyle = workbook.createCellStyle();
        final XSSFFont redFont = workbook.createFont();
        redFont.setColor(HSSFColor.RED.index);
        redFontCellStyle.setFont(redFont);
        return redFontCellStyle;
    }

    private XSSFCellStyle orangeFontCellStyle(final XSSFWorkbook workbook) {
        final XSSFCellStyle orangeFontCellStyle = workbook.createCellStyle();
        final XSSFFont orangeFont = workbook.createFont();
        orangeFont.setColor(HSSFColor.ORANGE.index);
        orangeFontCellStyle.setFont(orangeFont);
        return orangeFontCellStyle;
    }

    private Calendar getTodayWithStartTime() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar;
    }
}
