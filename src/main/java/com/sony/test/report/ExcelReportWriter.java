package com.sony.test.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

public class ExcelReportWriter {
	
	private static Log LOGGER = LogFactory.getLog(ExcelReportWriter.class);
	private static final ColumnHeading[] HEADINGS = ColumnHeading.values();
	private static final String SHEET_NAME = "TestRunner";
	private static final String EXCEL_REPORT_ERROR ="EXCEL_REPORT_ERROR";
	private static final int TOTAL_ROW_START_COL = 4;
	private static final int TOTAL_ROW_LAST_COL = 14;
	private String filePath;
	private HSSFWorkbook workbook;
	private int PERCENTAGE_INVESTIGATED_COL = 12;
	private static final List<String> regexJobsList = Arrays.asList("TR_Auth_.*","TR_Commerce_AdHoc_*","TR_Commerce_PGW_*","TR_Commerce_Reg_*","TR_PTBatch_*","TR_Commerce_Tokenization_.*","TR_Commerce_3DS_Flow_*");
	
	
	public ExcelReportWriter(String filePath) {
		this.filePath = filePath;
	}

	public void write(Map<String, BuildResult> result) {
		{
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(SHEET_NAME);
			createHeading(sheet.createRow(0)); //rowNumber = 0
			fillTestResultData(result, sheet);
			adjustColumnSize(sheet);
			//add legend information
			
			writeToExcelFile(workbook);
		}
	}

	private void fillTestResultData(Map<String, BuildResult> result, HSSFSheet sheet) {
		Set<String> jobs = result.keySet();
		List<String> groupedJobs = new LinkedList<String>();
		List<String> list = jobs.stream().collect(Collectors.toList());
		
		for(String regex:regexJobsList)
		{
			Pattern pattern = Pattern.compile(regex);
			List<String> subGrpList = groupJobs(list,pattern);
			for(String job: subGrpList )
			{
				groupedJobs.add(job);
			}
		}
		Row row = null;
		int colTotalTests = 0;
		int rowNumber = 1;

		for(String job : groupedJobs)
		{
			row = sheet.createRow(rowNumber++);
			int sNo = rowNumber;
			row.createCell(0).setCellValue(sNo -1);
			row.createCell(1).setCellValue(job);
			BuildResult buildResult = result.get(job);
			if (buildResult != null) {
				int column = 2;
				row.createCell(column++).setCellValue(buildResult.getBuildNumber());
				row.createCell(column++).setCellValue(buildResult.getBuildResult());
				row.createCell(column++).setCellValue(buildResult.getTimestamp());
				List<Action> actions = buildResult.getActions();
				for (Action action : actions) {
					if (action.getTotalCount() > 0) {
						colTotalTests = column;
						row.createCell(column++).setCellValue(action.getTotalCount());
						row.createCell(column++).setCellValue(action.getTotalCount() - action.getFailCount());
						row.createCell(column++).setCellValue(action.getFailCount());
					}
				}
				Cell percentage_cell = row.createCell(PERCENTAGE_INVESTIGATED_COL);	
				setPercentageInvestigatedFormula(percentage_cell,rowNumber);
			}
		}
		Row totalsRow = sheet.createRow(rowNumber);
		computeTotalRow(sheet,rowNumber, totalsRow, colTotalTests);
		
	}
	
	private void computeTotalRow(HSSFSheet sheet, int rowNumber, Row row, int totalTestsColNum)
	{
		row = sheet.createRow(rowNumber);
		for(int i=0; i< TOTAL_ROW_START_COL; i++)
		{
			row.createCell(i);
		}
		Cell cell =row.createCell(totalTestsColNum-1);
		cell.setCellValue("TOTAL");
		for(int i= totalTestsColNum; i<= TOTAL_ROW_LAST_COL; i++)
		{
			if(i<12)
			{
				setSumFormula(row, totalTestsColNum);
				totalTestsColNum++;
			}
			else if(i==12)
			{
				setPercentageInvestigatedFormula(row.createCell(i),rowNumber+1);
			}
			else
			{
				row.createCell(i);
			}
		}
		int cellNumtoStyle = 14;
		styleLastRow(row,cellNumtoStyle);
	}
	
	private void setPercentageInvestigatedFormula(Cell cell,int rowNum){
		String strFormula= String.format("IF(J%d=%d,%s,IF(L%d=%d,%s, ROUND(L%d*%d/J%d,2)&%s))",rowNum,0, "\"0%\"", rowNum,0,"\"0%\"", rowNum,100,rowNum,"\"%\"");
		cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula(strFormula);
	}

	private void adjustColumnSize(HSSFSheet sheet) {
		for (int i = 0; i < HEADINGS.length; i++){
			sheet.autoSizeColumn(i);
		}
	}

	private void writeToExcelFile(HSSFWorkbook workbook) {
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			workbook.write(out);
			out.close();
			LOGGER.info(String.format("Test report is written successfully %s",filePath));
		} catch (FileNotFoundException exp) {
			LOGGER.error(EXCEL_REPORT_ERROR, exp);
		} catch (IOException exp) {
			LOGGER.error(EXCEL_REPORT_ERROR, exp);
		}
	}

	private void createHeading(Row row) {
		int cellnumHeading =0;
		for (ColumnHeading heading : HEADINGS) {
			row.createCell(cellnumHeading++).setCellValue(heading.getName());
		}
		styleRow(row);
	}
	
	private void setSumFormula(Row row, int columnNum){
		String column = CellReference.convertNumToColString(columnNum);
		String strFormula= String.format("SUM(%s%d:%s%d)",column, 1, column, row.getRowNum());
		Cell cell =row.createCell(columnNum);
		cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula(strFormula);
		cell.setCellStyle(getBoldStyle());
	}
	
	private CellStyle getBoldStyle() {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(font);
		return style;
	}
	
	private void styleRow(Row row) {
		CellStyle style = getBoldStyle();
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		//apply style to every cell.
		for (int i = 0; i< row.getLastCellNum(); i++) {
			row.getCell(i).setCellStyle(style);
		}
	}
	
	private void styleLastRow(Row row, int cellNumtoStyle) {
		CellStyle style = getBoldStyle();
		row.setHeightInPoints(27);
		style.setBorderTop(CellStyle.BORDER_MEDIUM);
		style.setBorderBottom(CellStyle.BORDER_MEDIUM);
		style.setBorderLeft(CellStyle.BORDER_MEDIUM);
		style.setBorderRight(CellStyle.BORDER_MEDIUM);
		style.setAlignment(style.ALIGN_CENTER);
		style.setVerticalAlignment(style.VERTICAL_CENTER);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		for (int i = TOTAL_ROW_START_COL; i<= cellNumtoStyle; i++) {
			row.getCell(i).setCellStyle(style);
		}
	}
	
	private List<String> groupJobs(List<String> filteredList,Pattern pattern)
	{
		List<String> matching = filteredList.stream()
		        .filter(pattern.asPredicate())
		        .collect(Collectors.toList());
		return matching;
	}
}
