package org.dionysus.streamer.mongo;

import com.github.mongobee.Mongobee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Configuration
public class MongoBeeConfiguration {

    private final MongoConfig mongoConfig;

    private static Logger logger = LoggerFactory.getLogger(MongoBeeConfiguration.class);

    @Inject
    public MongoBeeConfiguration(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    @Bean
    public Mongobee mongobee() {
        Mongobee runner = new Mongobee(mongoConfig.buildMongoDBURIString());
        runner.setDbName(mongoConfig.getDbName());         // host must be set if not set in URI
        runner.setChangeLogsScanPackage(
                "org.dionysus.streamer.mongo.changesets"); // the package to be scanned for changesets

        return runner;
    }
}
