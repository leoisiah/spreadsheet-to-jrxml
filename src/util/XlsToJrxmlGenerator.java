package util;

import domain.CellDomain;
import domain.ReportDomain;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: IPC_Server
 * Date: 8/26/14
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class XlsToJrxmlGenerator {

    private static final String COLUMN_COUNT = "COLUMN_COUNT";
    private static final String SHEET_WIDTH = "SHEET_WIDTH";
    private static final String ROW_COUNT = "ROW_COUNT";
    private static final String COLUMN_HEADER_START_ROW = "COLUMN_HEADER_START_ROW";
    private static final String COLUMN_HEADER_END_ROW = "COLUMN_HEADER_END_ROW";
    private static final String SUMMARY_START_ROW = "SUMMARY_START_ROW";

    public static void main(String[] args) throws Exception {
        process(new File("Coupons3.2a_peso.xls"), false);
//        process(new File("DTR3.3_PesoTDGS.xlsx"), false);
//        process(new File("C:/Users/IPC_Server/Documents/Investment Reports/Ned Files/INVREP - Trading (v2).xlsx"));
    }

    public static List<File> process(File file, boolean portrait) throws Exception {
        List<File> outputFiles = new ArrayList<File>();
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fis);

        int numberOfSheets = workbook.getNumberOfSheets();
        int i;

        for(i=0;i<numberOfSheets;i++) {

            Sheet sheet = workbook.getSheetAt(i);


            ReportDomain reportDomain = generateReportDomain(sheet, portrait);
            if(reportDomain!=null) {
                String outputFilename = generateOutputFilename(file, i);
                File outputFile = new File(outputFilename);
                exportToJrxml(reportDomain,portrait, outputFile);
                outputFiles.add(outputFile);
            }

        }

        fis.close();

         return outputFiles;
    }

    private static String generateOutputFilename(File file, int i) {
        String path = file.getAbsolutePath();
        String outputPath = path.substring(0, path.lastIndexOf('.'));
        if(i!=0) {
            outputPath += "_"+(i+1);
        }
        outputPath += ".jrxml";
        return outputPath;
    }


    private static ReportDomain generateReportDomain(Sheet sheet, boolean portrait) {
        int totalReportWidth = 802;
        if(portrait) {
            totalReportWidth = 555;
        }
        Map<String, Integer> sheetDetails = getDetails(sheet);

        if(sheetDetails.get(ROW_COUNT)==0) {
            return null;
        }
        int rowCount = sheetDetails.get(ROW_COUNT);
        int columnCount = sheetDetails.get(COLUMN_COUNT);
        int sheetWidth = sheetDetails.get(SHEET_WIDTH);
        int columnHeaderStart = sheetDetails.get(COLUMN_HEADER_START_ROW);
        int columnHeaderEnd = sheetDetails.get(COLUMN_HEADER_END_ROW);
        int summaryStart = sheetDetails.get(SUMMARY_START_ROW);
        int i;
        List<Integer> modifiedWidths = new ArrayList<Integer>();
        List<Integer> lefts = new ArrayList<Integer>();
        int totalReportWidthSoFar = 0;
        for(i=0;i<columnCount;i++) {
            lefts.add(totalReportWidthSoFar);
            int modifiedWidth = (int) Math.round(1.0*getColumnWidth(sheet, i)/sheetWidth*totalReportWidth);
            if(i==columnCount-1) {
                modifiedWidth = totalReportWidth-totalReportWidthSoFar;
            }
            totalReportWidthSoFar +=modifiedWidth;
            modifiedWidths.add(modifiedWidth);
        }



        ReportDomain reportDomain = new ReportDomain();
        reportDomain.setTitleHeight(columnHeaderStart*CellDomain.CELL_HEIGHT);
        reportDomain.setColumnHeaderHeight((columnHeaderEnd-columnHeaderStart)*CellDomain.CELL_HEIGHT);
        reportDomain.setSummaryHeight((rowCount-summaryStart)*CellDomain.CELL_HEIGHT);

        List<CellDomain> titleCells = getCellDomains(sheet, 0, columnHeaderStart, columnCount, lefts, modifiedWidths);
        List<CellDomain> columnHeaderCells = getCellDomains(sheet, columnHeaderStart, columnHeaderEnd, columnCount, lefts, modifiedWidths);
        List<CellDomain> summaryCells = getCellDomains(sheet, summaryStart, rowCount, columnCount, lefts, modifiedWidths);
        List<CellDomain> detailCells = getDetailCellDomains(sheet, columnCount, lefts, modifiedWidths);

        reportDomain.getTitleCells().addAll(titleCells);
        reportDomain.getColumnHeaderCells().addAll(columnHeaderCells);
        reportDomain.getSummaryCells().addAll(summaryCells);
        reportDomain.getDetailCells().addAll(detailCells);

        return reportDomain;
    }

    private static Map<String,Integer> getDetails(Sheet sheet) {
        int width = 0;
        int lastRowNum = -1;
        Iterator<Row> rowIterator = sheet.rowIterator();

        int maxColumnIndex = -1;
        Map<Integer,Integer> numberOfNonEmptyCellsPerRow = new HashMap<Integer,Integer>();
        Map<Integer,Integer> lastCellNumPerRow = new HashMap<Integer, Integer>();
        Map<Integer,Boolean> isDetailPerRow = new HashMap<Integer, Boolean>();

        while(rowIterator.hasNext()) {

            Row row = rowIterator.next();


            int lastCellNum = getLastNonBlankCellNum(row);
            if(lastCellNum!=-1) {
                lastRowNum = row.getRowNum();
            }

//            if(row.getCell(lastCellNum)==null) {
//                lastCellNum--;
//            }
            int numberOfNonEmptyCells = 0;
            int j;
            int cellIndex = 0;
            boolean isDetail = false;
            for(j=0;j<=lastCellNum;j++) {
                Cell cell = row.getCell(j);


                if(cell!=null && !cell.toString().equals("")) {
                    numberOfNonEmptyCells++;
                    if(cell.getCellType()!=1) {
                        if(getColumnWidth(sheet,j)!=0) {
                            isDetail = true;
                        }

                    }

                }

                if(j>=maxColumnIndex+1) {
                    width+=getColumnWidth(sheet, j);
//                    System.out.println(j+":"+sheet.getColumnWidth(j));
                }
            }
            isDetailPerRow.put(row.getRowNum(), isDetail);
            lastCellNumPerRow.put(row.getRowNum(),lastCellNum);
            numberOfNonEmptyCellsPerRow.put(row.getRowNum(),numberOfNonEmptyCells);
            if(maxColumnIndex< lastCellNum) {
                maxColumnIndex = lastCellNum;
            }
        }

        rowIterator = sheet.rowIterator();
        int numColumns = maxColumnIndex+1;
        int columnHeaderStart = 0;
        int columnHeaderEnd = 0;
        int summaryStart = 0;
        boolean columnHeaderStartFound = false;
        boolean columnHeaderEndFound = false;
        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int numColumnsMinusOutliers = lastCellNumPerRow.get(row.getRowNum())+1;

            int numNonEmptyCells = numberOfNonEmptyCellsPerRow.get(row.getRowNum());
            boolean hasManyColumns = 1.0 * numColumnsMinusOutliers / (maxColumnIndex + 1) > 0.87 && 1.0 * numColumnsMinusOutliers / (maxColumnIndex + 1) < 1.00;
//            System.out.println("ROW:"+row.getRowNum());
//            System.out.println(numColumnsMinusOutliers );
//            System.out.println(maxColumnIndex+1);



            if(hasManyColumns) {
//                System.out.println("DECREASED COLUMNS!");
                numColumns = numColumnsMinusOutliers;

            }

            boolean hasManyFilledCells = 1.0 * numNonEmptyCells / (maxColumnIndex + 1) > 0.70;
            if(!columnHeaderStartFound) {
                if(hasManyFilledCells) {
                    columnHeaderStartFound = true;
                    columnHeaderStart = row.getRowNum();
                }
            }
            Boolean isDetail = isDetailPerRow.get(row.getRowNum());
            if(columnHeaderStartFound && !columnHeaderEndFound) {
                if(isDetail) {
                    columnHeaderEndFound = true;
                    columnHeaderEnd = row.getRowNum();
                }
            }
            hasManyColumns = 1.0 * numColumnsMinusOutliers / (maxColumnIndex + 1) > 0.75;
            if(isDetail && hasManyColumns) {
                summaryStart = row.getRowNum()+1;
            }

        }





        int k;
        for(k=numColumns;k<=maxColumnIndex;k++) {
            width-=getColumnWidth(sheet, k);
        }


        Map<String,Integer> returned = new HashMap<String, Integer>();
        returned.put(SHEET_WIDTH, width);
        returned.put(COLUMN_COUNT, numColumns);
        returned.put(ROW_COUNT, lastRowNum+1);
        returned.put(COLUMN_HEADER_START_ROW, columnHeaderStart);
        returned.put(COLUMN_HEADER_END_ROW, columnHeaderEnd);
        returned.put(SUMMARY_START_ROW, summaryStart);
        return returned;
    }

    private static int getLastNonBlankCellNum(Row row) {
        int lastNonBlankCellNum = -1;
        Iterator<Cell> cellIterator = row.cellIterator();
        while(cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if(cell!=null && cell.toString().trim().length()!=0) {
                lastNonBlankCellNum = cell.getColumnIndex();
            }
        }
        return lastNonBlankCellNum;
    }

    private static String getHorizontalAlignString(Cell cell) {
        String horizontalAlign;
        CellStyle cellStyle = cell.getCellStyle();
        switch(cellStyle.getAlignment()) {
            case CellStyle.ALIGN_LEFT:
                horizontalAlign = "Left";
                break;
            case CellStyle.ALIGN_CENTER:
                horizontalAlign = "Center";
                break;
            case CellStyle.ALIGN_RIGHT:
                horizontalAlign = "Right";
                break;
            case CellStyle.ALIGN_JUSTIFY:
                horizontalAlign = "Justified";
                break;
            default:
                horizontalAlign = "Left";
        }
        return horizontalAlign;
    }

    private static String getVerticalAlignString(Cell cell) {
        String verticalAlign;
        CellStyle cellStyle = cell.getCellStyle();
        switch(cellStyle.getVerticalAlignment()) {
            case CellStyle.VERTICAL_TOP:
                verticalAlign = "Top";
                break;
            case CellStyle.VERTICAL_CENTER:
                verticalAlign = "Middle";
                break;
            case CellStyle.VERTICAL_BOTTOM:
                verticalAlign = "Bottom";
                break;
            default:
                verticalAlign = "Middle";
                break;
        }
        return verticalAlign;
    }

    private static List<CellDomain> getCellDomains(Sheet sheet, int rowStart, int rowEnd, int columnCount, List<Integer> lefts, List<Integer> modifiedWidths) {
        List<CellDomain> cellDomains = new ArrayList<CellDomain>();
        int i;
        for(i=rowStart;i<rowEnd;i++) {
            Row row = sheet.getRow(i);

            if(row!=null) {
                int j;
                for(j=0;j<columnCount;j++) {
                    Cell cell = row.getCell(j);

                    if(cell!=null) {
                        CellStyle cellStyle = cell.getCellStyle();
                        String text = cell.toString().trim();
                        short borderTop = cellStyle.getBorderTop();
                        short borderBottom = cellStyle.getBorderBottom();
                        short borderLeft = cellStyle.getBorderLeft();
                        short borderRight = cellStyle.getBorderRight();
                        short fontIndex = cellStyle.getFontIndex();
                        Font font = sheet.getWorkbook().getFontAt(fontIndex);


                        if(text.length()==0 && borderTop==0 && borderBottom==0 && borderLeft==0 && borderRight==0) {
                            continue;
                        }


                        String horizontalAlign = getHorizontalAlignString(cell);
                        String verticalAlign = getVerticalAlignString(cell);

                        CellDomain cellDomain = new CellDomain();
                        cellDomain.setText(text);
                        cellDomain.setTop((i-rowStart)*CellDomain.CELL_HEIGHT);
                        cellDomain.setLeft(lefts.get(j));
                        int width = modifiedWidths.get(j);
                        cellDomain.setWidth(width);
                        cellDomain.setHorizontalAlign(horizontalAlign);
                        cellDomain.setVerticalAlign(verticalAlign);
                        cellDomain.setBorderTop(Math.min(borderTop*0.25,1));
                        cellDomain.setBorderBottom(Math.min(borderBottom*0.25,1));
                        cellDomain.setBorderLeft(Math.min(borderLeft*0.25,1));
                        cellDomain.setBorderRight(Math.min(borderRight*0.25,1));
                        cellDomain.setBold(700==font.getBoldweight());
                        if(width<0) {
                            System.out.println(cell);
                        }
                        if(width>0) {
                            //try to extend cell to the right
                            if(horizontalAlign.equals("Left")) {
                                int k;
                                for(k=j+1;k<columnCount;k++) {
                                    Cell adjacentCell = row.getCell(k);

                                    boolean extend = false;
                                    int adjacentWidth = modifiedWidths.get(k);
                                    if(adjacentWidth==0) {
                                        extend = true;
                                    } else {
                                        if(adjacentCell==null) {
                                            extend = true;
                                        } else {
                                            int adjacentCellTextLength = adjacentCell.toString().trim().length();
                                            CellStyle adjacentCellStyle= adjacentCell.getCellStyle();

                                            if(adjacentCellTextLength==0 && adjacentCellStyle.getBorderLeft()==0) {
                                                extend = true;
                                            }
                                        }
                                    }

                                    if(extend) {

                                        cellDomain.setWidth(cellDomain.getWidth()+adjacentWidth);
                                        j=k;
                                    } else {
                                        break;
                                    }





                                }
                            }

                            cellDomains.add(cellDomain);
                        }

                    }

                }
            }
        }
        return cellDomains;
    }
    private static List<CellDomain> getDetailCellDomains(Sheet sheet, int columnCount, List<Integer> lefts, List<Integer> modifiedWidths) {
        List<CellDomain> cellDomains = new ArrayList<CellDomain>();

        int j;
        for(j=0;j<columnCount;j++) {
            CellDomain cellDomain = new CellDomain();
            cellDomain.setTop(0);
            cellDomain.setLeft(lefts.get(j));
            int width = modifiedWidths.get(j);
            cellDomain.setWidth(width);
            if(width!=0) {
                cellDomains.add(cellDomain);
            }

        }


        return cellDomains;
    }

    public static void exportToJrxml(ReportDomain reportDomain, boolean portrait, File file) throws Exception {
        Configuration cfg = new Configuration();
        Map root = new HashMap();
        root.put("reportDomain", reportDomain);
        String templateFile = "landscape.ftl";
        if(portrait) {
            templateFile = "portrait.ftl";
        }

        Template temp = cfg.getTemplate(templateFile);
//        StringWriter out = new StringWriter();
        FileWriter out = new FileWriter(file);
        temp.process(root,out);
        out.close();
    }

    private static int getColumnWidth(Sheet sheet, int columnIndex) {

        int columnWidth = 0;
        if(sheet.isColumnHidden(columnIndex)) {
            return 0;
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
        int removeFirstFewColumns = -1;
        main:while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int cellIndex;
            Iterator<Cell> cellIterator = row.cellIterator();
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(cell!=null && cell.toString().trim().length()!=0) {
                    removeFirstFewColumns = cell.getColumnIndex()-1;

                    break main;
                }
            }
        }
        if(columnIndex<=removeFirstFewColumns) {
            return 0;
        }

        return sheet.getColumnWidth(columnIndex);

    }

}
