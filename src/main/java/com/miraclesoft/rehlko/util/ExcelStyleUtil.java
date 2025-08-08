package com.miraclesoft.rehlko.util;

//AWT Color class for hex-to-RGB conversion
import java.awt.Color;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelStyleUtil {

	public static CellStyle createHeaderStyle(Workbook workbook, short bgColor) {
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());

		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setFillForegroundColor(bgColor);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		applyBorders(style);
		return style;
	}

	public static CellStyle createBorderedStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		applyBorders(style);
		return style;
	}

	public static CellStyle createCurrencyStyle(Workbook workbook) {
		CellStyle style = createBorderedStyle(workbook);
		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("₹#,##0.00")); // For Indian format
		return style;
	}

	public static CellStyle createDateStyle(Workbook workbook) {
		CellStyle style = createBorderedStyle(workbook);
		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("MM/dd/yyyy"));
		return style;
	}

	public static CellStyle createErrorStyle(Workbook workbook) {
		CellStyle style = createBorderedStyle(workbook);
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	private static void applyBorders(CellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}

	public static CellStyle createHeaderStyleWithHexColor(Workbook workbook, String hexColor) {
		if (!(workbook instanceof XSSFWorkbook)) {
			throw new IllegalArgumentException("Custom colors require XSSFWorkbook (xlsx format).");
		}

		XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
		XSSFCellStyle style = xssfWorkbook.createCellStyle();

		// Convert hex to java.awt.Color
		Color awtColor = Color.decode(hexColor); // e.g., "#131f32"
		XSSFColor poiColor = new XSSFColor(awtColor, new DefaultIndexedColorMap());

		// Set background color
		style.setFillForegroundColor(poiColor);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Font settings
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);

		style.setAlignment(HorizontalAlignment.CENTER);
		applyBorders(style);

		return style;
	}
}
