package com.example.report.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApiModel(value = "Report")
@Data
@FieldDefaults(level = PRIVATE)
public class ReportDto {

    String sheetName;
    String excelCell;
    String doing;
    String queryKey;
    String arg;

    @Override
    public String toString() {
        String text = String.format(
                "<ReportDto> sheetName: %s, excelCell: %s, doing: %s, queryKey: %s",
                this.getSheetName(), this.getExcelCell(), this.getDoing(), this.getQueryKey());
        return text;
    }

}