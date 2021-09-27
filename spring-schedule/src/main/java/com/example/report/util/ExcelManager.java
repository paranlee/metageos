package com.example.report.util;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData.Series;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/***
 * ExcelBase 이외의 함수를 정의한다. 커스텀
 * @author shin
 */
public class ExcelManager extends ExcelBase {

    /***
     * @param templatePath
     */
    public ExcelManager(String templatePath) throws Exception {
        super(templatePath);
    }

    /***
     * 첫번째 Row의 값을 array순서에 맞게 작성한다.
     */
    public void setRowToArray(String[] addrList, List<LinkedHashMap<String, Object>> dataList) {
        try {
            if (!dataList.isEmpty()) {
                LinkedHashMap<String, Object> row = dataList.get(0);
                int index = 0;
                for (Object data : row.values())
                {
                    CellAddress startCell = getAddressStartCell(addrList[index]);
                    int rowIndex = startCell.getRow();
                    int colIndex = startCell.getColumn();
                    setData(rowIndex, colIndex, data.toString());
                    index++;
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    /***
     * datatable의 첫 번째 컬럼의 값을 array순서에 맞게 작성한다.
     */
    public void setDataTableToArray(String[] addrList, List<LinkedHashMap<String, Object>> dataList) {
        try {
            int index = 0;
            for (LinkedHashMap<String, Object> row : dataList) {
                CellAddress startCell = getAddressStartCell(addrList[index]);
                int rowIndex = startCell.getRow();
                int colIndex = startCell.getColumn();
                Object data = Iterables.getFirst(row.values(), null);
                if(data != null)
                {
                    setData(rowIndex, colIndex, data.toString());
                }
                index++;
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        
    }

    /***
     * datatable의 데이터를 엑셀에 작성한다.
     */
    public CellAddress setDataTable(String useAddr, List<Map> dataList) {
        CellAddress ret = null;
        try {
            CellAddress startCell = getAddressStartCell(useAddr);
    
            int rowIndex = startCell.getRow();
            int colIndex = startCell.getColumn();
    
            for (Map row : dataList)
            {
                colIndex = startCell.getColumn();
                for (Object data : row.values())
                {
                    setData(rowIndex, colIndex, data.toString());
                    colIndex++;
                }
                rowIndex++;
            }
            ret = new CellAddress((rowIndex - 1), (colIndex - 1));
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
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
                for (Object data : row.values())
                {
                    setData(rowIndex, colIndex, data.toString());
                    rowIndex++;
                }
                colIndex++;
            }
    
            ret = new CellAddress((rowIndex - 1), (colIndex - 1));
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return ret;
    }
    
    /***
     * 템플릿의 차트 스타일을 토대로 하여 차트 작성
     */
    public void setChart(String chartDataAddr, String sheetName, String chartName,
            List<Map> dataList) {
        try {
            XSSFChart templateChart = getChart(sheetName, chartName);
            if(templateChart != null) {
                String[] addr = chartDataAddr.split("!"); // 시트이름!주소
                
                String chartDataSheetName = StringUtils.EMPTY;
                String chartDatastartAddr = StringUtils.EMPTY;
                
                if (addr.length > 1) {
                    chartDataSheetName = addr[0];
                    chartDatastartAddr = addr[1];
                    setSelectedSheet(chartDataSheetName);
                } else {
                    chartDataSheetName = sheetName;
                    setSelectedSheet(sheetName);
                }
                setDataTable(chartDatastartAddr, dataList); // ChartData에 데이터 작성
                LinkedHashMap<Integer, CellRangeAddress> dic = getChartSeriesAddress(chartDatastartAddr, dataList); // dt를 기준으로 Series Address 리스트 가져오기
                setChartSeries(templateChart, chartDataSheetName, dic);
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
    
    /***
     * DataTable로 차트 Series의 주소값 만들기
     */
    private LinkedHashMap<Integer, CellRangeAddress> getChartSeriesAddress(String startAddr, List<Map> dataList) {
        LinkedHashMap<Integer, CellRangeAddress> dic = null;
        try {
            dic = new LinkedHashMap<Integer, CellRangeAddress>();
            CellAddress cellAddress = getAddressStartCell(startAddr);
            
            int rowCount = dataList.size();
            int colCount = 0;
            Map firstRow = Iterables.getFirst(dataList, null);
            if(firstRow != null) {
                colCount = firstRow.size();    
            }
            
            for (int i = 0; i < colCount; i++) {
                int fromRow = cellAddress.getRow();
                int fromCol = cellAddress.getColumn() + i;
                int toRow = cellAddress.getRow() + rowCount - 1;
                int toCol = cellAddress.getColumn() + i;
                
                CellRangeAddress r = new CellRangeAddress(fromRow, toRow, fromCol, toCol);
                dic.put(i, r);
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return dic;
    }

    /***
     * 차트에 각 Series별 Address 입력
     */
    private void setChartSeries(XSSFChart chart, String sheetName, LinkedHashMap<Integer, CellRangeAddress> dic)
    {
        try {
            //int count = dic.size();
            if(chart.getChartSeries().size() >= 1) {
                //int seriesSize = chart.getChartSeries().size(); // series 크기 가져오기
                List<XDDFChartData> chartDataList = chart.getChartSeries();
                //chart.getChartSeries()
                XSSFSheet sheet = getSheet(sheetName);
                CellRangeAddress catAddress = dic.get(0);
                XDDFDataSource<?> cat = XDDFDataSourcesFactory.fromStringCellRange(sheet, catAddress);
                
                int dataSeriesIndex = 1; // dic의 첫번째는 x축이기 때문에 plus 1
                for(XDDFChartData chartData : chartDataList) {
                    //XDDFChartData chartData = chart.getChartSeries().get(seriesIndex); // series 데이터 가져오기
                    Series series = chartData.getSeries(0); // chart data당 series는 한개다
                    XDDFNumericalDataSource<?> val = XDDFDataSourcesFactory.fromNumericCellRange(sheet, dic.get(dataSeriesIndex));
                    //System.out.println(dic.get(dataSeriesIndex));
                    series.replaceData(cat, val);
                    series.plot();
                    dataSeriesIndex++;
                }                                
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
}
