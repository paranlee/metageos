package com.example.report.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/***
 * ExcelBase, Excel에 대한 공통 함수만 정의
 * @author shin
 */
public class ExcelBase implements AutoCloseable {
    protected XSSFSheet _selectedSheet = null;
    protected XSSFWorkbook _workbook = null;
    protected CreationHelper _helper = null;

    /***
     * @see 생성자
     */
    public ExcelBase() { }

    /***
     * @see 생성자, file 읽을 때 사용
     */
    public ExcelBase(String filePath) throws Exception {
        initReadFile(filePath);
    }

    private void initReadFile(String filePath) throws Exception {
        try(FileInputStream file = new FileInputStream(filePath))
        {
            _workbook = new XSSFWorkbook(file);
            _helper = _workbook.getCreationHelper();
        }
        catch(Exception ex)
        {
            throw ex;
        }    
    }

    /**
     * @see sheet를 생성한다.
     * @param sheetName
     */
    public void addSheet(String sheetName) {
        _workbook.createSheet(sheetName);
    }

    /**
     * @see 수정할 sheet를 선택한다. 
     * @param sheetName
     */
    public void setSelectedSheet(String sheetName) {
        _selectedSheet = getSheet(sheetName);
    }

    /***
     * @see sheetName으로 sheet를 찾아 리턴한다. 
     */
    public XSSFSheet getSheet(String sheetName) {
        return _workbook.getSheet(sheetName);
    }

    /*
     * @see 수정할 sheet를 선택한다. 
     * @param sheetName
     */
    public void setSelectedSheet(int index) {
        _selectedSheet = _workbook.getSheetAt(index);
    }

    /***
     * @see 시트 복사 
     * @param sheetName
     * @param newSheetName
     */
    public void copySheet(String sheetName, String newSheetName) {
        int index = _workbook.getSheetIndex(sheetName);
        XSSFSheet newSheet = _workbook.cloneSheet(index);
        int newIndex = _workbook.getSheetIndex(newSheet);
        _workbook.setSheetName(newIndex, newSheetName);
    }

    /**
     * @see 현재 선택되어있는 시트를 가져온다.
     * @param sheetName
     * @return 선택된 시트
     */
    public XSSFSheet getSeletedSheet() {
        return _selectedSheet;
    }

    /**
     * @see 시트이름을 변경한다.
     * @param sheetName
     * @param newSheetName
     */
    public void changeSheetName(String sheetName, String newSheetName) {
        int index = _workbook.getSheetIndex(sheetName);
        _workbook.setSheetName(index, newSheetName);
    }

    /**
     * @see 선택된 시트에서 row를 가져온다. 생성되지 않았다면 생성해준 후 가져온다.
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
     * @see 선택된 시트 및 Row에서 하나의 cell을 가져온다. 생성되지 않았다면 생성해준 후 가져온다.
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
     * @see row,column to address 
     */
    public String getCellAddressStr(int row, int column) {
        return new CellAddress(row, column).toString();
    }

    /***
     * @see address to cell row, column 
     */
    public CellAddress getAddressStartCell(String address) {
        CellAddress ret = null;
        if (!StringUtils.isEmpty(address)) {
            ret = new CellAddress(address);
        } else {
            ret = new CellAddress(0, 0);
        }
        return ret;
    }

    /**
     * @see 하나의 cell에 데이터를 넣는다. 기본 String
     * @param cell
     * @param data
     */
    public void setData(Cell cell, String data) {
        if(StringUtils.isNumeric(data))
        {
            cell.setCellValue(Double.parseDouble(data));
        }
        else
        {
            cell.setCellValue((String) data);
        }
    }

    /**
     * @see 하나의 cell에 데이터를 넣는다.
     * @param rowIndex
     * @param colIndex
     * @param data
     */
    public void setData(int rowIndex, int colIndex, String data) {
        Row row = getRow(rowIndex);
        Cell cell = getCell(row, colIndex);
        setData(cell, data);
    }

    /**
     * @see 하나의 cell에 데이터를 넣는다.
     * @param address
     * @param data
     */
    public void setData(String address, String data) {
        CellAddress addr = new CellAddress(address);
        Row row = getRow(addr.getRow());
        Cell cell = getCell(row, addr.getColumn());
        setData(cell, data);
    }

    /**
     * @see 선택된 sheet의 값을 Row단위로 넣는다.
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
     * @see 선택된 sheet의 값을 Row단위로 넣는다.
     * @param address
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
     * @see 해당 range를 머지한다.
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
     * @see data를 가져온다.
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
     * @see data를 가져온다.
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
     * @see data를 가져온다.
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
     * @see 해당 Sheet를 숨김처리 한다.
     * @param sheetName
     * @param isHide
     */
    public void hideSheet(String sheetName, boolean isHide) {
        int index = _workbook.getSheetIndex(sheetName);
        if(_workbook.getActiveSheetIndex() == index && isHide) // sheet가 active 상태이면 hide 처리가 안된다. 
        {
            for (int i=0; i<_workbook.getNumberOfSheets(); i++) {
                if(index != i) {
                    _workbook.setActiveSheet(i);
                    _workbook.setSelectedTab(i); // 정상적으로 선택이 안되는 문제가 있어서 추가
                    break;
                }
            }
        }
        _workbook.setSheetHidden(index, isHide);
    }

    /**
     * @see 해당 Sheet를 제거 한다.
     * @param sheetName
     * @param isHide
     */
    public void removeSheet(String sheetName) {
        int index = _workbook.getSheetIndex(sheetName);
        _workbook.removeSheetAt(index);
    }

