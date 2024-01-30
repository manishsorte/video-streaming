package com.video.streaming.service;

import com.video.streaming.exceptions.FileDownloadException;
import com.video.streaming.exceptions.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    Object downloadFile(String fileName) throws FileDownloadException, IOException;

    boolean delete(String fileName);
}
