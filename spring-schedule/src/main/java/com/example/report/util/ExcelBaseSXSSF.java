package com.example.report.util;


import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelBaseSXSSF implements AutoCloseable {
    protected SXSSFSheet _selectedSheet = null;
    protected SXSSFWorkbook _workbook = null; // Write only
    protected XSSFWorkbook workbook = null; // Write only

    protected CreationHelper _helper = null;
    private CellStyle _cs;

    public ExcelBaseSXSSF() {
        init(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
    }

    public ExcelBaseSXSSF(Path template) throws Exception {
        initReadFile(template.toString());
    }

    public ExcelBaseSXSSF(String template) throws Exception {
        initReadFile(template);
    }

    public ExcelBaseSXSSF(int rowAccessWindowSize) {
        init(rowAccessWindowSize);
    }

    private void init(int rowAccessWindowSize) {
        _workbook = new SXSSFWorkbook(rowAccessWindowSize); // '.xlsx'
        _helper = _workbook.getCreationHelper();
        _cs = this._workbook.createCellStyle();
        _cs.setWrapText(true);
    }

    private void initReadFile(String template) throws Exception {
        try(FileInputStream file = new FileInputStream(template))
        {
            workbook = new XSSFWorkbook(file);
            _workbook = new SXSSFWorkbook(workbook, SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
            _helper = workbook.getCreationHelper();
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }

    /**
     * sheet??? ????????????.
     * @param sheetName
     */
    public void addSheet(String sheetName) {
        SXSSFSheet sheet = _workbook.createSheet(sheetName);
    }

    /**
     * ????????? sheet??? ????????????.
     * @param sheetName
     */
    public void setSelectedSheet(String sheetName) {
        _selectedSheet = _workbook.getSheet(sheetName);
        if(_selectedSheet == null) {
            addSheet(sheetName);
            _selectedSheet = _workbook.getSheet(sheetName);
        }
    }

    /*
     * @see ????????? sheet??? ????????????.
     * @param sheetName
     */
    public void setSelectedSheet(int index) {
        _selectedSheet = _workbook.getSheetAt(index);
    }

    /**
     * ?????? ?????????????????? ????????? ????????????.
     * @return ????????? ??????
     */
    public SXSSFSheet getSeletedSheet() {
        return _selectedSheet;
    }

    /**
     * keep ????????? rowSize ?????? momory??? ????????????  ????????? disk???  flush??????.
     * @param rowSize
     */
    public void setRandomAccessWindowSize(int rowSize) {
        if(_selectedSheet != null) {
            _selectedSheet.setRandomAccessWindowSize(rowSize);
        }
    }

    /**
     * ??????????????? ????????????.
     * @param sheetName
     * @param newSheetName
     */
    public void changeSheetName(String sheetName, String newSheetName) {
        int index = _workbook.getSheetIndex(sheetName);
        _workbook.setSheetName(index, newSheetName);
    }

    /**
     * ????????? ???????????? row??? ????????????. ???????????? ???????????? ???????????? ??? ????????????.
     * @param index
     * @return row
     */
    protected Row getRow(int index)
    {
        Row ret = _selectedSheet.getRow(index);
        if(ret == null) {
            ret = _selectedSheet.createRow(index);
        }
        return ret;
    }

    /**
     * ?????? ??? Row?????? ????????? cell??? ????????????. ???????????? ???????????? ???????????? ??? ????????????.
     * @param row
     * @param index (column index)
     * @return cell
     */
    protected Cell getCell(Row row, int index)
    {

        Cell ret = row.getCell(index);
        if (ret == null) {
            ret = row.createCell(index);
        }
        return ret;
    }

    /***
     * row,column to address
     */
    public String getCellAddressStr(int row, int column) {
        return new CellAddress(row, column).toString();
    }

    /***
     * address to cell row, column
     */
    public CellAddress getAddressStartCell(String address) {
        CellAddress ret = null;
        if (!StringUtils.isEmpty(address))
        {
            ret = new CellAddress(address);
        }
        else
        {
            ret = new CellAddress(0, 0);
        }
        return ret;
    }

    /**
     * ????????? cell??? ???????????? ?????????. ?????? String
     * @param cell
     * @param data
     */
    public void setData(Cell cell, String data, boolean isMultiLine) {
        if(NumberUtils.isCreatable(data))
        {
            cell.setCellValue(Double.parseDouble(data));
        }
        else
        {
            if(isMultiLine) {
                cell.setCellStyle(_cs);
            }
            cell.setCellValue((String) data);
        }
    }

    /**
     * ????????? cell??? ???????????? ?????????.
     * @param rowIndex
     * @param colIndex
     * @param data
     */
    public void setData(int rowIndex, int colIndex, String data) {
        Row row = getRow(rowIndex);
        Cell cell = getCell(row, colIndex);
        setData(cell, data, false);
    }

    /**
     * ????????? cell??? ???????????? ?????????.
     * @param rowIndex
     * @param colIndex
     * @param data
     */
    public void setData(int rowIndex, int colIndex, String data, boolean isMultiline) {
        Row row = getRow(rowIndex);
        Cell cell = getCell(row, colIndex);
        setData(cell, data, isMultiline);
    }

    /**
     * ????????? cell??? ???????????? ?????????.
     * @param rowIndex
     * @param colIndex
     * @param data
     */
    public void setBackColor(int rowIndex, int colIndex, int data) {
        Row row = getRow(rowIndex);
        Cell cell = getCell(row, colIndex);
        CellStyle combined = _workbook.createCellStyle();

        combined.cloneStyleFrom(cell.getCellStyle());
        XSSFColor myColor = new XSSFColor(new java.awt.Color(data));
        ((XSSFCellStyle) combined).setFillForegroundColor(myColor);
        combined.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(combined);
    }

    /**
     * ?????? ????????? ????????????.
     */
    public void setColumnLength(int colIndex, double length) {
        int colSize = (int) ((length * 256) + 200);
        _selectedSheet.setColumnWidth(colIndex, colSize);
    }


    /**
     * ????????? cell??? ???????????? ?????????.
     * @param address
     * @param data
     */
    public void setData(String address, String data) {
        CellAddress addr = new CellAddress(address);
        Row row = getRow(addr.getRow());
        Cell cell = getCell(row, addr.getColumn());
        setData(cell, data, false);
    }


    /**
     * ????????? sheet??? ?????? Row????????? ?????????.
     * @param address
     * @param list
     * @return
     */
    public int setRowData(String address, List<String> list) {
        CellAddress addr = new CellAddress(address);
        int rowIndex = addr.getRow();
        int colIndex = addr.getColumn();

        for(String cellData : list) {
            setData(rowIndex, colIndex, cellData);
            colIndex++;
        }
        return rowIndex;
    }

    /**
     * ????????? sheet??? ?????? Row????????? ?????????.
     * @param rowIndex
     * @param colIndex
     * @param list
     * @return
     */
    public int setRowData(int rowIndex, int colIndex, List<String> list) {
        int col = colIndex;
        for(String cellData : list) {
            setData(rowIndex, col, cellData);
            col++;
        }
        return rowIndex;
    }

    /**
     * ?????? range??? ????????????.
     * @param fromRow
     * @param fromCol
     * @param toRow
     * @param toCol
     */
    public void setMerge(int fromRow, int fromCol, int toRow, int toCol) {
        CellRangeAddress addr = new CellRangeAddress(fromRow, toRow, fromCol, toCol);
        _selectedSheet.addMergedRegion(addr);
    }

    /**
     * data??? ????????????.
     * @param address
     * @return
     */
    public Object getData(String address) {
        CellAddress addr = new CellAddress(address);
        Row row = getRow(addr.getRow());
        Cell cell = getCell(row, addr.getColumn());
        return getData(cell);
    }

    /**
     * data??? ????????????.
     * @param rowIndex
     * @param colIndex
     * @return
     */
    public Object getData(int rowIndex, int colIndex) {
        Row row = getRow(rowIndex);
        Cell cell = getCell(row, colIndex);
        return getData(cell);
    }

    /**
     * data??? ????????????.
     * @param cell
     * @return
     */
    public Object getData(Cell cell) {
        Object ret = null;
        if(cell != null) {
            CellType type = cell.getCellType();

            switch(type) {
                case STRING:
                    ret = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    ret = cell.getNumericCellValue();
                    break;
                case BOOLEAN:
                    ret = cell.getBooleanCellValue();
                    break;
                case FORMULA:
                    ret = cell.getCellFormula();
                default:
                    ret = StringUtils.EMPTY;
                    break;
            }
        }
        return ret;
    }

    /**
     * ?????? Sheet??? ???????????? ??????.
     * @param sheetName
     * @param isHide
     */
    public void hideSheet(String sheetName, boolean isHide)
    {
        int index = _workbook.getSheetIndex(sheetName);
        if(_workbook.getActiveSheetIndex() == index && isHide) // sheet??? active ???????????? hide ????????? ?????????.
        {
            for (int i=0; i<_workbook.getNumberOfSheets(); i++) {
                if(index != i) {
                    _workbook.setActiveSheet(i);
                    _workbook.setSelectedTab(i); // ??????????????? ????????? ????????? ????????? ????????? ??????
                    break;
                }
            }
        }
        _workbook.setSheetHidden(index, isHide);
    }

    /**
     * ?????? Sheet??? ?????? ??????.
     * @param sheetName
     */
    public void removeSheet(String sheetName)
    {
        int index = _workbook.getSheetIndex(sheetName);
        _workbook.removeSheetAt(index);
    }

    /**
     * ?????? Sheet??? ???????????? ??????.
     * @param sheetName
     */
    public void hideSheet(String sheetName)
    {
        hideSheet(sheetName, true);
    }
    /**
     * ?????? ??????
     * @param filePath
     * @throws Exception
     */
    public void save(String filePath) throws Exception {
        try(FileOutputStream fileOut = new FileOutputStream(filePath))
        {
            _workbook.write(fileOut);
            fileOut.close();
        }
        catch(Exception ex){
            throw ex;
        }
    }

    public void getExcelStream(OutputStream out) throws IOException {
        _workbook.write(out);
        out.flush();
        out.close();
    }


    @Override
    public void close() throws Exception {
        dispose();
    }

    /**
     * ????????? ??????, ??????????????? workbook??? ?????????.
     * @throws IOException
     */
    private void dispose() throws IOException {
        if(_workbook != null) {
            _workbook.close();
        }
    }
}
