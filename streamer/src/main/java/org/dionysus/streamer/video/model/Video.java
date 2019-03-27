package org.dionysus.streamer.video.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dionysus.streamer.video.VideoRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection= VideoRepository.VIDEO_COLLECTION)
@TypeAlias("Video")
public class Video {
    @Id
    private String id;

    private String name;

    private Rating rating;

    private String parentId;

    @JsonIgnore
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    @JsonIgnore
    public void setPath(String path) {
        this.path = path;
    }

    @JsonGetter("groupContainer")
    public boolean isGroupContainer() {
        return path == null;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", parentId='" + parentId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
