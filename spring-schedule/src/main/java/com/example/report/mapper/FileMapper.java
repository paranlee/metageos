package com.example.report.mapper;

import com.example.report.model.FileDto;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface FileMapper {

    Map<String, Object> getSetting(@Param("usrId") String usrId, @Param("name") String name);

    Integer download(FileDto param);

    /**
     * insert upload info
     *
     * @param upload
     * @return
     */
    int add(FileDto upload);

    Integer csv2db(final String usrId, final String columns, final String path);
    Integer csv2std2db(final String fileName, final String columns, final String path);
    Integer csv2file2db(final String fileName, final String columns, final String path);

    Integer csv2call(final String path);
    Integer csv2scan(final String path);
}