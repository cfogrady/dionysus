package org.dionysus.streamer.video;

import org.dionysus.streamer.util.MultipartFileSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@RestController
@Singleton
@RequestMapping("/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoRepository videoRepository;

    @Inject
    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping(path="/{id}", produces = "application/json")
    public CompletableFuture<Void> getUser(@PathVariable String id,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.videoRepository.findById(id).doOnSuccess(video -> {
            logger.info("Call to video for {}", video);
            Path path = Paths.get("/home/cfogrady/dev/dionysus/small.mp4");
            MultipartFileSender.fromPath(path).with(request).with(response).serveResource();
            future.complete(null);
        }).subscribe();
        return future;
    }
}
