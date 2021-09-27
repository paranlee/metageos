package com.example.report.service;

import com.example.report.mapper.JobMapper;
import com.example.report.model.JobDto;
import com.example.report.model.ReportDto;
import com.example.report.util.ExcelSXSSFManager;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JobService {

    private final JobMapper jobMapper;

    /**
     * Find users by conditions
     * @return Jobs
     */
    public List<JobDto> getJobs(String flag) {
        List<JobDto> jobs = jobMapper.getJobs(flag);

        Integer cnt = jobs.size();
        msg(String.format("Report Queuing Num of %s ", cnt));
        jobs.stream().forEach(job -> {
            String form = String.format(
                "Do logPart: %d, LogSeq: %d, ",
                job.getLogPart(), job.getLogSeq());

            System.out.println(form);
        });

        return jobs;
    }

    public void msg (String msg) { // https://ryan-han.com/post/java/java-calendar-date/
        LocalDateTime today = LocalDateTime.now();
        ZoneId zid = ZoneId.of("Asia/Seoul");
        ZonedDateTime zdt = today.atZone(zid);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        msg = String.format("Do JOB at <%s> : %s", formatter.format(zdt.toLocalDateTime()), msg);
        System.out.println(msg);
    }
}
