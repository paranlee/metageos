package com.example.report.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.desair.tus.server.upload.UploadInfo;

import java.nio.file.Path;

import static lombok.AccessLevel.PRIVATE;

@ApiModel(value = "File")
@Data
@FieldDefaults(level = PRIVATE)
public class FileDto {
    UploadInfo uploadInfo;
    String uri;
    Integer code;
    Path path;
    String usrId;
    String fileName;
    String fileSize;
}