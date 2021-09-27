package com.example.report.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import static lombok.AccessLevel.PRIVATE;

@ApiModel(value = "Job")
@Data
@FieldDefaults(level = PRIVATE)
public class JobDto {
    Long reportSeq;
    Integer reportType;
    String usrId;
    String filePath;
    String fileNm;
    Integer logPart;
    Integer logSeq;
    String err;

    /* R (uploaded)
     * ├── A
     * └── F TODO: is reprocess? If then selective reporcess form REST API or Chatbot
     * */
    String flag;

    @Override
    public String toString() {
        String text = String.format(
            "<JobDto> file: %s, reportSeq: %s, reportType: %s, logPart: %s, logSeq: %s",
                (this.getFilePath() + "/" + this.getFileNm()),
                this.getReportSeq(), this.getReportType(), this.getLogPart(), this.getLogSeq());
        return text;
    }

    public boolean isGood() {
        boolean good = this.flag == "A" && StringUtils.isEmpty(this.err);
        return good;
    }

}