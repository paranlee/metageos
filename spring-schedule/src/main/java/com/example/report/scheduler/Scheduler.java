package com.example.report.scheduler;

import com.example.report.controller.ChatController;
import com.example.report.mapper.JobMapper;
import com.example.report.mapper.ReportMapper;
import com.example.report.model.ChatMessage;
import com.example.report.model.JobDto;
import com.example.report.model.ReportDto;
import com.example.report.service.JobService;
import com.example.report.service.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Scheduler {

    final JobService jobService;
    final ReportService reportService;

    final JobMapper jobMapper;
    final ReportMapper reportMapper;

    public Scheduler(JobService jobService, ReportService reportService,
            ReportMapper reportMapper, JobMapper jobMapper) {
        this.jobService = jobService;
        this.reportService = reportService;
        this.jobMapper = jobMapper;
        this.reportMapper = reportMapper;
    }

    @Scheduled(cron = "* * * 1 * *")
    public void printDate() { // https://ryan-han.com/post/java/java-calendar-date/
        reportService.msg("I am Alive!");
        /*ChatController test = new ChatController();
        ChatMessage mm = new ChatMessage();
        test.sendMessage(mm);*/
    }

    @Scheduled(cron = "3 * * * * *")
    public void batchJob() {
        List<JobDto> js = jobService.getJobs("R");

        js.stream().forEach(j -> {
            List<ReportDto> rs = jobMapper.getJob(j);
            try {
                reportService.templatingJob(j, rs);
            } catch (Exception e) {
                j.setFlag("F");
                j.setErr(e.getMessage());
                e.printStackTrace();
            }

            reportService.msg(String.format("FINISH JOB: %s", j.toString()));

            jobMapper.updateJob(j);

            reportService.msg(String.format("UPDATE STATE: %s %s", j.getFlag(), j.getFileNm()));
            if(!j.isGood()) {
                reportService.msg(String.format("IS ERROR? : %s %s", j.getFlag(), j.getErr()));
            }
        });
    }
}