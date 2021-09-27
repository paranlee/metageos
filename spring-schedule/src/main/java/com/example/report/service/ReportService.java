package com.example.report.service;

import com.example.report.mapper.JobMapper;
import com.example.report.mapper.ReportMapper;
import com.example.report.model.JobDto;
import com.example.report.model.ReportDto;
import com.example.report.util.ExcelManager;
import com.example.report.util.ExcelSXSSFManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@RequiredArgsConstructor
@Service
public class ReportService {

    @Value("${report.template}")
    public Path templatePath;

    @Value("${file.download-path}")
    public Path downloadDir;

    private final ReportMapper reportMapper;

    public void msg (String msg) { // https://ryan-han.com/post/java/java-calendar-date/
        LocalDateTime today = LocalDateTime.now();
        ZoneId zid = ZoneId.of("Asia/Seoul");
        ZonedDateTime zdt = today.atZone(zid);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        msg = String.format("Do Report at <%s> %s", formatter.format(zdt.toLocalDateTime()), msg);
        System.out.println(msg);
    }

    public ReportDto doReport(JobDto j, ReportDto r) {
        String sheetName = r.getSheetName();
        String cell = r.getExcelCell();

        String queryKey = r.getQueryKey(); // MyBatis Mapper Method Name
        String doing = r.getDoing(); // Excel Manager Method Name

        String str = r.toString();
        try {
            System.out.println(str);
            // invoke queryKey
            // https://kaspyx.tistory.com/80
            // Class myClass = ReportMapper.class();
            List<Map> dt = invokeMybatis(j, queryKey);

            CellAddress ca = invokePoi(sheetName, cell, doing, dt);
            msg(String.format("Complete: ", ca.toString(), str));
        } catch (Exception ex) {
            msg(String.format("ERROR: %s %s", str, ex.toString()));
        }

        return r;
    }

    public List<Map> invokeMybatis(final JobDto j, String queryKey)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<ReportMapper> reportMapperClass = ReportMapper.class;
        Class<JobDto> jobDtoClass = JobDto.class;
        Method mybatis = reportMapperClass.getMethod(queryKey, jobDtoClass);
        List<Map> dt = (List<Map>) mybatis.invoke(reportMapper, j);
        return dt;
    }

    public CellAddress invokePoi(
            String sheetName, String cell, String doing,
            List<Map> dt)
            throws Exception {
        // TODO: For Mutithreaded Jobs.
        ExcelSXSSFManager excelManager = new ExcelSXSSFManager(templatePath);
        excelManager.setSelectedSheet(sheetName);

        Class<ExcelSXSSFManager> excemMan = ExcelSXSSFManager.class;
        Method poi = excemMan.getMethod(doing);

        // setDataTable
        CellAddress ca = (CellAddress) poi.invoke(excelManager, cell, dt);
        return ca;
    }

    public JobDto templatingJob(final JobDto j, List<ReportDto> rs) throws Exception {
        String fileName = j.getFileNm();
        String ts = String.format("%s/%s", downloadDir.toString(), fileName);

        String ss = templatePath.toString();
        Path source = Paths.get(ss);
        Path target = Paths.get(ts);

        msg(String.format("COPY FILES: %s <- %s ", ts, ss));
        if (!Files.exists(target.getParent())) {
            Files.createDirectories(target.getParent());
        }

        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        ExcelManager excelManager = new ExcelManager(ts);

        rs.stream().forEach(r -> {

            List<Map> dt = null;
            try {
                dt = invokeMybatis(j, r.getQueryKey());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                j.setErr(e.getMessage());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                j.setErr(e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                j.setErr(e.getMessage());
            }

            if(dt.isEmpty()) {
                return;
            }

            dt.stream().forEach(d -> {
                System.out.println(d.toString());
            });

            excelManager.setSelectedSheet(r.getSheetName());
            excelManager.setDataTable(r.getExcelCell(), dt);
        });

        try {
            excelManager.save(ts);
            j.setFlag("A");
        } catch (Exception e) {
            j.setFlag("F");
            j.setErr(e.getMessage());
            e.printStackTrace();
        }

        return j;
    }

    public ReportDto templatingTable(JobDto j, ReportDto r, List<Map> dt) throws Exception {
        String ss = templatePath.toString();

        String fileName = j.getFileNm();
        String ts = String.format("%s/%s", downloadDir.toString(), fileName);

        Path source = Paths.get(templatePath.toString());
        Path target = Paths.get(ts);

        msg(String.format("COPY FILES: %s -> %s ", ss, ts));
        if (!Files.exists(target.getParent())) {
            Files.createDirectories(target.getParent());
        }

        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        // try(ExcelSXSSFManager excelManager = new ExcelSXSSFManager(ts)) {
         try(ExcelManager excelManager = new ExcelManager(ts)) {
            excelManager.setSelectedSheet(r.getSheetName());
            excelManager.setDataTable(r.getExcelCell(), dt);
            excelManager.save(ts);
            excelManager.close();
        } catch(Exception ex) {
            System.out.println(ex.toString());
        }

        return r;
    }

}
