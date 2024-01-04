package com.video.streaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(value = "Video")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    private String id;
    private String title;
    private String description;
    private Integer like;
    private Integer dislike;
    private String videoUrl;
    private String thumbnailUrl;
    private Set<String> tags;
    private VideoStatus videoStatus;
    private String viewsCount;
    private List<Comment> commentList;
}
