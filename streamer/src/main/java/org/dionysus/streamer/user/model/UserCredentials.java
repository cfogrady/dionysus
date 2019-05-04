package org.dionysus.streamer.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class UserCredentials {
    private static Logger logger = LoggerFactory.getLogger(UserCredentials.class);

    @NotNull
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public UserCredentials(@JsonProperty("username") String username,
                           @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) String password) {
        this.username = username;
        this.password = password;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(@Nonnull String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCredentials that = (UserCredentials) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
