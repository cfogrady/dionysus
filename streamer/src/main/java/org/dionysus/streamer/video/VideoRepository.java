package org.dionysus.streamer.video;

import org.dionysus.streamer.video.model.Video;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface VideoRepository extends ReactiveMongoRepository<Video, String> {
    String VIDEO_COLLECTION = "video";

    Flux<Video> findByParentId(String parentId);
}
