package com.video.streaming.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.video.streaming.Constants.Constants;
import com.video.streaming.model.Video;
import com.video.streaming.model.VideoDto;
import com.video.streaming.repository.VideoRepository;
import com.video.streaming.responses.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final AmazonS3 s3Client;
    @Value("${aws.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        // converting multipart file  to a file
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(multipartFile.getBytes());
        }

        // generating file name
        String fileName = generateFileName(multipartFile);

        // uploading file to S3
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.addUserMetadata("Title", "File Upload - " + fileName);
        metadata.setContentLength(file.length());
        request.setMetadata(metadata);
        s3Client.putObject(request);

        // delete file
        file.delete();
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public APIResponse uploadVideo(MultipartFile file) throws IOException {
        String fileName = uploadFile(file);
        Video video = new Video();
        video.setVideoUrl(fileName);
        videoRepository.save(video);
        return new APIResponse(video.getId(), fileName, Constants.FILE_UPLOADED_SUCCESSFULLY,
                200, true);
    }

    public APIResponse uploadThumbnail(MultipartFile file, String videoId) throws IOException {
        Video retrievedVideo = getVideoById(videoId);
        String fileName = uploadFile(file);
        retrievedVideo.setThumbnailUrl(fileName);
        videoRepository.save(retrievedVideo);
        return new APIResponse(retrievedVideo.getId(), fileName, Constants.FILE_UPLOADED_SUCCESSFULLY,
                200, true);
    }

    public Video editVideoMetaData(VideoDto videoDetails) {
        //retrieving file from database
        Video retrievedVideo = getVideoById(videoDetails.getId());
        //updating video details
        retrievedVideo.setDescription(videoDetails.getDescription());
        retrievedVideo.setTags(videoDetails.getTags());
        retrievedVideo.setTitle(videoDetails.getTitle());
        retrievedVideo.setThumbnailUrl(videoDetails.getThumbnailUrl());
        retrievedVideo.setVideoStatus(videoDetails.getVideoStatus());
        videoRepository.save(retrievedVideo);
        return retrievedVideo;
    }

    Video getVideoById(String videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by id - " + videoId));
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}
