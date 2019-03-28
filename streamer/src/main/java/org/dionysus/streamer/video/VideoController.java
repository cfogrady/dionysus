package org.dionysus.streamer.video;

import org.dionysus.streamer.video.model.Video;
import org.dionysus.streamer.video.model.VideoScanRequest;
import org.dionysus.streamer.video.model.VideoScanResponse;
import org.dionysus.streamer.video.repository.VideoRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoRepository videoRepository;
    private final FileSystemWrapper fileSystemWrapper;

    @Inject
    public VideoController(VideoRepository videoRepository,
                           FileSystemWrapper fileSystemWrapper) {
        this.videoRepository = videoRepository;
        this.fileSystemWrapper = fileSystemWrapper;
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

    /**
     * Treats any files in the directory passed in as movies. Groups all files by their respective directory structure.
     */
    @PostMapping(path="/scan")
    public Mono<VideoScanResponse> scanForVideos(@RequestBody VideoScanRequest videoScanRequest) {
        Publisher<Video> publisher = scanForVideos(videoScanRequest.getRootPath());
        return videoRepository.insert(publisher).reduce(0, (count, video) -> {
            if(video.isGroupContainer()) {
                return count;
            }
            return count+1;
        }).map(VideoScanResponse::new);
    }

    private Flux<Video> scanForVideos(String pathStr) {
        return Flux.create(fluxSink -> {
            Path searchPath = this.fileSystemWrapper.getPath(pathStr).normalize();
            Map<Path, String> parentsToIds = new HashMap<>();
            Queue<Path> pathsToProcess = fileSystemWrapper.listDirectoryContents(searchPath).collect(Collectors.toCollection(LinkedList::new));
            while(!pathsToProcess.isEmpty()) {
                Path path = pathsToProcess.remove();
                File file = path.toFile();
                String name = file.getName();
                String parent = parentsToIds.get(path.getParent());
                Video video = new Video();
                video.setId(videoRepository.generateId());
                video.setParentId(parent);
                if(file.isDirectory()) {
                    parentsToIds.put(path, video.getId());
                    pathsToProcess.addAll(fileSystemWrapper.listDirectoryContents(path).collect(Collectors.toList()));
                } else {
                    video.setPath(path.toString());
                    if(name.contains(".mp4")) {
                        name = name.split(".")[0];
                    } else {
                        continue;
                    }
                }
                video.setName(name);
                fluxSink.next(video);
            }
            fluxSink.complete();
        });
    }

    @GetMapping(path="/stream/{id}", produces="video/mp4")
    public Mono<ResponseEntity<Resource>> getVideo(@PathVariable String id) {
        return this.videoRepository.findById(id).switchIfEmpty(Mono.error(() -> {
            logger.info("User requested video {} that doesn't exist", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND,  "Video not found for id " + id);
        })).map(video -> {
            if(video.isGroupContainer()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request stream for a group of videos. Streams must be for a single video");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            return new ResponseEntity<>(fileSystemWrapper.buildFileSystemResource(video.getPath()), headers, HttpStatus.OK );
        });
    }
}
