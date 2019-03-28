package org.dionysus.streamer.video.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

public class VideoScanRequest {
    private static Logger logger = LoggerFactory.getLogger(VideoScanRequest.class);

    public static final String ROOT_PATH = "rootPath";

    @JsonProperty(ROOT_PATH)
    private final String rootPath;

    @JsonCreator
    public VideoScanRequest(@JsonProperty(ROOT_PATH) @NotNull String rootPath) {
        this.rootPath = rootPath;
    }

    public @NotNull String getRootPath() {
        return rootPath;
    }
}
