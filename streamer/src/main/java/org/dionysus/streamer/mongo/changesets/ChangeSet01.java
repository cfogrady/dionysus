package org.dionysus.streamer.mongo.changesets;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        MongoCollection<Document> collection = db.getCollection("user");
        Document index = new Document("username", 1);
        IndexOptions options = new IndexOptions().unique(true);
        collection.createIndex(index, options);
    }
}
