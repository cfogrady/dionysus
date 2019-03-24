package org.dionysus.streamer.video;

import org.apache.catalina.connector.ClientAbortException;
import org.dionysus.streamer.video.model.Video;
import org.dionysus.streamer.video.model.VideoScanRequest;
import org.dionysus.streamer.video.model.VideoScanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoRepository videoRepository;
    private final RangeResourceRequestHandler requestHandler;

    @Inject
    public VideoController(VideoRepository videoRepository,
                           RangeResourceRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        this.videoRepository = videoRepository;
    }

    @GetMapping(path="/group/{id}")
    public CompletableFuture<List<Video>> getVideoGroup(@PathVariable String id) {
        CompletableFuture<List<Video>> response = new CompletableFuture<>();
        this.videoRepository.findByParentId(id).collectList().subscribe(videoList -> {
            response.complete(videoList);
        });
        return response;
    }

    @GetMapping(path="/group")
    public CompletableFuture<List<Video>> getRootVideoGroup() {
        return getVideoGroup(null);
    }

    @PostMapping
    public CompletableFuture<String> postVideoMetadata(@RequestBody Video video) {
        CompletableFuture<String> response = new CompletableFuture<>();
        this.videoRepository.insert(video).subscribe(insertedVideo -> {
            response.complete(insertedVideo.getId());
        });
        return response;
    }

    @PostMapping(path="/scan")
    public CompletableFuture<VideoScanResponse> scanForVideos(@RequestBody VideoScanRequest videoScanRequest) {
        CompletableFuture<VideoScanResponse> response = new CompletableFuture<>();
        return response;
    }

    @GetMapping(path="/stream/{id}")
    //@Async(AsyncConfig.ASYNC_EXECUTOR)
    // I don't know how to make this async :(
    public void getVideo(@PathVariable String id,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        Video video = this.videoRepository.findById(id).block();
        logger.info("Call to video for {}", video);
        try {
            if(video == null) {
                logger.info("User requested video {} that doesn't exist", id);
                response.sendError(HttpStatus.NOT_FOUND.value(), "Video not found");
            }
            Path videoPath = Paths.get(video.getPath());
            request.setAttribute(RangeResourceRequestHandler.FILE_ATTRIBUTE, videoPath.toFile());
            this.requestHandler.handleRequest(request, response);
        } catch (ServletException se) {
            throw new IllegalStateException(se);
        } catch (ClientAbortException cae){
            logger.debug("Client Aborted. You can never rely on anything anymore", cae);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
