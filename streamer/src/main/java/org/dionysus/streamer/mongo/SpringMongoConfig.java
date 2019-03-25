package org.dionysus.streamer.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import javax.inject.Inject;

@Configuration
@EnableReactiveMongoRepositories("org.dionysus.streamer")
public class SpringMongoConfig extends AbstractReactiveMongoConfiguration {

    private final MongoConfig mongoConfig;
    private final MongoClient mongoClient;

    @Inject
    public SpringMongoConfig(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
        String mongoURI = mongoConfig.buildMongoDBURIString();
        ConnectionString connectionString = new ConnectionString(mongoURI);
        this.mongoClient = MongoClients.create(connectionString);
    }

    @Override
    protected String getDatabaseName() {
        return this.mongoConfig.getDbName();
    }

    @Override
    public MongoClient reactiveMongoClient()
    {
        return this.mongoClient;
    }
}
