package org.dionysus.streamer.video;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends ReactiveMongoRepository<Video, String> {
    String VIDEO_COLLECTION = "video";

}
