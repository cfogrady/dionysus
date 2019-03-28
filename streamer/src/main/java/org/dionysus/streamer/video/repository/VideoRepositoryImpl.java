package org.dionysus.streamer.video.repository;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoRepositoryImpl implements VideoRepositoryCusomization {
    private static Logger logger = LoggerFactory.getLogger(VideoRepositoryImpl.class);

    @Override
    public String generateId() {
        return new ObjectId().toHexString();
    }
}
