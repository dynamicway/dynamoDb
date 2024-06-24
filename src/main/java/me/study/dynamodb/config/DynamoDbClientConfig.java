package me.study.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DynamoDbClientConfig {

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                                     .dynamoDbClient(dynamoDbClient)
                                     .build();
    }

    @Bean
    public DynamoDbClient testDynamoDbClient() throws URISyntaxException {
        return DynamoDbClient.builder()
                             .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                             .endpointOverride(new URI("http://localhost:8000"))
                             .build();
    }
}

