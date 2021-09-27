package com.example.report.mapper;

import com.example.report.model.JobDto;
import com.example.report.model.ReportDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JobMapper {
    List<JobDto> getJobs(String flag);

    void updateJob(JobDto job);

    List<ReportDto> getJob(JobDto job);
    List<ReportDto> getJob(Integer reportType);
}