    /**
     * @see 해당 Sheet를 숨김처리 한다.
     * @param sheetName
     * @param isHide
     */
    public void hideSheet(String sheetName) {
        hideSheet(sheetName, true);
    }

    /***
     * chart 이름으로 chart를 가져온다. 
     */
    public XSSFChart getChart(String sheetName, String chartName) {
        XSSFChart ret = null;
        XSSFSheet sheet = getSheet(sheetName);
        XSSFDrawing xssfDrawing = sheet.getDrawingPatriarch();
        for(XSSFShape xssfShape : xssfDrawing.getShapes()) {
            List<XSSFChart> charts = xssfShape.getDrawing().getCharts();
            for(XSSFChart chart : charts) {
                if (StringUtils.equalsIgnoreCase(chart.getGraphicFrame().getName(), chartName)) {
                    ret = chart;
                    break;
                }
            }
        }
        return ret;
    }
    
    /***
     * @see Image를 추가한다. column 크기에 맞게 추가된다.
     * @param filePath
     * @param fromRow
     * @param fromCol
     * @param toRow
     * @param toCol
     * @throws Exception
     */
    public void insertImage(String filePath, int fromRow, int fromCol, int toRow, int toCol) throws Exception {
         //FileInputStream obtains input bytes from the image file
        try(InputStream inputStream = new FileInputStream(filePath)) {
            byte[] bytes = IOUtils.toByteArray(inputStream); //Get the contents of an InputStream as a byte[].
            int pictureIdx = _workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);    //Adds a picture to the workbook
            inputStream.close(); //close the input stream
            @SuppressWarnings("rawtypes")
            Drawing drawing = _selectedSheet.createDrawingPatriarch(); //Creates the top-level drawing patriarch.
            ClientAnchor anchor = _helper.createClientAnchor(); //Create an anchor that is attached to the worksheet

            //create an anchor with upper left cell _and_ bottom right cell
            anchor.setCol1(fromCol);
            anchor.setRow1(fromRow);
            anchor.setCol2(toCol);
            anchor.setRow2(toRow);

            //#1 테이블 셀의 너비에 의존적이지않게 사이즈조절.
            anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

            //Creates a picture
            //Picture picture =
            drawing.createPicture(anchor, pictureIdx);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    /***
     * @see Image를 추가한다. column 크기에 맞게 추가된다.
     * @param filePath
     * @param fromRow
     * @param fromCol
     * @param toRow
     * @param toCol
     * @throws Exception
     */
    public void insertImage(String filePath, int fromRow, int fromCol) throws Exception {
         //FileInputStream obtains input bytes from the image file
        try(InputStream inputStream = new FileInputStream(filePath)) {
            byte[] bytes = IOUtils.toByteArray(inputStream); //Get the contents of an InputStream as a byte[].
            int pictureIdx = _workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);    //Adds a picture to the workbook
            inputStream.close(); //close the input stream
            @SuppressWarnings("rawtypes")
            Drawing drawing = _selectedSheet.createDrawingPatriarch(); //Creates the top-level drawing patriarch.
            ClientAnchor anchor = _helper.createClientAnchor(); //Create an anchor that is attached to the worksheet

            //create an anchor with upper left cell _and_ bottom right cell
            anchor.setCol1(fromCol);
            anchor.setRow1(fromRow);

            //#1 테이블 셀의 너비에 의존적이지않게 사이즈조절.
            anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

            //Creates a picture
            Picture picture =
            drawing.createPicture(anchor, pictureIdx);

            picture.resize();
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    /***
     * @see Image를 추가한다.
     */
    public void insertImage(byte[] bytes, String rangeAddress) throws Exception {
        CellRangeAddress addr =CellRangeAddress.valueOf(rangeAddress);
        int pictureId = _workbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
        XSSFDrawing drawing = _selectedSheet.createDrawingPatriarch();
        XSSFClientAnchor myAnchor = new XSSFClientAnchor();
        myAnchor.setDx1(0);
        myAnchor.setCol1(addr.getFirstColumn());
        myAnchor.setRow1(addr.getFirstRow());
        myAnchor.setCol2(addr.getLastColumn() + 1);
        myAnchor.setRow2(addr.getLastRow() + 1);
        myAnchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
        drawing.createPicture(myAnchor, pictureId);
       //picture.resize();
    }
    
    /***
     * @see Image를 추가한다. column 크기에 맞게 추가된다.
     */
    public void insertImage(byte[] bytes, int fromRow, int fromCol, int toRow, int toCol) throws Exception {
        int pictureIdx = _workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        @SuppressWarnings("rawtypes")
        Drawing drawing = _selectedSheet.createDrawingPatriarch();
        ClientAnchor anchor = _helper.createClientAnchor();
        anchor.setCol1(fromCol);
        anchor.setRow1(fromRow);
        anchor.setCol2(toCol);
        anchor.setRow2(toRow);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
        drawing.createPicture(anchor, pictureIdx);
    }
    
    /**
     * @see 저장
     * @param filePath
     * @throws Exception
     */
    public void save(String filePath) throws Exception {
        try(FileOutputStream fileOut = new FileOutputStream(filePath)) {
            _workbook.write(fileOut);
            fileOut.close();
        } catch(Exception ex) {
            throw ex;
        }
    }

    @Override
    public void close() throws Exception {
        dispose();
    }
    
    /**
     * @see 메모리 해제, 생성되었던 workbook을 닫는다.
     * @throws IOException
     */
    private void dispose() throws IOException {
        if(_workbook != null) {
            _workbook.close();
        }
    }
}
