package org.dionysus.streamer.mongo;

import com.github.mongobee.Mongobee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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
        String hosts = StringUtils.collectionToCommaDelimitedString(mongoConfig.getHosts());
        String dbName = mongoConfig.getDbName();
        logger.info("Hosts: {}", hosts);
        Mongobee runner = new Mongobee("mongodb://" + hosts + "/" + dbName);
        runner.setDbName(dbName);         // host must be set if not set in URI
        runner.setChangeLogsScanPackage(
                "org.dionysus.streamer.mongo.changesets"); // the package to be scanned for changesets

        return runner;
    }
}
