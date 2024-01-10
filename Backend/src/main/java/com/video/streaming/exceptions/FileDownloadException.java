package com.video.streaming.exceptions;

public class FileDownloadException extends SpringBootFileUploadException{
    public FileDownloadException(String message) {
        super(message);
    }
}
