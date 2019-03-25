package org.dionysus.streamer.video;

import org.dionysus.streamer.video.model.Video;
import org.dionysus.streamer.video.model.VideoScanRequest;
import org.dionysus.streamer.video.model.VideoScanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoRepository videoRepository;

    @Inject
    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping(path="/group/{id}")
    public Flux<Video> getVideoGroup(@PathVariable String id) {
        return this.videoRepository.findByParentId(id);
    }

    @GetMapping(path="/group")
    public Flux<Video> getRootVideoGroup() {
        return getVideoGroup(null);
    }

    @PostMapping
    public Mono<String> postVideoMetadata(@RequestBody Video video) {
        return this.videoRepository.insert(video).map(Video::getId);
    }

    @PutMapping
    public Mono<Video> setVideoMetadata(@RequestBody Video video) {
        return this.videoRepository.save(video);
    }

    @GetMapping(path="/{id}")
    public Mono<Video> getVideoMetadata(@PathVariable String id) {
        return this.videoRepository.findById(id);
    }

    @PostMapping(path="/scan")
    public Mono<VideoScanResponse> scanForVideos(@RequestBody VideoScanRequest videoScanRequest) {
        return null;
    }

    @GetMapping(path="/stream/{id}", produces="video/mp4")
    public Mono<ResponseEntity<Resource>> getVideo(@PathVariable String id) {
        return this.videoRepository.findById(id).switchIfEmpty(Mono.error(() -> {
            logger.info("User requested video {} that doesn't exist", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND,  "Video not found for id " + id);
        })).map(video -> {
            Path videoPath = Paths.get(video.getPath());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            return new ResponseEntity<>(new FileSystemResource(videoPath), headers, HttpStatus.OK );
        });
    }
}
