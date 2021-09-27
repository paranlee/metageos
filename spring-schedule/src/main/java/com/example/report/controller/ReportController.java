package com.example.report.controller;

import static lombok.AccessLevel.PRIVATE;

import com.example.report.mapper.JobMapper;
import com.example.report.mapper.ReportMapper;
import com.example.report.model.ReportDto;
import io.swagger.annotations.Api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "REST API for Report")
@RestController
@RequestMapping("api")
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReportController {

    ReportMapper reportMapper;

    /**
     * Select Spots
     *
     * @return
     */
    @GetMapping("reports")
    public List<Map> getSpots() {
        return reportMapper.getReports();
    }

    /**
     * Select report queue entry
     *
     * @param seq
     * @return
     */
    @GetMapping("report")
    public Map<String, Object> getReport(@RequestParam final Integer seq) {
        return reportMapper.getReport(seq);
    }

    /**
     * get report sheet
     *
     * @param reportDto
     */
    @PostMapping("report/sheet")
    public List<Map> getSheet(@RequestBody final ReportDto reportDto) {
        /*
         * sheet number or sheet type?
         * */
        return reportMapper.getSheet(reportDto);
    }

    /**
     * Insert report queue
     *
     * @param reportDto
     */
    @PostMapping("report")
    public Integer setReport(@RequestBody final ReportDto reportDto) {

        return reportMapper.setReport(reportDto);
    }

    /**
     * Update report queue
     *
     * @param reportDto
     */
    @PostMapping("report/update")
    public Integer updateSpot(@RequestBody final ReportDto reportDto) {

        return reportMapper.updateReport(reportDto);
    }

    /**
     * Delete report queue
     *
     * @param reportDto
     */
    @PostMapping("report/delete")
    public void deleteReport(@RequestBody final ReportDto reportDto) {

        reportMapper.deleteReport(reportDto);
    }
}
