package org.dionysus.streamer.video;

import com.google.common.collect.Lists;
import org.dionysus.streamer.video.model.Video;
import org.dionysus.streamer.video.model.VideoScanRequest;
import org.dionysus.streamer.video.model.VideoScanResponse;
import org.dionysus.streamer.video.repository.VideoRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public class VideoControllerTest {
    private static Logger logger = LoggerFactory.getLogger(VideoControllerTest.class);

    private VideoController videoController;

    @Mock
    private VideoRepository videoRepository;
    @Mock
    private FileSystemWrapper fileSystemWrapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        setupMockPaths();
        Mockito.when(videoRepository.insert(Mockito.any(Flux.class))).then(answer -> {
            return (Flux<Video>)answer.getArguments()[0];
        });
        this.videoController = new VideoController(videoRepository, fileSystemWrapper);
    }

    public void setupMockPaths() {
        Path searchPath = Mockito.mock(Path.class);
        Mockito.when(fileSystemWrapper.getPath(Mockito.anyString())).thenReturn(searchPath);
        Stream<Path> searchPathChildren = Lists.newArrayList(setupPath(false, "Test.mp4"),
                setupPath(false, "Test2.mp4"),
                setupPath(false, "Test.avi")).stream();
        Mockito.when(fileSystemWrapper.listDirectoryContents(searchPath))
                .thenReturn(searchPathChildren);
    }

    public Path setupPath(boolean isDirectory, String name) {
        Path path = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(path.toFile()).thenReturn(file);
        Mockito.when(file.getName()).thenReturn(name);
        Mockito.when(file.isDirectory()).thenReturn(isDirectory);
        return path;
    }

    @Test
    public void testThatScanForVideoAddsAllVideos()
    {
        VideoScanRequest request = new VideoScanRequest("/");
        Mono<VideoScanResponse> responseStream = this.videoController.scanForVideos(request);
        VideoScanResponse response = responseStream.block();
        responseStream.subscribe();
        Mockito.verify(videoRepository, Mockito.times(1))
                .insert(Mockito.any(Publisher.class));
        Assert.assertThat("Correct number of videos were processed", response.getVideosFiled(), CoreMatchers.equalTo(2));
    }
}
