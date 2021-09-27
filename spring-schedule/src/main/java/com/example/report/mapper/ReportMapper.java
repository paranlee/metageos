package com.example.report.mapper;

import com.example.report.model.JobDto;
import com.example.report.model.ReportDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface ReportMapper {
    List<Map> getReports();
    Map getReport(Integer seq);

    List<Map> getSheet(ReportDto report);

    Integer setReport(final ReportDto report);
    Integer updateReport(final ReportDto report);
    Integer deleteReport(final ReportDto report);

    List<Map> getDummyTable(final JobDto job);
}
