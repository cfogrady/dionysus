package org.dionysus.streamer.user.model;


import org.dionysus.streamer.user.UserRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Document(collection= UserRepository.USER_COLLECTION)
@TypeAlias("User")
public class User {
    @Id
    private String id;
    @NotNull
    private UserCredentials credentials;

    public User(String id, @Nonnull UserCredentials credentials) {
        this.id = id;
        this.credentials = credentials;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(@Nonnull UserCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                credentials.equals(user.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, credentials);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", credentials=" + credentials +
                '}';
    }
}
