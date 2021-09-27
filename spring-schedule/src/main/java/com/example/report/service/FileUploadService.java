package com.example.report.service;

import com.example.report.mapper.FileMapper;
import com.example.report.util.ApiException;
import lombok.RequiredArgsConstructor;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.apache.ibatis.session.SqlSessionFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.example.report.util.ErrorCodes.INTERNAL_ERROR;

/**
 * 업로드 관련 서비스 Static 하게 생성한 Service 클래스를 사용한다.
 */

@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final FileMapper fileMapper;

    private final TusFileUploadService tusFileUploadService;

    private SqlSessionFactory sqlsessionFactory;

    @Value("${tus.server.data.directory}")
    private Path tusPath;

    @Value("${tus.server.data.directory}")
    protected String tusPaths;

    @Value("${file.chunk-size}")
    private Integer chunkSize;

    @Value("${file.download-path}")
    private Path downloadDir;

    public Integer tus(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse)
            throws ApiException {
        Integer res = 0;

        try {
            tusFileUploadService.process(servletRequest, servletResponse);

            final String uri = servletRequest.getRequestURI();
            UploadInfo uploadInfo = uri2info(uri);

            // https://github.com/tomdesair/tus-java-server/issues/20
            if(null != uploadInfo && !uploadInfo.isUploadInProgress()) {
                InputStream uploadedBytes = tusFileUploadService.getUploadedBytes(uri);

                Path newFile = Paths.get(tusPaths, uploadInfo.getFileName());
                Files.deleteIfExists(newFile);
                Files.copy(
                        uploadedBytes,
                        newFile,
                        StandardCopyOption.REPLACE_EXISTING);

                //Since we're done with processing the upload, we can safely remove it now
                tusFileUploadService.deleteUpload(uri);
            }

            res++;
        } catch(Exception ex) {
            throw new ApiException(INTERNAL_ERROR);
        }

        return res;
    }

    private UploadInfo uri2info(String uri){
        UploadInfo uploadInfo = null;
        try {
            uploadInfo = this.tusFileUploadService.getUploadInfo(uri);
        }
        catch (IOException | TusException ex) {
            System.out.println(ex.getMessage());
        }
        return uploadInfo;
    }

}
