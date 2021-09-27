package com.example.report.service;

import com.example.report.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 다운로드 관련 서비스 Static 하게 생성한 Service 클래스를 사용한다.
 */

@RequiredArgsConstructor
@Service
public class FileDownloadService {

    @Value("${tus.server.data.directory}")
    private Path tusPath;

    @Value("${file.download-path}")
    private Path downloadDir;

    public Path getTusPath() {
        return tusPath;
    }

    public Path getDownloadDir() {
        return downloadDir;
    }

    public Integer download(String fileName, HttpServletResponse response, Path path) throws IOException {
        Integer res = 0;
        String filePath = path.toString();
        InputStream file = new FileInputStream(filePath);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        int readBytes = 0;
        byte[] toDownload = new byte[100];
        OutputStream downloadStream = response.getOutputStream();

        while((readBytes = file.read(toDownload))!= -1){
            downloadStream.write(toDownload, 0, readBytes);
            downloadStream.flush();
        }

        downloadStream.close();
        res++;

        return res;
    }

    public Integer downloadZip(String fileName, HttpServletResponse response, Path uploadPath, Path downloadPath) throws IOException {
        Integer res = 0;
        String uploadFilePath = uploadPath.toString();

        File dir = new File(uploadFilePath);
        FileFilter fileFilter = new WildcardFileFilter(fileName + "*.csv");
        File[] files = dir.listFiles(fileFilter);

        String zipFile = downloadPath + "/" + fileName + ".zip";

        try {
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (int i = 0; i < files.length; i++) {
                FileInputStream fis = new FileInputStream(files[i]);

                zos.putNextEntry(new ZipEntry(files[i].getName()));

                int length;

                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        }
        catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }

        InputStream file = new FileInputStream(zipFile);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".zip" + "\"");

        int readBytes = 0;
        byte[] toDownload = new byte[100];
        OutputStream downloadStream = response.getOutputStream();

        while((readBytes = file.read(toDownload))!= -1){
            downloadStream.write(toDownload, 0, readBytes);
            downloadStream.flush();
        }

        downloadStream.close();
        res++;

        return res;
    }
}
