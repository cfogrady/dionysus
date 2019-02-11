package org.dionysus.streamer.user;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection="user")
public class User {
    @Id
    private String id;
    @NotNull
    private String username;
    private String hashedPassword;
}
