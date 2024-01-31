package com.video.streaming.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class APIResponse {
    private String id;
    private String url;
    private String message;
    private int statusCode;
    private boolean isSuccessful;
}
