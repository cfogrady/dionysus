package org.dionysus.streamer.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Configuration
@Singleton
public class SpringMongoConfig extends AbstractReactiveMongoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(SpringMongoConfig.class);

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
