package framework.tools;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XLSReporter implements IReporter {
    private static final String REPORT_DIR      = "xls";
    private static final String FILE_NAME       = "ERP_REGRESSION_RUN_REPORT.xls";

    private static final String TEST_NAME       = "TEST CLASS NAME";
    private static final String DURATION        = "DURATION";
    private static final String IN_SECONDS      = " (in seconds)";
    private static final String PASSED          = "PASSED";
    private static final String SKIPPED         = "SKIPPED";
    private static final String FAILED          = "FAILED";
    private static final String PASS_RATE       = "PASS RATE";
    private static final String TOTAL           = "TOTAL";

    private static final int TEST_NAME_INDEX    = 0;
    private static final int DURATION_INDEX     = 1;
    private static final int PASSED_INDEX       = 2;
    private static final int SKIPPED_INDEX      = 3;
    private static final int FAILED_INDEX       = 4;
    private static final int PASS_RATE_INDEX    = 5;

    private final short FONT_HIGHT = 18;
    private HSSFWorkbook workbook;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        workbook = new HSSFWorkbook();

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> suiteResults = suite.getResults();

            String suiteName = suite.getName();
            Date startDate = new Date();
            List<Map<String, Object>> table = new ArrayList<Map<String, Object>>();

            for (ISuiteResult suiteResult : suiteResults.values()) {
                ITestContext testContext = suiteResult.getTestContext();
                Map<String, Object> rowMap = new HashMap<String, Object>();
                rowMap.put(TEST_NAME, testContext.getName());
                rowMap.put(DURATION, getDurationInSeconds(testContext));
                rowMap.put(PASSED, testContext.getPassedTests().size());
                rowMap.put(SKIPPED, testContext.getSkippedTests().size());
                rowMap.put(FAILED, testContext.getFailedTests().size());

                table.add(rowMap);
            }
            System.out.println(table);
            writeSuiteToSheet(suiteName, startDate, table);
        }

        File outputDirectoryFile = new File(outputDirectory, REPORT_DIR);
        outputDirectoryFile.mkdirs();
        writeWorkbook(new File(outputDirectoryFile, FILE_NAME));
    }

    private long getDurationInSeconds(ITestContext testContext) {
        return (testContext.getEndDate().getTime() - testContext.getStartDate().getTime()) / 1000;
    }

    private void writeSuiteToSheet(String suiteName, Date startDate, List<Map<String, Object>> table) {
        int rowNum = 0;

        HSSFSheet sheet = workbook.createSheet(suiteName);
        sheet.setDefaultRowHeightInPoints(22);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
        sheet.setColumnWidth(0, 16000);
        for (int i = 1; i <= 6; i++) {
            sheet.setColumnWidth(i, 10000);
        }

        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("SUITE : " + suiteName);
        titleCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.AQUA.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        Row dateRow = sheet.createRow(rowNum++);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Generated on " + startDate.toString());

        if (table.size() == 0) {
            return;
        }

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);

        //report header section
        Cell testNameHeaderCell = headerRow.createCell(TEST_NAME_INDEX);
        testNameHeaderCell.setCellValue(TEST_NAME);
        testNameHeaderCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.AQUA.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        Cell durationHeaderCell = headerRow.createCell(DURATION_INDEX);
        durationHeaderCell.setCellValue(DURATION + IN_SECONDS);
        durationHeaderCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.AQUA.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        Cell passedHeaderCell = headerRow.createCell(PASSED_INDEX);
        passedHeaderCell.setCellValue(PASSED);
        passedHeaderCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREEN.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        Cell skippedHeaderCell = headerRow.createCell(SKIPPED_INDEX);
        skippedHeaderCell.setCellValue(SKIPPED);
        skippedHeaderCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREY_50_PERCENT.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        Cell failedHeaderCell = headerRow.createCell(FAILED_INDEX);
        failedHeaderCell.setCellValue(FAILED);
        failedHeaderCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.RED.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        Cell passRateHeaderCell = headerRow.createCell(PASS_RATE_INDEX);
        passRateHeaderCell.setCellValue(PASS_RATE);
        passRateHeaderCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.AQUA.index).boldWhiteFont().fontHightInPoints(FONT_HIGHT).build());

        //report cells section
        for (Map rowMap : table) {
            Row row = sheet.createRow(rowNum++);

            Cell testNameCell = row.createCell(TEST_NAME_INDEX);
            testNameCell.setCellValue((String) rowMap.get(TEST_NAME));
            testNameCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREY_25_PERCENT.index).build());

            Cell durationCell = row.createCell(DURATION_INDEX);
            durationCell.setCellValue((Long) rowMap.get(DURATION));
            durationCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREY_25_PERCENT.index).build());

            Cell passedCell = row.createCell(PASSED_INDEX);
            passedCell.setCellValue((Integer) rowMap.get(PASSED));
            passedCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.LIGHT_GREEN.index).build());

            Cell skippedCell = row.createCell(SKIPPED_INDEX);
            skippedCell.setCellValue((Integer) rowMap.get(SKIPPED));
            skippedCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREY_25_PERCENT.index).build());

            Cell failedCell = row.createCell(FAILED_INDEX);
            failedCell.setCellValue((Integer) rowMap.get(FAILED));
            failedCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.CORAL.index).build());

            Cell passRateCell = row.createCell(PASS_RATE_INDEX);
            passRateCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
            passRateCell.setCellFormula(passRateFormula(rowNum, "C", new String[]{"C", "D", "E"}));
            passRateCell.setCellStyle(new CellStyleBuilder().format("0.00%").backgroundColor(HSSFColor.GREY_25_PERCENT.index).build());
        }

        //report Total row section
        Row totalRow = sheet.createRow(rowNum);

        Cell totalCell = totalRow.createCell(TEST_NAME_INDEX);
        totalCell.setCellValue(TOTAL);
        totalCell.setCellStyle(new CellStyleBuilder().boldFont().fontHightInPoints(FONT_HIGHT).backgroundColor(HSSFColor.AQUA.index).build());

        Cell durationTotalCell = totalRow.createCell(DURATION_INDEX);
        durationTotalCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        durationTotalCell.setCellFormula(sumFormula("B", 5, rowNum));
        durationTotalCell.setCellStyle(new CellStyleBuilder().boldFont().fontHightInPoints(FONT_HIGHT).backgroundColor(HSSFColor.AQUA.index).build());

        Cell passedTotalCell = totalRow.createCell(PASSED_INDEX);
        passedTotalCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        passedTotalCell.setCellFormula(sumFormula("C", 5, rowNum));
        passedTotalCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREEN.index).boldFont().fontHightInPoints(FONT_HIGHT).build());

        Cell skippedTotalCell = totalRow.createCell(SKIPPED_INDEX);
        skippedTotalCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        skippedTotalCell.setCellFormula(sumFormula("D", 5, rowNum));
        skippedTotalCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.GREY_50_PERCENT.index).boldFont().fontHightInPoints(FONT_HIGHT).build());

        Cell failedTotalCell = totalRow.createCell(FAILED_INDEX);
        failedTotalCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        failedTotalCell.setCellFormula(sumFormula("E", 5, rowNum));
        failedTotalCell.setCellStyle(new CellStyleBuilder().backgroundColor(HSSFColor.RED.index).boldFont().fontHightInPoints(FONT_HIGHT).build());

        Cell passRateTotalCell = totalRow.createCell(PASS_RATE_INDEX);
        passRateTotalCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        passRateTotalCell.setCellFormula(passRateFormula(rowNum + 1, "C", new String[]{"C", "D", "E"}));
        passRateTotalCell.setCellStyle(new CellStyleBuilder().boldFont().format("0.00%").fontHightInPoints(FONT_HIGHT).backgroundColor(HSSFColor.AQUA.index).build());
    }

    private String sumFormula(String column, int startIndex, int endIndex) {
        return "SUM(" + column + startIndex + ":" + column + endIndex +")";
    }

    private String passRateFormula(int rowNum, String mainColumn, String[] columns) {
        StringBuilder builder = new StringBuilder();
        builder.append(mainColumn);
        builder.append(rowNum);
        builder.append("/(");
        for (String column : columns) {
            builder.append(column);
            builder.append(rowNum);
            builder.append("+");
        }
        builder.deleteCharAt(builder.lastIndexOf("+"));
        builder.append(")");
        return builder.toString();
    }

    private void writeWorkbook(File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CellStyleBuilder {
        private CellStyle style;

        final short CELL_BORDER_COLOR = HSSFColor.GOLD.index;
        final short CELL_BORDER_WIDTH = HSSFCellStyle.BORDER_THICK;

        public CellStyleBuilder() {

            style = workbook.createCellStyle();
            style.setBorderBottom(CELL_BORDER_WIDTH);
            style.setBorderTop(CELL_BORDER_WIDTH);
            style.setBorderRight(CELL_BORDER_WIDTH);
            style.setBorderLeft(CELL_BORDER_WIDTH);
            style.setBottomBorderColor(CELL_BORDER_COLOR);
            style.setTopBorderColor(CELL_BORDER_COLOR);
            style.setLeftBorderColor(CELL_BORDER_COLOR);
            style.setRightBorderColor(CELL_BORDER_COLOR);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        }

        public CellStyleBuilder format(String format) {
            style.setDataFormat(workbook.createDataFormat().getFormat(format));
            return this;
        }

        public CellStyleBuilder boldWhiteFont() {
            HSSFFont boldWhiteFont = workbook.createFont();
            boldWhiteFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            boldWhiteFont.setColor(HSSFColor.WHITE.index);
            style.setFont(boldWhiteFont);
            return this;
        }

        public CellStyleBuilder boldFont() {
            HSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(boldFont);
            return this;
        }

        public CellStyleBuilder fontHightInPoints(short fontSize){
            HSSFFont hightFont = workbook.createFont();
            hightFont.setFontHeightInPoints(fontSize);
            style.setFont(hightFont);
            return this;
        }

        public CellStyleBuilder backgroundColor(short index) {
            style.setFillForegroundColor(index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            return this;
        }

        public CellStyle build() {
            return style;
        }
    }
}
