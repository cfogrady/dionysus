package org.dionysus.streamer.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UserCredentialsTest {
    private static Logger logger = LoggerFactory.getLogger(UserCredentialsTest.class);

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatPasswordDeserializesOnly() throws IOException {
        String json = "{ \"username\": \"test\", \"password\": \"testpass\" }";
        UserCredentials creds = objectMapper.readValue(json, UserCredentials.class);
        Assert.assertThat("Object has password", creds.getPassword(), CoreMatchers.equalTo("testpass"));
        Assert.assertThat("Object has username", creds.getUsername(), CoreMatchers.equalTo("test"));
        json = objectMapper.writeValueAsString(creds);
        logger.info("Serialize: {}", json);
        creds = objectMapper.readValue(json, UserCredentials.class);
        Assert.assertThat("Object is now missing password", creds.getPassword(), CoreMatchers.nullValue());

    }
}
