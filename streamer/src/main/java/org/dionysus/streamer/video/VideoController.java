package org.dionysus.streamer.video;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @GetMapping(path="/{id}")
    //@Async(AsyncConfig.ASYNC_EXECUTOR)
    // I don't know how to make the async :(
    public void getVideo(@PathVariable String id,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        Video video = this.videoRepository.findById(id).block();
        logger.info("Call to video for {}", video);
        if(video == null) {
            //logger.info("User requested video {} that doesn't exist", id);
            //response.sendError(HttpStatus.NOT_FOUND.value(), "Video not found");
        }
        Path videoPath = video == null ? Paths.get("/home/cfogrady/Hook.mp4") : Paths.get(video.getPath());
        try {
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
