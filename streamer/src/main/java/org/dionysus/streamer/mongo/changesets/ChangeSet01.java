package org.dionysus.streamer.mongo.changesets;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.dionysus.streamer.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ChangeLog(order = "001")
public class ChangeSet01 {

    private static Logger logger = LoggerFactory.getLogger(ChangeSet01.class);

    @ChangeSet(order = "001", id = "createUserCollection", author = "cfogrady")
    public void createUserCollection(MongoDatabase db){
        logger.info("Test that we create the user collection");
        db.createCollection("user");
    }

    @ChangeSet(order = "002", id = "createUsernameIndex", author = "cfogrady")
    public void createUsernameIndex(MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection(UserRepository.USER_COLLECTION);
        Document index = new Document("credentials.username", 1);
        IndexOptions options = new IndexOptions().unique(true);
        collection.createIndex(index, options);
    }

    @ChangeSet(order = "003", id = "createDefaultUser", author = "cfogrady")
    public void createDefaultUser(MongoDatabase db) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        MongoCollection<Document> collection = db.getCollection(UserRepository.USER_COLLECTION);
        Document credentials = new Document("username", "admin").append("password", bCryptPasswordEncoder.encode("tempPassword"));
        collection.insertOne(new Document("credentials", credentials).append("_class", "User"));
    }
}
