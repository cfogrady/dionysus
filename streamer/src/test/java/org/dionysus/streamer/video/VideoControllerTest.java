package org.dionysus.streamer.video;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.dionysus.streamer.exception.BadRequestException;
import org.dionysus.streamer.exception.NotFoundException;
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
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.videoController = new VideoController(videoRepository, fileSystemWrapper);
    }

    private void setupMockPaths() {
        Path searchPath = Mockito.mock(Path.class);
        Mockito.when(searchPath.toString()).thenReturn("/search/path");
        Mockito.when(searchPath.normalize()).thenReturn(searchPath);
        Mockito.when(fileSystemWrapper.getPath(Mockito.anyString())).thenReturn(searchPath);
        Path dir1 = setupPath(true, "dir1", searchPath);
        List<Path> dir1Children = Lists.newArrayList(setupPath(false, "S01E01.mp4", dir1),
                setupPath(false, "S01E02.mp4", dir1));
        Mockito.when(fileSystemWrapper.listDirectoryContents(dir1)).thenAnswer(invocation -> dir1Children.stream());
        Path dir2 = setupPath(true, "dir2", searchPath);
        Path dir2Sp = setupPath(true, "dir2Specials", dir2);
        List<Path> dir2SpChildren = Lists.newArrayList(setupPath(false, "Special1.mp4", dir2Sp),
                setupPath(false, "Special2.mp4", dir2Sp));
        Mockito.when(fileSystemWrapper.listDirectoryContents(dir2Sp)).thenAnswer(invocation -> dir2SpChildren.stream());
        List<Path> dir2Children = Lists.newArrayList(setupPath(false, "S02E01.mp4", dir2),
                setupPath(false, "S02E02.mp4", dir2),
                dir2Sp);
        Mockito.when(fileSystemWrapper.listDirectoryContents(dir2)).thenAnswer(invocation -> dir2Children.stream());
        List<Path> searchPathChildren = Lists.newArrayList(setupPath(false, "Test.mp4", searchPath),
                setupPath(false, "Test2.mp4", searchPath),
                setupPath(false, "Test.avi", searchPath),
                dir1,
                dir2);
        Mockito.when(fileSystemWrapper.listDirectoryContents(searchPath))
                .thenAnswer(invocation -> searchPathChildren.stream());
    }

    private Path setupPath(boolean isDirectory, String name, Path parent) {
        Path path = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(path.toFile()).thenReturn(file);
        Mockito.when(file.getName()).thenReturn(name);
        Mockito.when(file.isDirectory()).thenReturn(isDirectory);
        Mockito.when(path.getParent()).thenReturn(parent);
        return path;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testThatScanForVideoAddsAllVideos()
    {
        Map<String, Video> videosByName = new HashMap<>();
        Mockito.when(videoRepository.insert(Mockito.any(Flux.class))).then(answer ->
                ((Flux<Video>)answer.getArgument(0)).doOnEach(videoSignal -> {
                    Video video = videoSignal.get();
                    if(video != null) {
                        videosByName.put(video.getName(), video);
                    }
                })
        );
        Mockito.when(videoRepository.generateId()).then(inv -> new ObjectId().toHexString());
        setupMockPaths();
        VideoScanRequest request = new VideoScanRequest("/");
        Mono<VideoScanResponse> responseStream = this.videoController.scanForVideos(request);
        VideoScanResponse response = responseStream.block();
        responseStream.subscribe();
        Mockito.verify(videoRepository, Mockito.times(1))
                .insert(Mockito.any(Publisher.class));
        Assert.assertThat("Response is not null", response, CoreMatchers.notNullValue());
        Assert.assertThat("Correct number of videos were processed", response.getVideosFiled(), CoreMatchers.equalTo(8));
        Assert.assertThat("Files in Dir1 have it as parent",
                videosByName.get("S01E02").getParentId(),
                CoreMatchers.equalTo(videosByName.get("dir1").getId()));
        Assert.assertThat("Root files have no parent",
                videosByName.get("Test2").getParentId(),
                CoreMatchers.nullValue());
        Assert.assertThat("SubDir2 has Dir2 as parent",
                videosByName.get("dir2Specials").getParentId(),
                CoreMatchers.equalTo(videosByName.get("dir2").getId()));
        Assert.assertThat("SubDir2 has Dir2 as parent",
                videosByName.get("dir2Specials").getParentId(),
                CoreMatchers.equalTo(videosByName.get("dir2").getId()));
        Assert.assertThat("SubDir2 files has SubDir2 as parent",
                videosByName.get("Special2").getParentId(),
                CoreMatchers.equalTo(videosByName.get("dir2Specials").getId()));
        videosByName.values().forEach(video ->
                Assert.assertThat("Video " + video.getName() + " has id",
                        video.getId(),
                        CoreMatchers.notNullValue())
        );
    }

    @Test
    public void getVideoSetsHeader() {
        String id = "testId";
        Video video = new Video();
        video.setName("File");
        video.setPath("/i/am/a/path/file.mp4");
        video.setId(id);
        Mockito.when(videoRepository.findById(id)).thenReturn(Mono.just(video));
        Mono<ResponseEntity<Resource>> resultStream = videoController.getVideo(id);
        ResponseEntity<Resource> result = resultStream.block();
        Assert.assertThat("Result is present", result, CoreMatchers.notNullValue());
        Assert.assertThat("Headers are present", result.getHeaders(), CoreMatchers.notNullValue());
        Assert.assertThat("Content-Header is set", result.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), CoreMatchers.equalTo("video/mp4"));
    }

    @Test(expected = BadRequestException.class)
    public void getVideoOnDirectoryThrows() {
        String id = "testId";
        Video video = new Video();
        video.setName("Directory");
        video.setId(id);
        Mockito.when(videoRepository.findById(id)).thenReturn(Mono.just(video));
        Mono<ResponseEntity<Resource>> resultStream = videoController.getVideo(id);
        resultStream.block();
    }

    @Test(expected = NotFoundException.class)
    public void getMissingVideoThrows() {
        String id = "testId";
        Mockito.when(videoRepository.findById(id)).thenReturn(Mono.empty());
        Mono<ResponseEntity<Resource>> resultStream = videoController.getVideo(id);
        resultStream.block();
    }
}
