package org.dionysus.streamer.video.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoScanResponse {
    private static Logger logger = LoggerFactory.getLogger(VideoScanResponse.class);

    public static final String VIDEOS_FILED = "videosFiled";

    @JsonProperty(VIDEOS_FILED)
    public final int videosFiled;

    @JsonCreator
    public VideoScanResponse(@JsonProperty(VIDEOS_FILED) int videosFiled) {
        this.videosFiled = videosFiled;
    }

    public int getVideosFiled() {
        return videosFiled;
    }
}
