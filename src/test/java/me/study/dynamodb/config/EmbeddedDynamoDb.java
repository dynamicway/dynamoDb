package me.study.dynamodb.config;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
class EmbeddedDynamoDb {
    private DynamoDBProxyServer server;

    @PostConstruct
    public void start() throws Exception {
        if (server != null) {
            return;
        }

        EmbeddedDynamoDbUtils.initSqLite();
        server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory"});
        server.start();
    }

    @PreDestroy
    public void stop() throws Exception {
        if (server == null) {
            return;
        }
        server.stop();
    }

}
