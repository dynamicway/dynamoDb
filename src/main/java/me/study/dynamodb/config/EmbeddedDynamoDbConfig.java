package me.study.dynamodb.config;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;


@Configuration
class EmbeddedDynamoDbConfig {
    private DynamoDBProxyServer server;

    @PostConstruct
    public void start() {
        if (server != null) {
            return;
        }

        try {
            AwsDynamoDbLocalTestUtils.initSqLite();
            server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory"});
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException("DynamoDB Failed to Start", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (server == null) {
            return;
        }

        try {
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("DynamoDB Failed to Stop", e);
        }
    }

}
