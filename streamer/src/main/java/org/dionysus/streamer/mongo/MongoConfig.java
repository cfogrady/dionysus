package org.dionysus.streamer.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.util.StringUtils;

import java.util.List;

@ConfigurationProperties(prefix="mongo")
@Configuration
@EnableReactiveMongoRepositories("org.dionysus.streamer")
public class MongoConfig {

    private List<String> hosts;

    private String dbName;

    private String username;

    private String password;

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String buildMongoDBURIString() {
        String hosts = StringUtils.collectionToCommaDelimitedString(getHosts());
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("mongodb://");
        if(!org.apache.commons.lang3.StringUtils.isBlank(getUsername())) {
            strBuilder.append(getUsername());
            if(!org.apache.commons.lang3.StringUtils.isBlank(getPassword())) {
                strBuilder.append(":").append(getPassword());
            }
            strBuilder.append("@");
        }
        strBuilder.append(hosts).append("/").append(getDbName());
        return strBuilder.toString();
    }
}
