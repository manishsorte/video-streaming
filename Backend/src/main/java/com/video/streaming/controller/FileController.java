package com.video.streaming.controller;

import com.video.streaming.Constants.Constants;
import com.video.streaming.exceptions.FileDownloadException;
import com.video.streaming.exceptions.FileEmptyException;
import com.video.streaming.model.Video;
import com.video.streaming.model.VideoDto;
import com.video.streaming.responses.APIResponse;
import com.video.streaming.service.FileService;
import com.video.streaming.service.VideoService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class FileController {
    private final FileService fileService;
    private final VideoService videoService;

    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile multipartFile) throws FileEmptyException, IOException {
        if (multipartFile.isEmpty()) {
            throw new FileEmptyException(Constants.FILE_IS_EMPTY);
        }
        boolean isValidFile = isValidFile(multipartFile);
        List<String> allowedFileExtensions = new ArrayList<>(Arrays.asList("mp4", "png", "jpg", "jpeg"));
        return uploadFiles(isValidFile, multipartFile, allowedFileExtensions);
    }

    @PostMapping("/upload/thumbnail")
    public ResponseEntity<?> uploadThumbnail(@RequestParam("thumbnail") MultipartFile multipartFile, String videoId) throws FileEmptyException, IOException {
        if (multipartFile.isEmpty()) {
            throw new FileEmptyException(Constants.FILE_IS_EMPTY);
        }
        boolean isValidFile = isValidFile(multipartFile);
        List<String> allowedFileExtensions = new ArrayList<>(Arrays.asList("png", "jpg", "jpeg"));
        if (isValidFile && allowedFileExtensions.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
            APIResponse apiResponse = videoService.uploadThumbnail(multipartFile, videoId);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            APIResponse apiResponse = new APIResponse(null, null,Constants.INVALID_FILE,
                    400, false);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/video/edit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Video editVideoDetails(@RequestBody VideoDto videoDto) {
        return videoService.editVideoMetaData(videoDto);
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName") @NotBlank @NotNull String fileName) throws FileDownloadException, IOException {
        Object response = fileService.downloadFile(fileName);
        if (response != null) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(response);
        } else {
            APIResponse apiResponse = APIResponse.builder()
                    .message("File could not be downloaded")
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("fileName") @NotBlank @NotNull String fileName) {
        boolean isDeleted = fileService.delete(fileName);
        if (isDeleted) {
            APIResponse apiResponse = APIResponse.builder().message("file deleted!")
                    .statusCode(200).build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            APIResponse apiResponse = APIResponse.builder().message("file does not exist")
                    .statusCode(404).build();
            return new ResponseEntity<>("file does not exist", HttpStatus.NOT_FOUND);
        }
    }

    private boolean isValidFile(MultipartFile multipartFile) {
        log.info("Empty Status ==> {}", multipartFile.isEmpty());
        if (Objects.isNull(multipartFile.getOriginalFilename())) {
            return false;
        }
        return !multipartFile.getOriginalFilename().trim().equals("");
    }

    private ResponseEntity<?> uploadFiles(boolean isValidFile, MultipartFile multipartFile, List<String> extensions) throws IOException {
        if (isValidFile && extensions.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
            APIResponse apiResponse = videoService.uploadVideo(multipartFile);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            APIResponse apiResponse = new APIResponse(null, null, Constants.INVALID_FILE,
                    400, false);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
