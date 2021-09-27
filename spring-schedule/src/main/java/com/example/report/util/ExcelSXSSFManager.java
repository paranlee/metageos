package com.example.report.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.example.report.model.ReportDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellAddress;

/***
 * ExcelBase 이외의 함수를 정의한다.
 * @author shin
 */
public class ExcelSXSSFManager extends ExcelBaseSXSSF {

    LinkedHashMap<Integer, Integer> mapXaxColumnWidths = new LinkedHashMap<>();

    public ExcelSXSSFManager() throws Exception {
        super();
    }

    public ExcelSXSSFManager(Path templatePath) throws Exception {
        super(templatePath);
    }

    public ExcelSXSSFManager(String templatePath) throws Exception {
        super(templatePath);
    }

    private void setMaxColumnWidths(int columnIndex, String data){
        if(mapXaxColumnWidths.containsKey(columnIndex)){
            int len = mapXaxColumnWidths.get(columnIndex);
            if(len < data.length()){
                mapXaxColumnWidths.put(columnIndex, data.length());
            }
        } else {
            mapXaxColumnWidths.put(columnIndex, data.length());
        }
    }

    public void copyTemplateFile(Path template, Path dir)
            throws IOException {
        File directory = new File(dir.toString());
        if (!directory.exists()){
            directory.mkdir();
        }
        Files.copy(template, dir, StandardCopyOption.REPLACE_EXISTING);
    }

    private void copyTemplateFile(String templatePath, String outputFilePath)
        throws IOException {
        Path path = Paths.get(outputFilePath);
        String dirPath = path.getParent().toFile().getAbsolutePath();
        File directory = new File(dirPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        Files.copy(new File(templatePath).toPath(), new File(outputFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }


    /***
     * datatable의 데이터를 엑셀에 작성한다.
     */
    public CellAddress setDataTable(String useAddr, List<Map> dt) {
        CellAddress ret = null;
        System.out.println(String.format("%s : ", useAddr));
        try {
            CellAddress startCell = getAddressStartCell(useAddr);

            int rowIndex = startCell.getRow();
            int colIndex = startCell.getColumn();

            for (Map row : dt) {
                if(row.isEmpty()) {
                    continue;
                }
                //LinkedHashMap<Object, String> m = (LinkedHashMap<Object, String>) row;
                System.out.println(String.format("row: %s : ", row.toString()));

                colIndex = startCell.getColumn();
                Collection vals = row.values();
                for (Object data : vals) {
                    String val = Objects.toString(data, StringUtils.EMPTY);
                    System.out.println(String.format("val: %s : ", val));
                    setData(rowIndex, colIndex, val);
                    setMaxColumnWidths(colIndex, val);
                    colIndex++;
                }
                rowIndex++;
            }

            ret = new CellAddress((rowIndex - 1), (colIndex - 1));
        } catch(Exception ex) {
            throw ex;
        }

        return ret;
    }

    private void setDataWithMultiline(String data, int colIndex){
        String[] tmpStr = data.split("\n");
        if(tmpStr.length > 1) {
            if(tmpStr[0].length() > tmpStr[1].length()) {
                setMaxColumnWidths(colIndex, tmpStr[0]);
            } else {
                setMaxColumnWidths(colIndex, tmpStr[1]);
            }

        } else {
            setMaxColumnWidths(colIndex, tmpStr[0]);
        }
    }

    /***
     * datatable의 데이터를 엑셀에 작성한다.
     */
    public CellAddress setArrayToRow(String useAddr, String[] dataList, boolean isMultiline) {
        CellAddress ret = null;
        try {
            CellAddress startCell = getAddressStartCell(useAddr);

            int rowIndex = startCell.getRow();
            int colIndex = startCell.getColumn();

            for (String data : dataList) {
                String tmp = Objects.toString(data, StringUtils.EMPTY);
                setData(rowIndex, colIndex, tmp, isMultiline);
                if(isMultiline){
                    setDataWithMultiline(tmp, colIndex);
                }else{
                    setMaxColumnWidths(colIndex, tmp);
                }
                colIndex++;
            }
            ret = new CellAddress((rowIndex - 1), (colIndex - 1));
        } catch(Exception ex) {
            throw ex;
        }
        return ret;
    }


    /***
     * datatable의 데이터를 엑셀에 작성한다.(행렬변환)
     */
    public CellAddress setDataTableWithRowToColumn(String useAddr, List<LinkedHashMap<String, Object>> dataList) {
        CellAddress ret = null;
        try {
            CellAddress startCell = getAddressStartCell(useAddr);

            int rowIndex = startCell.getRow();
            int colIndex = startCell.getColumn();

            for (LinkedHashMap<String, Object> row : dataList) {
                rowIndex = startCell.getRow();
                if(row != null) {
                    for (Object data : row.values()) {
                        setData(rowIndex, colIndex, Objects.toString(data, StringUtils.EMPTY));
                        rowIndex++;
                    }
                }
                colIndex++;
            }

            ret = new CellAddress((rowIndex - 1), (colIndex - 1));
        } catch(Exception ex) {
            throw ex;
        }
        return ret;
    }


    /***
     * datatable의 데이터를 엑셀에 작성한다.
     */
    public void setAutoColumnSize() {
        try {
            for (Map.Entry<Integer,Integer> entry : mapXaxColumnWidths.entrySet()) {
                Integer colIndex = entry.getKey();
                Integer len = entry.getValue();
                if(len > 5) {
                    int width = ((int)(len * 1.14388)) * 256;
                    _selectedSheet.setColumnWidth(colIndex, width);
                }

            }
        } catch(Exception ex) {
            throw ex;
        }
    }

    /***
     * lock 설정
     */
    public void createFreezePane(int col, int row){
        _selectedSheet.createFreezePane(col, row);
    }

    /***
     * datatable의 데이터를 엑셀에 작성한다.
     */
    public CellAddress setDataTableColumnSize(String useAddr, List<LinkedHashMap<String, Object>> dataList) {
        CellAddress ret = null;
        try {
            CellAddress startCell = getAddressStartCell(useAddr);

            int rowIndex = startCell.getRow();
            int colIndex = startCell.getColumn();
            for (LinkedHashMap<String, Object> row : dataList) {
                colIndex = startCell.getColumn();
                if(row != null) {
                    for (Object data : row.values())
                    {
                        if(data != null) {
                            setColumnLength(rowIndex, (float)data);
                        }
                        colIndex++;
                    }
                }
                rowIndex++;
            }
            ret = new CellAddress((rowIndex - 1), (colIndex - 1));
        } catch(Exception ex) {
            throw ex;
        }
        return ret;
    }

    public void trackAllColumnsForAutoSizing(){
        _selectedSheet.trackAllColumnsForAutoSizing();
    }

    public void setDataTableAutoSize(String useAddr, List<LinkedHashMap<String, Object>> dataList) {
        try {
            CellAddress startCell = getAddressStartCell(useAddr);

            int rowIndex = startCell.getRow();
            int colIndex = startCell.getColumn();

            for (LinkedHashMap<String, Object> row : dataList) {
                colIndex = startCell.getColumn();
                if(row != null) {
                    for (Object data : row.values()) {
                        if(data != null) {
                            this._selectedSheet.autoSizeColumn(colIndex);
                        }
                        colIndex++;
                    }
                }
                break;
            }
        } catch(Exception ex) {
            throw ex;
        }
    }

}
