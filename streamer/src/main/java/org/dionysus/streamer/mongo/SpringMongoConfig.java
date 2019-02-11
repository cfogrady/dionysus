package org.dionysus.streamer.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.util.StringUtils;

import javax.inject.Singleton;

@Singleton
@EnableReactiveMongoRepositories("org.dionysus.streamer")
public class SpringMongoConfig extends AbstractReactiveMongoConfiguration {

    private final MongoConfig mongoConfig;
    private final MongoClient mongoClient;

    public SpringMongoConfig(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
        String hosts = StringUtils.collectionToCommaDelimitedString(mongoConfig.getHosts());
        String dbName = mongoConfig.getDbName();
        String mongoURI = "mongodb://" + hosts + "/" + dbName;
        ConnectionString connectionString = new ConnectionString(mongoURI);
        this.mongoClient = MongoClients.create(connectionString);
    }

    @Override
    protected String getDatabaseName() {
        return this.mongoConfig.getDbName();
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return this.mongoClient;
    }
}
