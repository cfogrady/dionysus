package org.dionysus.streamer.video.repository;

import org.dionysus.streamer.video.model.Video;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface VideoRepository extends ReactiveMongoRepository<Video, String>, VideoRepositoryCusomization {
    String VIDEO_COLLECTION = "video";

    Flux<Video> findByParentId(String parentId);
}
